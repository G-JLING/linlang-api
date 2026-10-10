package api.linlang.text;

/**
 * 可按语言和变量上下文生成高级字符串的文本来源。
 */
public interface TextSource {

    /**
     * 组合多个文本来源，每次解析时重新读取动态语言引用。
     *
     * @param parts 文本来源
     * @return 可随语言重载更新的组合文本
     */
    static TextSource join(TextSource... parts) {
        TextSource[] copy = parts == null ? new TextSource[0] : parts.clone();
        return new TextSource() {
            @Override
            public LinText resolveText(TextContext context) {
                TextContext effective = context == null ? TextContext.empty() : context;
                LinText[] resolved = new LinText[copy.length];
                for (int index = 0; index < copy.length; index++) {
                    resolved[index] = copy[index].resolveText(effective);
                }
                return LinText.join(resolved);
            }

            @Override
            public String resolve() {
                return resolveText(TextContext.empty()).toPlainText();
            }
        };
    }

    /**
     * 在当前上下文中解析并绑定文本。
     *
     * @param context 文本上下文
     * @return 已编译的高级字符串
     */
    default LinText resolveText(TextContext context) {
        TextContext effective = context == null ? TextContext.empty() : context;
        return LinText.parse(resolve(effective.locale())).bind(effective);
    }

    /**
     * 解析当前文本源码。
     *
     * @return 高级字符串源码，不为 {@code null}
     */
    String resolve();

    /**
     * 按指定语言解析文本源码。
     *
     * <p>固定文本默认忽略语言参数。支持多语言的来源可以重写此方法。</p>
     *
     * @param locale 语言标签
     * @return 高级字符串源码，不为 {@code null}
     */
    default String resolve(String locale) {
        return resolve();
    }
}
