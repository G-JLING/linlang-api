package api.linlang.view.state;

import java.util.Map;

/**
 * 界面会话共享的可变状态。
 *
 * <p>状态可用于动态区分页、筛选、临时输入和 {@code state.*} 占位符。</p>
 */
public interface GuiState extends Map<String, Object> {

    /**
     * 读取字符串值。
     *
     * @param key 状态键
     * @param def 缺失值的默认结果
     * @return 字符串值或默认结果
     */
    default String str(String key, String def) {
        Object v = get(key);
        return v == null ? def : String.valueOf(v);
    }

    /**
     * 读取整数值。
     *
     * @param key 状态键
     * @param def 缺失或无法转换时的默认结果
     * @return 整数值或默认结果
     */
    default int integer(String key, int def) {
        Object v = get(key);
        if (v instanceof Number n) return n.intValue();
        try { return v == null ? def : Integer.parseInt(String.valueOf(v)); }
        catch (Exception ignore) { return def; }
    }

    /**
     * 读取布尔值。
     *
     * @param key 状态键
     * @param def 缺失值的默认结果
     * @return 布尔值；字符串仅在忽略大小写等于 {@code true} 时为真
     */
    default boolean bool(String key, boolean def) {
        Object v = get(key);
        if (v instanceof Boolean b) return b;
        if (v == null) return def;
        return "true".equalsIgnoreCase(String.valueOf(v));
    }
}
