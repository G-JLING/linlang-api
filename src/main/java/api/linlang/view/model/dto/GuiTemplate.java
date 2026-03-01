package api.linlang.view.model.dto;

import api.linlang.view.model.GuiAction;

import java.util.List;

/**
 * 界面的动态区元素按钮模板类型
 *
 * 数据类型
 *
 * 用于把一条 GuiRow 渲染成 GuiWidget。
 *
 * <p>若定义了 variants，则按顺序匹配第一个 when=true 的变体，作为本行最终模板。</p>
 * <p>表达式求值由 core 实现，API 只负责承载 when 文本。</p>
 */
public record GuiTemplate(
        GuiIcon icon,
        GuiAction action,
        List<GuiVariant> variants
) {
    public GuiTemplate {
        variants = (variants == null) ? List.of() : List.copyOf(variants);
    }

    /** 无变体的简单模板。 */
    public static GuiTemplate of(GuiIcon icon, GuiAction action) {
        return new GuiTemplate(icon, action, List.of());
    }
}