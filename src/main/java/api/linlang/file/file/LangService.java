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
     * @param providers 语言提供者列表
     * @param emit      是否为此语言生成语言文件
     */
    <T> T bind(Class<T> keysClass,
                     List<? extends LocaleProvider<T>> providers, boolean emit);

    /**
     * 绑定语言对象并且注册语言提供者
     * <p>将一个语言对象绑定至语言服务，并且根据语言代码加载对应的语言提供者，随后生成语言文件</p>
     *
     * @param keysClass 语言对象类
     * @param providers 语言提供者列表
     */
    <T> T bind(Class<T> keysClass,
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

    /**
     * 根据键返回模板文本，并按需处理占位、复数等。
     *
     * @param key 键，例如 "messenger.no-item-in-hand"
     * @param args 可选参数，交由实现处理（占位替换等）
     *
     * @hidden
     */
    String tr(String key, Object... args);

    /**
     * 重新读取磁盘上所有绑定的文件并应用
     * <p>更新会被应用于最后一次赋值 {@link #bind(Class, String, List)} 的字段。<code>List</code> 中的所有文件均被更新，语言代码保持不变</p>
     *
     * <p>若文件代码被更改过，应先调用 {@link #saveAll()} 方法使更改落盘</p>
     */
    default void reload() {};

}
