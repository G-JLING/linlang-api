package api.linlang.file.file.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 为字段指定自定义的键名，覆盖自动生成的命名
 *
 * <p>此注解应用于字段</p>
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface Key {
    /**
     * 指定键名
     */
    String value();
}