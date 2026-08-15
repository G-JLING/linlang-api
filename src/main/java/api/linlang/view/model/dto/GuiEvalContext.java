package api.linlang.view.model.dto;

import java.util.Map;

/**
 * 变体匹配时的求值上下文。
 *
 * <p>{@code row} 与 {@code state} 是内建命名空间，{@code vars} 可承载玩家或平台变量。</p>
 *
 * @param state 当前会话状态
 * @param row 当前动态行数据
 * @param vars 其他求值变量
 */
public record GuiEvalContext(
        Map<String, Object> state,
        Map<String, Object> row,
        Map<String, Object> vars
) {
}
