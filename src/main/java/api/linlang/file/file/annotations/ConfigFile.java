package api.linlang.file.file.annotations;

import api.linlang.file.file.FileType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 将类声明为配置文件对象，并指定文件名、相对路径和序列化格式。
 *
 * <p>配置对象需要可通过无参构造方法创建；参与映射的字段应为公开字段。</p>
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface ConfigFile {

    /**
     * @return 不含扩展名的文件名
     */
    String name() default "config";

    /**
     * @return 相对于插件数据目录的子路径；空字符串表示插件数据目录
     */
    String path() default "";

    /**
     * @return 配置文件格式
     */
    FileType format() default FileType.YAML;
}
