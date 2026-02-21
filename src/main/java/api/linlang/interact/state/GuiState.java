package api.linlang.interact.state;

import java.util.Map;

/**
 * 会话状态：动态区分页/筛选/临时输入等都放这里。
 */
public interface GuiState extends Map<String, Object> {

    default String str(String key, String def) {
        Object v = get(key);
        return v == null ? def : String.valueOf(v);
    }

    default int integer(String key, int def) {
        Object v = get(key);
        if (v instanceof Number n) return n.intValue();
        try { return v == null ? def : Integer.parseInt(String.valueOf(v)); }
        catch (Exception ignore) { return def; }
    }

    default boolean bool(String key, boolean def) {
        Object v = get(key);
        if (v instanceof Boolean b) return b;
        if (v == null) return def;
        return "true".equalsIgnoreCase(String.valueOf(v));
    }
}