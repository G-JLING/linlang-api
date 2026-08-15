package api.linlang.file.database.dto;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 不可变的分页查询结果。
 *
 * @param <T> 结果项类型
 */
public final class Page<T> {
    private final List<T> items;
    private final int total;
    private final int offset;

    /**
     * 创建分页结果。
     *
     * @param items 当前页数据；为 {@code null} 时按空列表处理
     * @param total 符合查询条件的总记录数
     * @param offset 当前页相对于完整结果集的起始偏移量
     */
    public Page(List<T> items, int total, int offset) {
        if (total < 0) throw new IllegalArgumentException("total must be non-negative");
        if (offset < 0) throw new IllegalArgumentException("offset must be non-negative");
        this.items = items == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(items));
        this.total = total;
        this.offset = offset;
    }

    /**
     * @return 当前页数据的不可变列表
     */
    public List<T> items() {
        return items;
    }

    /**
     * @return 符合查询条件的总记录数
     */
    public int total() {
        return total;
    }

    /**
     * @return 当前页的起始偏移量
     */
    public int offset() {
        return offset;
    }
}
