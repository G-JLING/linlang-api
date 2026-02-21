package api.linlang.interact.spi;


import api.linlang.interact.context.GuiContext;
import api.linlang.interact.model.GuiRow;

import java.util.List;

/**
 * 数据源：为动态区提供数据。
 */
@FunctionalInterface
public interface GuiSource {
    List<? extends GuiRow> load(GuiContext ctx) throws Exception;
}