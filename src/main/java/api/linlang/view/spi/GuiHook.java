package api.linlang.view.spi;

import api.linlang.view.context.GuiContext;

/**
 * 处理 LinView 动作的业务 Hook。
 */
@FunctionalInterface
public interface GuiHook {
    /**
     * 处理一次界面动作。
     *
     * @param ctx 动作上下文
     * @throws Exception 业务处理失败时
     */
    void handle(GuiContext ctx) throws Exception;
}
