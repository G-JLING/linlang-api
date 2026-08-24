package api.linlang.file.file;

/**
 * 指向语言文件字段的稳定值引用。
 *
 * @param <T> 解析后的值类型
 */
public interface LangValue<T> {

    /**
     * 返回字段在语言对象中的路径键。
     *
     * @return 路径键；尚未绑定时为空字符串
     */
    String key();

    /**
     * 返回代码声明的缺失键默认值。
     *
     * @return 默认值
     */
    T fallback();

    /**
     * 按语言服务当前使用的 locale 解析值。
     *
     * @return 当前值；语言文件中的值无效或不存在时返回默认值
     */
    T resolve();

    /**
     * 按指定 locale 解析值。
     *
     * @param locale locale 标签；为 {@code null} 或空白时使用当前 locale
     * @return 对应值；语言文件中的值无效或不存在时返回默认值
     */
    T resolve(String locale);
}
