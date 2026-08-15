package api.linlang.view.model;

import java.util.Map;

/**
 * 界面控件被点击后执行的动作。
 *
 * <p>运行时内建支持 {@code hook}、{@code open}、{@code back}、{@code close}、
 * {@code command} 与 {@code state}。参数中的字符串允许使用 LinView 占位符。</p>
 */
public interface GuiAction {

    /**
     * @return 动作类型
     */
    String type();

    /**
     * @return 动作参数的只读映射
     */
    Map<String, Object> args();

    /**
     * @return 刷新策略；空字符串表示不刷新，其他常用值包括 {@code session}、
     * {@code area:<id>} 和 {@code widget:<uid>}
     */
    default String refresh() { return ""; }

    /**
     * 创建调用已注册 Hook 的动作。
     *
     * @param hookId Hook ID
     * @param args 附加参数，可为 {@code null}
     * @param refresh 执行后的刷新策略
     * @return Hook 动作
     */
    static GuiAction hook(String hookId, Map<String, Object> args, String refresh) {
        return new Simple("hook", merge(args, Map.of("hookId", hookId)), refresh);
    }

    /**
     * 创建打开另一视图的动作。
     *
     * @param viewId 目标视图 ID
     * @param args 附加参数；其中 {@code state} 映射用于初始化目标会话
     * @return 打开视图动作
     */
    static GuiAction open(String viewId, Map<String, Object> args) {
        return new Simple("open", merge(args, Map.of("viewId", viewId)), "session");
    }

    /**
     * @return 返回导航栈上一视图的动作
     */
    static GuiAction back() {
        return new Simple("back", Map.of(), "session");
    }

    /**
     * @return 关闭当前会话的动作
     */
    static GuiAction close() {
        return new Simple("close", Map.of(), "");
    }

    /**
     * 创建会话状态更新动作。
     *
     * <p>传入的字段会写入当前 {@code GuiState}。值为 {@code null} 时移除对应字段。</p>
     *
     * @param patch 状态补丁，可为 {@code null}
     * @param refresh 执行后的刷新策略
     * @return 状态更新动作
     */
    static GuiAction state(Map<String, Object> patch, String refresh) {
        Map<String, Object> args = new java.util.LinkedHashMap<>();
        if (patch != null) args.putAll(patch);
        return new Simple("state", java.util.Collections.unmodifiableMap(args), refresh);
    }

    /**
     * 创建玩家命令动作。
     *
     * @param command 玩家执行的命令；可以包含开头的斜杠
     * @return 命令动作
     */
    static GuiAction command(String command) {
        return new Simple("command", Map.of("command", command == null ? "" : command), "");
    }

    /**
     * {@link GuiAction} 的简单不可变实现。
     *
     * @param type 动作类型
     * @param args 动作参数
     * @param refresh 刷新策略
     */
    record Simple(String type, Map<String, Object> args, String refresh) implements GuiAction {}

    private static Map<String, Object> merge(Map<String, Object> a, Map<String, Object> b) {
        java.util.LinkedHashMap<String, Object> m = new java.util.LinkedHashMap<>();
        if (a != null) m.putAll(a);
        if (b != null) m.putAll(b);
        return java.util.Collections.unmodifiableMap(m);
    }
}
