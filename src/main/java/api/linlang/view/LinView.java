package api.linlang.view;

import api.linlang.view.session.GuiSession;
import api.linlang.view.spi.GuiHook;
import api.linlang.view.spi.GuiSource;
import api.linlang.view.state.GuiState;

import java.util.Map;
import java.util.function.Consumer;

/**
 * LinView 界面服务入口。
 *
 * <p>视图定义来自 YAML 或 JSON 资源，{@link GuiSession} 保存单个 viewer 的状态、
 * 静态控件覆盖和动态区绑定。Hook 与 Source 应在打开相关视图之前注册。</p>
 */
public interface LinView {

    /**
     * 为指定 viewer 打开视图并创建新会话。
     *
     * @param viewer 平台 viewer，例如 Bukkit Player
     * @param viewId 视图 ID
     * @return 新创建的界面会话
     */
    GuiSession open(Object viewer, String viewId);

    /**
     * 打开视图，并在首次渲染和 Source 加载之前初始化会话状态。
     *
     * @param viewer 平台 viewer
     * @param viewId 视图 ID
     * @param patch 状态初始化回调，可为 {@code null}
     * @return 新创建的界面会话
     */
    GuiSession open(Object viewer, String viewId, Consumer<GuiState> patch);

    /**
     * 查找 viewer 当前的活动会话。
     *
     * @param viewer 平台 viewer
     * @return 活动会话；不存在时为 {@code null}
     */
    GuiSession session(Object viewer);

    /**
     * 关闭 viewer 当前的活动会话。
     *
     * @param viewer 平台 viewer
     */
    void close(Object viewer);

    /**
     * 清除全部视图缓存，并使用新定义重新打开受影响的活动会话。
     */
    void reload();

    /**
     * 清除指定视图的缓存，并重新打开使用该视图的活动会话。
     *
     * @param viewId 视图 ID
     */
    void reload(String viewId);

    /**
     * 注册由 {@code hook} 动作调用的业务处理器。
     *
     * @param hookId Hook ID
     * @param hook 业务处理器
     * @return 当前界面服务
     */
    LinView hook(String hookId, GuiHook hook);

    /**
     * 注册用于填充动态区的数据源。
     *
     * @param sourceId Source ID
     * @param source 数据源
     * @return 当前界面服务
     */
    LinView source(String sourceId, GuiSource source);

    /**
     * 使用映射初始化会话状态并打开视图。
     *
     * @param viewer 平台 viewer
     * @param viewId 视图 ID
     * @param initState 初始状态；为 {@code null} 时按空状态处理
     * @return 新创建的界面会话
     */
    default GuiSession open(Object viewer, String viewId, Map<String, Object> initState) {
        return open(viewer, viewId, s -> { if (initState != null) s.putAll(initState); });
    }
}
