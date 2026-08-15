package api.linlang.view.session;

import api.linlang.view.state.GuiState;

import java.util.Optional;

/**
 * 单个 viewer 打开视图后产生的运行态会话。
 *
 * <p>会话始终包含静态区和共享状态，可以包含零个或多个动态区。会话对象只在其所属
 * {@link api.linlang.view.LinView} 生命周期内有效。</p>
 */
public interface GuiSession {

    /**
     * @return 会话所属的平台 viewer
     */
    Object viewer();

    /**
     * @return 当前视图 ID
     */
    String viewId();

    /**
     * @return 当前会话的可变共享状态
     */
    GuiState state();

    /**
     * @return 静态控件集合
     */
    StaticView statics();

    /**
     * @return 动态区集合
     */
    DynamicAreas dynamics();

    /**
     * 重新加载全部数据源并刷新整个视图。
     */
    void refresh();

    /**
     * 重新加载指定动态区的数据源，并仅更新该区域。
     *
     * @param areaId 动态区 ID
     */
    void refreshArea(String areaId);

    /**
     * 关闭当前会话。
     */
    void close();

    /**
     * 返回导航栈中的上一视图。
     *
     * <p>默认实现不执行操作，支持导航的运行时会覆盖此方法。</p>
     */
    default void back() { /* optional in impl */ }

    /**
     * 以 Optional 形式获取动态区。
     *
     * @param areaId 动态区 ID
     * @return 动态区；不存在时为空
     */
    default Optional<DynamicAreaView> area(String areaId) {
        return Optional.ofNullable(dynamics().area(areaId));
    }
}
