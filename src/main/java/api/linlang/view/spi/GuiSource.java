package api.linlang.view.spi;


import api.linlang.view.context.GuiContext;
import api.linlang.view.model.GuiRow;

import java.util.List;

/**
 * 为动态区提供行数据的 Source。
 *
 * <p>资源文件中的 {@code source.args} 会在占位符求值后通过
 * {@link GuiContext#args()} 提供。</p>
 */
@FunctionalInterface
public interface GuiSource {
    /**
     * 加载动态区行数据。
     *
     * @param ctx Source 上下文
     * @return 按显示顺序排列的行；不应返回 {@code null}
     * @throws Exception 数据加载失败时
     */
    List<? extends GuiRow> load(GuiContext ctx) throws Exception;
}
