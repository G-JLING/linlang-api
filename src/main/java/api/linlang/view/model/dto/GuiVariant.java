package api.linlang.view.model.dto;

import api.linlang.view.model.GuiAction;

/**
 * 界面的动态区元素按钮变体类型
 *
 * 数据类型
 *
 * <p>用于根据条件选择不同的 icon/action/enabled/visible 等属性。</p>
 * <p>when 是简易表达式字符串，例如：
 * <ul>
 *   <li>{@code "{row.stock} <= 0"}</li>
 *   <li>{@code "{row.affordable} == false"}</li>
 *   <li>{@code "true"} 作为兜底</li>
 * </ul>
 * </p>
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