package api.linlang.file.file;

import api.linlang.file.file.FileType;

import java.util.Set;

/**
 * 语言文件服务
 */
public interface LangService {

    /**
     * 绑定一个语言对象（Keys Class）
     *
     * @param keysClass 语言对象类
     * @return 可用的语言对象实例
     */
    <T> T bind(Class<T> keysClass);

    /**
     * 绑定一个语言对象（Keys Class），并覆写该次 bind 的 emit 行为
     *
     * <p>emit=true 表示允许：
     * emit=false 表示仅读取，不写回磁盘。</p>
     */
    <T> T bind(Class<T> keysClass, boolean emit);

    /**
     * 重新扫描语言目录，并重新读取所有已绑定语言对象的文件内容，刷新到内存
     *
     * <p>该方法不会改变当前全局语言（total locale），只会重读磁盘文件并补齐缺失键（若允许写回）</p>
     *
     * <p>当你在外部编辑器修改了语言文件内容，需要调用该方法使其生效</p>
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
     */
    <T> void save(Class<T> keysClass, String locale);

    /**
     * 保存所有已绑定语言对象的所有 locale 文件。
     *
     * <p>建议在插件卸载或重载前调用，以保存对语言文件的动态修改</p>
     */
    void saveAll();

    /**
     * 返回当前已发现的 locale 集合（来自语言目录扫描结果）。
     *
     * <p>locale 会在扫描阶段按归一化规则处理，例如 enGB/en-GB → en_GB</p>
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
     * 仅对指定 Keys Class 执行缺失键补齐。
     */
    <T> void ensure(Class<T> keysClass);

    /**
     * 翻译：按 key 获取模板文本（可选能力）。
     *
     * <p>如果你希望保留 “key → 文本” 的扁平翻译入口，可用此方法。
     * 其 key 的语义由实现决定（例如 message.prefix 这样的路径键）。</p>
     *
     * <p>@hidden：如果你不希望对外暴露，也可以删掉此方法，仅保留 Keys Class 访问方式</p>
     */
    String tr(String key, Object... args);
}