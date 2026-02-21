package api.linlang.interact.session;

import api.linlang.interact.state.GuiState;

import java.util.Optional;

/**
 * 一次界面会话（每个 viewer 独立）。
 */
public interface GuiSession {

    /** 会话所属 viewer（例如 Bukkit Player）。 */
    Object viewer();

    /** 当前 viewId。 */
    String viewId();

    /** 会话状态（可在 hook 中修改；用于过滤/分页/临时输入等）。 */
    GuiState state();

    /** 静态区：通过 UID 定位与修改控件。 */
    StaticView statics();

    /** 动态区：通过 areaId 操作可填充区域。 */
    DynamicAreas dynamics();

    /** 立即刷新（重渲染整个视图）。 */
    void refresh();

    /** 只刷新某个动态区（更轻量）。 */
    void refreshArea(String areaId);

    /** 关闭会话。 */
    void close();

    /** 返回上一页（如果 session 维护了导航栈）。 */
    default void back() { /* optional in impl */ }

    /** 获取动态区（不存在则 empty）。 */
    default Optional<DynamicAreaView> area(String areaId) {
        return Optional.ofNullable(dynamics().area(areaId));
    }
}