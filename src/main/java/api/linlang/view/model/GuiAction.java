package api.linlang.view.model;

import java.util.Map;

/**
 * 界面中元素按钮的点击后动作类型
 */
public interface GuiAction {

    /** 动作类型：hook/open/back/close/command/state 等。 */
    String type();

    /** 参数（支持 placeholders）。 */
    Map<String, Object> args();

    /** 刷新策略：空=不刷新；"session"=刷新全局；"area:xxx"=刷新某动态区；"widget:uid"=刷新静态控件等 */
    default String refresh() { return ""; }

    static GuiAction hook(String hookId, Map<String, Object> args, String refresh) {
        return new Simple("hook", merge(args, Map.of("hookId", hookId)), refresh);
    }

    static GuiAction open(String viewId, Map<String, Object> args) {
        return new Simple("open", merge(args, Map.of("viewId", viewId)), "session");
    }

    static GuiAction back() {
        return new Simple("back", Map.of(), "session");
    }

    static GuiAction close() {
        return new Simple("close", Map.of(), "");
    }

    record Simple(String type, Map<String, Object> args, String refresh) implements GuiAction {}

    private static Map<String, Object> merge(Map<String, Object> a, Map<String, Object> b) {
        java.util.LinkedHashMap<String, Object> m = new java.util.LinkedHashMap<>();
        if (a != null) m.putAll(a);
        if (b != null) m.putAll(b);
        return java.util.Collections.unmodifiableMap(m);
    }
}