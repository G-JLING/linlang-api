package api.linlang.view.context;

import api.linlang.view.model.GuiRow;
import api.linlang.view.session.GuiSession;
import api.linlang.view.state.GuiState;

import java.util.Map;

/**
 * Hook 或 Source 单次调用的界面上下文。
 *
 * <p>动态槽位点击时 {@link #row()} 返回对应行；静态控件点击时 {@link #uid()}
 * 返回控件 UID。与当前调用无关的值使用空字符串或 {@code null} 表示。</p>
 */
public interface GuiContext {

    /**
     * @return 当前界面会话
     */
    GuiSession session();

    /**
     * @return 当前平台 viewer
     */
    default Object viewer() { return session().viewer(); }

    /**
     * @return 当前会话的可变状态
     */
    default GuiState state() { return session().state(); }

    /**
     * @return 当前静态控件 UID；不适用时为空字符串
     */
    default String uid() { return ""; }

    /**
     * @return 当前动态区 ID；不适用时为空字符串
     */
    default String areaId() { return ""; }

    /**
     * @return 当前动态行；不适用时为 {@code null}
     */
    default GuiRow row() { return null; }

    /**
     * @return 已完成占位符替换的动作或 Source 参数
     */
    default Map<String, Object> args() { return Map.of(); }

    /**
     * 重新加载全部 Source 并刷新整个视图。
     */
    default void refresh() { session().refresh(); }

    /**
     * 重新加载并刷新指定动态区。
     *
     * @param areaId 动态区 ID
     */
    default void refreshArea(String areaId) { session().refreshArea(areaId); }

    /**
     * 关闭当前会话。
     */
    default void close() { session().close(); }
}
