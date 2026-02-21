package api.linlang.interact.model.dto;

import java.util.Map;

/**
 * 变体匹配时的求值上下文（API 级定义，core 用它做表达式求值）。
 *
 * <p>row/state 是最常用的两个命名空间。</p>
 * <p>你也可以扩展 vars（例如 player/server 等）。</p>
 */
public record GuiEvalContext(
        Map<String, Object> state,
        Map<String, Object> row,
        Map<String, Object> vars
) {
}