package api.linlang.file.file.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 为配置类或配置字段添加 YAML 注释
 *
 * <p>请注意，{@code Comment} 不应该用于语言文件。</p>
 *
 * <p>语言文件的注释应直接写入语言资源文件。</p>
 */
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Comment {
    /**
     * 注释内容，每一个数组元素为一行
     */
    String[] value();
}
