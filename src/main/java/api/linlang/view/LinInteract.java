package api.linlang.view;

import api.linlang.view.session.GuiSession;
import api.linlang.view.spi.GuiHook;
import api.linlang.view.spi.GuiSource;
import api.linlang.view.state.GuiState;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 交互服务（GUI）入口。
 *
 * <p>交互服务以“模板 View + 会话 Session”为核心模型：
 * - View：静态定义（可来自 yml/json）
 * - Session：每次打开的运行态实例（按玩家隔离）
 * </p>
 */
public interface LinInteract {

    /** 打开一个视图（为指定 viewer 创建或复用 session）。 */
    GuiSession open(Object viewer, String viewId);

    /** 打开视图，并在打开前应用一段 state patch。 */
    GuiSession open(Object viewer, String viewId, Consumer<GuiState> patch);

    /** 查找已打开的 session（若不存在返回 null）。 */
    GuiSession session(Object viewer);

    /** 关闭 viewer 当前打开的 session（若有）。 */
    void close(Object viewer);

    /** 热重载视图定义（从磁盘重新加载/编译）。 */
    void reload();

    /** 仅重载指定 viewId。 */
    void reload(String viewId);

    /** 注册 hook（由按钮动作调用，执行业务逻辑）。 */
    LinInteract hook(String hookId, GuiHook hook);

    /** 注册数据源（用于动态区填充）。 */
    LinInteract source(String sourceId, GuiSource source);

    /** 便捷：打开前设置 state 的常见入口。 */
    default GuiSession open(Object viewer, String viewId, Map<String, Object> initState) {
        return open(viewer, viewId, s -> { if (initState != null) s.putAll(initState); });
    }
}