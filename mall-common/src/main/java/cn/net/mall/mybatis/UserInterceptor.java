package cn.net.mall.mybatis;


import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.SpringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * MyBatis 更新拦截器 —— 自动注入创建人/修改人信息 和 雪花ID。
 *
 * <h3>工作原理</h3>
 * <ol>
 *   <li>拦截所有 MyBatis {@code Executor.update()} 调用（即 insert / update / delete）</li>
 *   <li>通过 JDK 动态代理包装 SQL 节点（{@link SqlNode}），在 SQL 执行前注入：
 *     <ul>
 *       <li>{@code GENERATE_ID} — 雪花算法 ID（通过 {@link cn.net.mall.workid.IdGenerateHelper#nextId()} 生成）</li>
 *       <li>{@code CURRENT_USER_ID} — 当前登录用户 ID（从 {@link org.springframework.security.core.context.SecurityContextHolder} 获取）</li>
 *       <li>{@code CURRENT_USER_NAME} — 当前登录用户名</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h3>使用方式</h3>
 * 在 mapper XML 的 insert/update SQL 中直接写：
 * <pre>{@code
 * -- insert 时作为字段值
 * #{GENERATE_ID},            -- 自动填充雪花ID（主键）
 * #{CURRENT_USER_ID},        -- 自动填充当前用户ID（create_user_id）
 * #{CURRENT_USER_NAME},      -- 自动填充当前用户名（create_user_name）
 *
 * -- update 时作为字段值
 * update_user_id = #{CURRENT_USER_ID},
 * update_user_name = #{CURRENT_USER_NAME},
 * }</pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li>所有 mapper XML 共享同一套变量名（GENERATE_ID / CURRENT_USER_ID / CURRENT_USER_NAME），
 *       修改变量名需要同步修改所有 XML 文件</li>
 *   <li>如果 {@link cn.net.mall.workid.IdGenerateHelper} Bean 不存在（未启动 Redis），
 *       {@code GENERATE_ID} 不会注入，SQL 执行时可能因主键为空而报错</li>
 *   <li>用户信息从 Spring Security {@link org.springframework.security.core.context.SecurityContextHolder}
 *       获取，Gateway 不传时以匿名用户身份执行，不会报错但 createUser 为空</li>
 * </ul>
 *
 * @date 2024/3/29 下午2:21
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class UserInterceptor implements Interceptor {

    // ========== 注入到 MyBatis 动态 SQL 上下文的变量名 ==========

    /** 雪花算法主键 ID，insert 时自动生成。取值：IdGenerateHelper.nextId() */
    private static final String GENERATE_ID = "GENERATE_ID";

    /** 当前登录用户 ID，insert/update 时自动填充 createUserId / updateUserId */
    private static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    /** 当前登录用户名，insert/update 时自动填充 createUserName / updateUserName */
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";

    /**
     * 拦截入口 —— 只拦截 Executor.update()（即 insert/update/delete）。
     *
     * 核心流程：
     * 1. 获取当前执行的 MappedStatement
     * 2. 如果是 DynamicSqlSource（动态 SQL），用 JDK 动态代理包装其 SqlNode
     * 3. 代理的 SqlNode 在执行前注入 GENERATE_ID / CURRENT_USER_ID / CURRENT_USER_NAME
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();

        if (target instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Collection<MappedStatement> mappedStatements = mappedStatement.getConfiguration().getMappedStatements();
            if (mappedStatements.isEmpty()) {
                return invocation.proceed();
            }
            Iterator<MappedStatement> iterator = mappedStatements.iterator();
            // 遍历所有 MappedStatement，找到与当前执行相同的 ID
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (object instanceof MappedStatement) {
                    MappedStatement objectMappedStatement = (MappedStatement) object;
                    if (!objectMappedStatement.getId().equals(mappedStatement.getId())) {
                        continue;
                    }
                    SqlSource sqlSource = objectMappedStatement.getSqlSource();
                    Field field;
                    // DynamicSqlSource 是 MyBatis 动态 SQL 的实现
                    if (sqlSource instanceof DynamicSqlSource) {
                        // 通过反射拿到 rootSqlNode，这是 SQL 语法树的根节点
                        field = DynamicSqlSource.class.getDeclaredField("rootSqlNode");
                        field.setAccessible(true);
                        SqlNode rootSqlNode = (SqlNode) field.get(sqlSource);
                        if (!(rootSqlNode instanceof Proxy)) {
                            // 判断 SQL 类型：insert 需要注入 GENERATE_ID，update 只需要注入用户信息
                            boolean isInsert = SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
                            SqlNode proxySqlNode = (SqlNode) Proxy.newProxyInstance(
                                    rootSqlNode.getClass().getClassLoader(),
                                    new Class[]{SqlNode.class},
                                    new CustomizeInvocationHandler(rootSqlNode, isInsert));
                            field.set(sqlSource, proxySqlNode);
                        }
                    }
                }

            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * SqlNode 的动态代理处理器。
     *
     * 在 SQL 真正执行前，往 DynamicContext 中绑定三个变量：
     * GENERATE_ID、CURRENT_USER_ID、CURRENT_USER_NAME。
     * 这样 mapper XML 中写 #{GENERATE_ID} 就能取到对应的值。
     */
    private class CustomizeInvocationHandler implements InvocationHandler {
        private final SqlNode target;
        private boolean isInsert;

        CustomizeInvocationHandler(SqlNode target, boolean isInsert) {
            this.target = target;
            this.isInsert = isInsert;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // DynamicContext 是 MyBatis 动态 SQL 的上下文容器，持有所有变量绑定
            DynamicContext context = (DynamicContext) args[0];

            // ===== 注入当前用户信息 =====
            JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfoOrNull();
            if (Objects.nonNull(currentUserInfo)) {
                context.bind(CURRENT_USER_ID, currentUserInfo.getId());
                context.bind(CURRENT_USER_NAME, currentUserInfo.getUsername());
            }

            // ===== insert 时注入雪花 ID =====
            if (isInsert) {
                try {
                    // 从 Spring 容器获取 IdGenerateHelper Bean
                    Object idGenerateHelper = SpringUtil.getBean("idGenerateHelper");
                    Long id = (Long) idGenerateHelper.getClass().getMethod("nextId").invoke(idGenerateHelper);
                    context.bind(GENERATE_ID, id);
                } catch (Exception e) {
                    // IdGenerateHelper 不存在（未启用 Redis / 未引入 workid starter），
                    // 用 Hutool 的雪花算法兜底生成 ID
                    context.bind(GENERATE_ID, cn.hutool.core.util.IdUtil.getSnowflakeNextId());
                }
            }

            return method.invoke(target, args);
        }
    }
}
