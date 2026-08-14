package api.linlang.file.database.dto;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class QuerySpec {
    private String where;
    private final List<Object> params = new ArrayList<>();
    private String orderBy;
    private int limit;
    private int offset;

    public static QuerySpec of() {
        return new QuerySpec();
    }

    public QuerySpec where(String where) {
        this.where = where;
        return this;
    }

    public QuerySpec param(Object p) {
        this.params.add(p);
        return this;
    }

    public QuerySpec params(List<?> ps) {
        if (ps != null) this.params.addAll(ps);
        return this;
    }

    public QuerySpec orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public QuerySpec limit(int limit) {
        if (limit < 0) throw new IllegalArgumentException("limit must be non-negative");
        this.limit = limit;
        return this;
    }

    public QuerySpec offset(int offset) {
        if (offset < 0) throw new IllegalArgumentException("offset must be non-negative");
        this.offset = offset;
        return this;
    }

    public String where() {
        return where;
    }

    public List<Object> params() {
        return Collections.unmodifiableList(params);
    }

    public String orderBy() {
        return orderBy;
    }

    public int limit() {
        return limit;
    }

    public int offset() {
        return offset;
    }
}
