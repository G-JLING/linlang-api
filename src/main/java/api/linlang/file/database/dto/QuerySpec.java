package api.linlang.file.database.dto;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 可变的参数化查询描述。
 *
 * <p>条件表达式和排序表达式的语法由数据库实现解释。参数应通过
 * {@link #param(Object)} 或 {@link #params(List)} 提供，避免直接拼接到条件中。</p>
 */
public final class QuerySpec {
    private String where;
    private final List<Object> params = new ArrayList<>();
    private String orderBy;
    private int limit;
    private int offset;

    /**
     * @return 新的空查询描述
     */
    public static QuerySpec of() {
        return new QuerySpec();
    }

    /**
     * 设置条件表达式。
     *
     * @param where 条件表达式，可为 {@code null}
     * @return 当前查询描述
     */
    public QuerySpec where(String where) {
        this.where = where;
        return this;
    }

    /**
     * 追加一个条件参数。
     *
     * @param p 参数值
     * @return 当前查询描述
     */
    public QuerySpec param(Object p) {
        this.params.add(p);
        return this;
    }

    /**
     * 按顺序追加多个条件参数。
     *
     * @param ps 参数列表；为 {@code null} 时不执行操作
     * @return 当前查询描述
     */
    public QuerySpec params(List<?> ps) {
        if (ps != null) this.params.addAll(ps);
        return this;
    }

    /**
     * 设置排序表达式。
     *
     * @param orderBy 排序表达式，可为 {@code null}
     * @return 当前查询描述
     */
    public QuerySpec orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * 设置最大返回条数。
     *
     * @param limit 最大条数；零的具体含义由实现决定
     * @return 当前查询描述
     */
    public QuerySpec limit(int limit) {
        if (limit < 0) throw new IllegalArgumentException("limit must be non-negative");
        this.limit = limit;
        return this;
    }

    /**
     * 设置查询偏移量。
     *
     * @param offset 非负偏移量
     * @return 当前查询描述
     */
    public QuerySpec offset(int offset) {
        if (offset < 0) throw new IllegalArgumentException("offset must be non-negative");
        this.offset = offset;
        return this;
    }

    /**
     * @return 条件表达式，可能为 {@code null}
     */
    public String where() {
        return where;
    }

    /**
     * @return 条件参数的不可变视图
     */
    public List<Object> params() {
        return Collections.unmodifiableList(params);
    }

    /**
     * @return 排序表达式，可能为 {@code null}
     */
    public String orderBy() {
        return orderBy;
    }

    /**
     * @return 最大返回条数
     */
    public int limit() {
        return limit;
    }

    /**
     * @return 查询偏移量
     */
    public int offset() {
        return offset;
    }
}
