package api.linlang.view.model.dto;

import api.linlang.view.model.GuiAction;

/**
 * 动态模板的条件变体。
 *
 * <p>用于根据条件选择不同的 icon/action/enabled/visible 等属性。</p>
 * <p>{@code when} 是简易表达式字符串，例如：</p>
 * <ul>
 *   <li>{@code "{row.stock} <= 0"}</li>
 *   <li>{@code "{row.affordable} == false"}</li>
 *   <li>{@code "true"} 作为兜底</li>
 * </ul>
 *
 * @param id 变体 ID，可用于日志和调试
 * @param when 条件表达式
 * @param visible 可见性覆盖；为 {@code null} 时继承模板
 * @param enabled 可点击性覆盖；为 {@code null} 时继承模板
 * @param icon 图标覆盖；为 {@code null} 时继承模板
 * @param action 动作覆盖；为 {@code null} 时继承模板
 */
public record GuiVariant(
        String id,          // 可选：用于调试/日志，例如 soldOut/default
        String when,        // 条件表达式（由 core 解析求值）
        Boolean visible,    // 可选：覆盖可见性
        Boolean enabled,    // 可选：覆盖可点击性
        GuiIcon icon,       // 可选：覆盖 icon
        GuiAction action    // 可选：覆盖 action
) {
}
