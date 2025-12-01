package api.linlang.file.file;

import api.linlang.file.file.implement.LocaleProvider;
import java.util.List;

/**
 * 语言服务
 */
public interface LangService {
    /**
     * 绑定语言对象并且注册语言提供者
     * <p>将一个语言对象绑定至语言服务，并且根据语言代码加载对应的语言提供者，并根据传参决定是否生成语言文件</p>
     *
     * @param keysClass 语言对象类
     * @param locale    地区代码，遵循 language_REGION 格式。例如 zh_CN
     * @param providers 语言提供者列表
     * @param emit      是否为此语言生成语言文件
     */
    <T> T bind(Class<T> keysClass, String locale,
                     List<? extends LocaleProvider<T>> providers, boolean emit);

    /**
     * 绑定语言对象并且注册语言提供者
     * <p>将一个语言对象绑定至语言服务，并且根据语言代码加载对应的语言提供者，随后生成语言文件</p>
     *
     * @param keysClass 语言对象类
     * @param locale    语言代码，遵循 language_REGION 格式。例如 zh_CN
     * @param providers 语言提供者列表
     */
    <T> T bind(Class<T> keysClass, String locale,
               List<? extends LocaleProvider<T>> providers);

    /** 保存指定语言对象到文件。
     * @hidden
     * */
    <T> void save(Class<T> keysClass, String locale);

    /** 保存所有已绑定的语言对象生成的语言文件中的修改
     *
     * <p>在软件卸载或重载前调用该方法以保存修改</p>
     */
    void saveAll();

    /** 当前语言代码 */
    String currentLocale();

    /** 设置当前语言代码
     * @param locale 语言代码，遵循 language_REGION 格式。例如 zh_CN
     */
    void setLocale(String locale);

    /**
     * 根据键返回模板文本，并按需处理占位、复数等。
     *
     * @param key 键，例如 "messenger.no-item-in-hand"
     * @param args 可选参数，交由实现处理（占位替换等）
     *
     * @hidden
     */
    String tr(String key, Object... args);

}
