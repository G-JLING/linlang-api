package api.linlang.file.file;

import java.util.Set;

/**
 * 将语言资源文件绑定为可直接读取的 Java 对象。
 *
 * <p>活动语言由 Linlang 的全局语言参数决定。每个语言包可以通过注解声明默认语言、
 * 文件格式、归一化策略和写回策略。语言对象既可以使用保存当前值快照的 {@link String}
 * 和普通集合字段，也可以使用 {@link LangText}、{@link LangList} 与 {@link LangMap}
 * 声明在读取时解析最新值的稳定引用。</p>
 */
public interface LangService {

    /**
     * 绑定语言对象，并允许生成和补齐语言文件。
     *
     * @param keysClass 语言对象类
     * @param <T> 语言对象类型
     * @return 由服务管理的活动语言对象；其中的语言引用字段保持稳定
     */
    <T> T bind(Class<T> keysClass);

    /**
     * 绑定语言对象，并覆盖本次绑定的写回策略。
     *
     * <p>{@code emit} 为 {@code false} 时仅读取资源或磁盘文件，不生成、补齐或保存文件。</p>
     *
     * @param keysClass 语言对象类
     * @param emit 是否允许生成和写回文件
     * @param <T> 语言对象类型
     * @return 由服务管理的活动语言对象
     */
    <T> T bind(Class<T> keysClass, boolean emit);

    /**
     * 绑定语言对象并注册当前语言服务内唯一的别名，供配置引用使用。
     *
     * @param alias 语言包别名，由字母、数字、下划线或连字符组成
     * @param keysClass 语言对象类，资源路径仍由 LangPack 声明
     * @param <T> 语言对象类型
     * @return 与普通绑定共享的语言对象
     * @throws IllegalArgumentException 别名无效或已绑定到其他语言类
     */
    <T> T bind(String alias, Class<T> keysClass);

    /**
     * 绑定带别名的语言对象，并指定是否允许输出文件。
     *
     * @param alias 当前语言服务内的语言包别名
     * @param keysClass 语言对象类
     * @param emit 是否允许输出文件
     * @param <T> 语言对象类型
     * @return 已绑定的语言对象
     */
    <T> T bind(String alias, Class<T> keysClass, boolean emit);

    /**
     * 按别名和包内路径读取当前语言的原始值，不替换占位符。
     *
     * <p>活动语言中缺少路径时查询该包的默认语言。集合与映射不可修改。
     * 别名或路径不存在时返回 null；不跨语言包或插件查找。</p>
     *
     * @param alias 绑定时声明的别名
     * @param key 包内路径，使用语言文件中的键名
     * @return 字符串、列表、映射等原始值，或 null
     */
    Object lookup(String alias, String key);

    /**
     * 重新扫描语言目录，并原地刷新所有已绑定语言对象。
     *
     * <p>该方法不会改变当前全局语言；允许写回的语言包会同时补齐缺失键。</p>
     *
     * <p>外部修改语言文件后，需要调用此方法使修改生效。</p>
     */
    void reload();

    /**
     * 保存指定 Keys Class、指定 locale 的语言文件。
     *
     * <p>该方法用于将内存中对语言对象字段的修改写回文件。
     * 通常更推荐调用 {@link #saveAll()} 保存所有已绑定对象</p>
     *
     * @param keysClass 语言对象类
     * @param locale    locale，如 zh_CN / en_GB
     * @param <T> 语言对象类型
     */
    <T> void save(Class<T> keysClass, String locale);

    /**
     * 将所有已绑定活动语言对象保存到当前全局语言对应的文件。
     *
     * <p>该方法用于将内存中对语言对象字段的修改写回文件。</p>
     */
    void saveAll();

    /**
     * 返回磁盘中已经发现的 locale 集合。
     *
     * <p>启用语言包归一化时，名称会按规则处理，例如 {@code enGB}、{@code en-GB}
     * 会变为 {@code en_GB}。</p>
     *
     * @return 不重复的 locale 集合
     */
    Set<String> availableLocales();

    /**
     * 检查并补齐所有已发现 locale 文件的缺失键。
     *
     * <p>该方法会按 Keys Class 的字段结构作为 schema，对目录中每个 locale 文件补齐缺失字段。</p>
     *
     * <p>是否写回磁盘取决于 bind 时的 emit 或 {@code @LangPack.emit}</p>
     */
    void ensureAllLocales();

    /**
     * 对指定语言对象的所有已发现 locale 文件执行缺失键补齐。
     *
     * @param keysClass 语言对象类
     * @param <T> 语言对象类型
     */
    <T> void ensure(Class<T> keysClass);

    /**
     * 按路径键取得当前语言的模板文本并执行参数格式化。
     *
     * <p>优先查询当前语言，随后查询各语言包声明的默认语言；仍不存在时返回键本身。
     * 偶数个“名称、值”参数用于替换 {@code {name}}，其他参数交给
     * {@link java.text.MessageFormat} 处理。</p>
     *
     * @param key 字段路径键，例如 {@code message.prefix}
     * @param args 可选格式化参数
     * @return 翻译后的文本；未找到时返回 {@code key}
     */
    String tr(String key, Object... args);
}
