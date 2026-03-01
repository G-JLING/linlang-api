package api.linlang.view.spi;


import api.linlang.view.context.GuiContext;
import api.linlang.view.model.GuiRow;

import java.util.List;

/**
 * 数据源：为动态区提供数据。
 */
@FunctionalInterface
public interface GuiSource {
    List<? extends GuiRow> load(GuiContext ctx) throws Exception;
}