package com.jarirahmed.hibernate;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Counts SQL statements so the N+1 example has observable evidence. */
public final class HibernateSqlCounter implements StatementInspector {
    private final List<String> statements = new CopyOnWriteArrayList<>();

    @Override
    public String inspect(String sql) {
        if (sql != null && !sql.isBlank()) {
            statements.add(sql);
        }
        return sql;
    }

    public void reset() {
        statements.clear();
    }

    public int count() {
        return statements.size();
    }

    public List<String> statements() {
        return List.copyOf(statements);
    }
}
