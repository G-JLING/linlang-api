package api.linlang.view.model.dto;

import api.linlang.view.model.GuiAction;

import java.util.List;

/**
 * 将 {@link api.linlang.view.model.GuiRow} 渲染为控件的动态区模板。
 *
 * <p>若定义了 variants，则按顺序匹配第一个 when=true 的变体，作为本行最终模板。</p>
 * <p>表达式求值由 core 实现，API 只负责承载 when 文本。</p>
 *
 * @param icon 默认图标
 * @param action 默认动作
 * @param variants 按声明顺序匹配的变体列表
 */
public record GuiTemplate(
        GuiIcon icon,
        GuiAction action,
        List<GuiVariant> variants
) {
    /**
     * 规范化变体列表并创建模板。
     *
     * @param icon 默认图标
     * @param action 默认动作
     * @param variants 变体列表
     */
    public GuiTemplate {
        variants = (variants == null) ? List.of() : List.copyOf(variants);
    }

    /**
     * 创建不包含变体的模板。
     *
     * @param icon 默认图标
     * @param action 默认动作
     * @return 简单模板
     */
    public static GuiTemplate of(GuiIcon icon, GuiAction action) {
        return new GuiTemplate(icon, action, List.of());
    }
}
