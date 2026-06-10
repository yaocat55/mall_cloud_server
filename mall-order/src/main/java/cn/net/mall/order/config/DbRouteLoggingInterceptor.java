package cn.net.mall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DbRouteLoggingInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();
        if (sql == null || sql.isEmpty()) {
            return invocation.proceed();
        }
        String normalized = sql.trim().toLowerCase(Locale.ROOT);
        if (!isSelect(normalized)) {
            return invocation.proceed();
        }
        Connection connection = (Connection) invocation.getArgs()[0];
        DatabaseMetaData meta = connection.getMetaData();
        String url = meta.getURL();
        String dbFromUrl = extractDbNameFromUrl(url);
        String catalog = safe(connection.getCatalog());
        String db = dbFromUrl.isEmpty() ? catalog : dbFromUrl;
        String tables = String.join(",", extractTablesFromSql(normalized));
        String compact = compactSql(sql);
        log.info("DB={} URL={} TABLES={} SQL={}", db, url, tables, compact);
        return invocation.proceed();
    }

    private boolean isSelect(String normalizedSql) {
        return normalizedSql.startsWith("select");
    }

    private String compactSql(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    private String extractDbNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        int idx = url.indexOf("jdbc:mysql://");
        String u = idx >= 0 ? url.substring(idx + "jdbc:mysql://".length()) : url;
        int slash = u.indexOf('/');
        if (slash < 0) {
            return "";
        }
        String rest = u.substring(slash + 1);
        int qm = rest.indexOf('?');
        String db = qm >= 0 ? rest.substring(0, qm) : rest;
        return db == null ? "" : db;
    }

    private Set<String> extractTablesFromSql(String normalizedSql) {
        Set<String> set = new LinkedHashSet<>();
        String s = normalizedSql;
        addTableAfterKeyword(set, s, " from ");
        addTableAfterKeyword(set, s, " join ");
        return set;
    }

    private void addTableAfterKeyword(Set<String> set, String sql, String kw) {
        int from = sql.indexOf(kw);
        while (from >= 0) {
            int start = from + kw.length();
            int end = findTableEnd(sql, start);
            if (end > start) {
                String table = sql.substring(start, end).replaceAll("[`]", "").trim();
                if (!table.isEmpty()) {
                    int dot = table.indexOf('.');
                    set.add(dot > 0 ? table.substring(dot + 1) : table);
                }
            }
            from = sql.indexOf(kw, end);
        }
    }

    private int findTableEnd(String sql, int start) {
        int space = sql.indexOf(' ', start);
        int newline = sql.indexOf('\n', start);
        int tab = sql.indexOf('\t', start);
        int comma = sql.indexOf(',', start);
        int on = sql.indexOf(" on ", start);
        int where = sql.indexOf(" where ", start);
        int group = sql.indexOf(" group ", start);
        int order = sql.indexOf(" order ", start);
        int limit = sql.indexOf(" limit ", start);
        int end = sql.length();
        end = minPos(end, space, newline, tab, comma, on, where, group, order, limit);
        return end < 0 ? sql.length() : end;
    }

    private int minPos(int init, int... arr) {
        int m = init;
        for (int v : arr) {
            if (v >= 0 && v < m) {
                m = v;
            }
        }
        return m;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
