package api.linlang.text;

import java.util.Objects;

/**
 * 解析文本时使用的语言与变量上下文。
 *
 * @param locale 语言标签；{@code null} 会转为空字符串，表示使用来源的当前语言
 * @param args 命名变量；{@code null} 会转为空集合
 */
public record TextContext(String locale, TextArgs args) {

    /**
     * 规范化空语言和空变量集合。
     *
     * @param locale 语言标签
     * @param args 命名变量
     */
    public TextContext {
        locale = Objects.requireNonNullElse(locale, "");
        args = args == null ? TextArgs.empty() : args;
    }

    /**
     * 创建不指定语言、也不绑定变量的上下文。
     *
     * @return 空上下文
     */
    public static TextContext empty() {
        return new TextContext("", TextArgs.empty());
    }
}
