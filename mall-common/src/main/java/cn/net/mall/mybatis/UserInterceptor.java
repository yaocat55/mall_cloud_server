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
 * @date 2024/3/29 下午2:21
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class UserInterceptor implements Interceptor {

    private static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    private static final String GENERATE_ID = "GENERATE_ID";

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
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (object instanceof MappedStatement) {
                    MappedStatement objectMappedStatement = (MappedStatement) object;
                    if (!objectMappedStatement.getId().equals(mappedStatement.getId())) {
                        continue;
                    }
                    SqlSource sqlSource = objectMappedStatement.getSqlSource();
                    Field field;
                    if (sqlSource instanceof DynamicSqlSource) {
                        field = DynamicSqlSource.class.getDeclaredField("rootSqlNode");
                        field.setAccessible(true);
                        SqlNode rootSqlNode = (SqlNode) field.get(sqlSource);
                        if (!(rootSqlNode instanceof Proxy)) {
                            boolean isInsert = SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
                            SqlNode proxySqlNode = (SqlNode) Proxy.newProxyInstance(rootSqlNode.getClass().getClassLoader(),
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

    private class CustomizeInvocationHandler implements InvocationHandler {
        private final SqlNode target;
        private boolean isInsert;

        CustomizeInvocationHandler(SqlNode target, boolean isInsert) {
            this.target = target;
            this.isInsert = isInsert;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            DynamicContext context = (DynamicContext) args[0];
            JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfoOrNull();
            if (Objects.nonNull(currentUserInfo)) {
                context.bind(CURRENT_USER_ID, currentUserInfo.getId());
                context.bind(CURRENT_USER_NAME, currentUserInfo.getUsername());
            }

            if (isInsert) {
                try {
                    Object idGenerateHelper = SpringUtil.getBean("idGenerateHelper");
                    Long id = (Long) idGenerateHelper.getClass().getMethod("nextId").invoke(idGenerateHelper);
                    context.bind(GENERATE_ID, id);
                } catch (Exception e) {
                    // 项目中无 IdGenerateHelper Bean（未引入 workid starter），跳过自动生成 ID
                }
            }

            return method.invoke(target, args);
        }
    }
}
