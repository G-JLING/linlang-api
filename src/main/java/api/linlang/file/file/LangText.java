package api.linlang.file.file;

import java.util.Objects;

/**
 * 指向语言文件字段的稳定文本引用。
 *
 * <p>{@code LangText} 不保存绑定时读取到的固定字符串。语言服务会根据字段路径创建引用，
 * 每次解析时从当前语言快照中取得文本，因此同一引用可以跨越普通语言切换和文件重载继续使用。</p>
 *
 * <p>语言对象中的字段应通过 {@link #of(String)} 提供缺失键的默认值。字段完成绑定前，
 * 该引用解析为默认值；完成绑定后，语言服务会为字段安装受管理的引用。</p>
 */
public interface LangText extends LangValue<String> {

    /**
     * 返回字段在语言对象中的路径键。
     *
     * @return 路径键；尚未绑定时为空字符串
     */
    @Override
    String key();

    /**
     * 返回代码声明的缺失键默认值。
     *
     * @return 默认文本，不为 {@code null}
     */
    @Override
    String fallback();

    /**
     * 按语言服务当前使用的 locale 解析文本。
     *
     * @return 当前文本；语言文件中不存在该键时返回默认文本
     */
    @Override
    String resolve();

    /**
     * 按指定 locale 解析文本。
     *
     * @param locale locale 标签；为 {@code null} 或空白时使用当前 locale
     * @return 对应文本；指定语言及默认语言均不存在该键时返回默认文本
     */
    @Override
    String resolve(String locale);

    /**
     * 创建尚未绑定的语言文本引用。
     *
     * <p>该对象用于在语言类中声明默认值。通常不应把它作为独立的动态语言引用使用。</p>
     *
     * @param fallback 缺失键默认值
     * @return 尚未绑定的语言文本引用
     */
    static LangText of(String fallback) {
        return new DefaultLangText(fallback);
    }
}

final class DefaultLangText implements LangText {

    private final String fallback;

    DefaultLangText(String fallback) {
        this.fallback = Objects.requireNonNullElse(fallback, "");
    }

    @Override
    public String key() {
        return "";
    }

    @Override
    public String fallback() {
        return fallback;
    }

    @Override
    public String resolve() {
        return fallback;
    }

    @Override
    public String resolve(String locale) {
        return fallback;
    }

    @Override
    public String toString() {
        return fallback;
    }
}
