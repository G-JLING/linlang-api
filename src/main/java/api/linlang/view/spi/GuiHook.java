package api.linlang.view.spi;

import api.linlang.view.context.GuiContext;

/**
 * 业务 Hook：用于处理点击、打开、关闭等事件。
 */
@FunctionalInterface
public interface GuiHook {
    void handle(GuiContext ctx) throws Exception;
}