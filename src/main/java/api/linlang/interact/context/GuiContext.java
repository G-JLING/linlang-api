package api.linlang.interact.context;

import api.linlang.interact.model.GuiRow;
import api.linlang.interact.session.GuiSession;
import api.linlang.interact.state.GuiState;

import java.util.Map;

/**
 * 一次交互上下文（点击/打开/刷新时）。
 *
 * <p>如果来自动态区点击，则 row() 有值，row().data() 可取到绑定数据。</p>
 */
public interface GuiContext {

    GuiSession session();

    /** 当前 viewer。 */
    default Object viewer() { return session().viewer(); }

    /** 会话状态。 */
    default GuiState state() { return session().state(); }

    /** 当前点击的静态控件 uid（若有）。 */
    default String uid() { return ""; }

    /** 当前点击的动态区 id（若有）。 */
    default String areaId() { return ""; }

    /** 当前点击的动态行（若有）。 */
    default GuiRow row() { return null; }

    /** 动作参数（已渲染 placeholders 后的最终参数，建议由 core 提供）。 */
    default Map<String, Object> args() { return Map.of(); }

    /** 刷新整个 view。 */
    default void refresh() { session().refresh(); }

    /** 刷新指定动态区。 */
    default void refreshArea(String areaId) { session().refreshArea(areaId); }

    /** 关闭。 */
    default void close() { session().close(); }
}