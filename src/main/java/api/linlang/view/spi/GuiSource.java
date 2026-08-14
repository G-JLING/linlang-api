package api.linlang.view.spi;


import api.linlang.view.context.GuiContext;
import api.linlang.view.model.GuiRow;

import java.util.List;

/**
 * 数据源：为动态区提供数据。
 *
 * <p>资源文件中的 {@code source.args} 会在占位符求值后通过
 * {@link GuiContext#args()} 提供。</p>
 */
@FunctionalInterface
public interface GuiSource {
    List<? extends GuiRow> load(GuiContext ctx) throws Exception;
}
