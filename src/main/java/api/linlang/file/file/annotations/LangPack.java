package api.linlang.file.file.annotations;

import api.linlang.file.file.FileType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明当前类为语言提供者
 * <p>注意，声明语言对象的文件不应该添加此注解</p>
 *
 * <p>此注解应用于实现 {@link api.linlang.file.file.implement.LocaleProvider} 的类</p>
 *
 * <p>指定语言文件的文件名、存放路径以及格式</p>
 *
 * <p>在 <a href="https://jling.me/p/linlang/file/file/语言文件" target=_blank>语言文件</a> 页面中，您可以了解什么是语言提供者，这是一个琳琅专有名词</p>
 */
@Retention(RUNTIME) @Target(TYPE)
public @interface LangPack {

    /**
     * 语言代码，遵循 language_REGION 规则，例如 zh_CN
     */
    String locale();

    /**
     * 文件名（不含扩展名），默认为语言代码
     */
    String name() default "";

    /**
     * 配置文件相对路径（相对于插件根目录）
     *
     * <p>默认时，在插件目录的 lang 文件夹</p>
     */
    String path() default "lang";

    /**
     * 配置文件格式。
     * 默认为 {@link FileType#YAML}。
     */
    FileType format() default FileType.YAML;
}