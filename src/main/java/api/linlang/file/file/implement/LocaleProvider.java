// called.linlang.annotations.file.LocaleProvider
package api.linlang.file.file.implement;

import java.util.List;

/**
 * 语言对象接口
 *
 * <p>在 <a href="https://jling.me/p/linlang/file/file/语言文件">语言文件</a> 页面中，您可以了解什么是语言对象，这是一个琳琅专有名词</p>
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
     * 使当前语言提供者实现的语言对象类中的 Key 赋值
     *
     * <p>在 <a herf="https://jling.me/p/linlang/file/file/语言服务">语言服务</a> 页面中，您可以看到使用示例</p>
     *
     * @param keys 语言对象类
     */
    void define(T keys);

    /**
     * 返回承载注释的类（结构需与键结构类同形）。为 null 表示无注释。
     *
     * @hidden
     */
    default Class<?> commentsType() {
        return null;
    }
}