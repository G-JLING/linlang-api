package api.linlang.file.file.implement;

/**
 * 以代码方式向语言对象写入单个 locale 内容的提供者。
 *
 * @param <T> 语言对象类
 */
public interface LocaleProvider<T> {

    /**
     * 语言代码，遵循 language_REGION 格式，例如 zh_CN
     *
     * @return 语言代码字符串
     */
    String locale();

    /**
     * 将该 locale 的文本写入语言对象。
     *
     * @param keys 语言对象类
     */
    void define(T keys);

}
