package api.linlang.file.file.annotations;

import api.linlang.file.file.FileType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明当前类为配置文件类
 *
 * <p>此注解应用于类</p>
 *
 * <p>指定语言文件的文件名、存放路径以及格式</p>
 *
 * <p>在 <a href="https://jling.me/p/linlang/file/file/配置文件">配置文件</a> 页面中，您可以了解什么是配置文件类，这是一个琳琅专有名词</p>
 *
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface ConfigFile {

    /**
     * 文件名（不含扩展名）
     * 默认为 config
     */
    String name() default "config";

    /**
     * 配置文件相对路径（相对于插件根目录）
     * 默认为空，即插件根目录
     */
    String path() default "";

    /**
     * 配置文件格式
     * 默认为 {@link FileType#YAML}
     */
    FileType format() default FileType.YAML;
}