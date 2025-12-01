package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明为主键
 *
 * <p>此注解应用于字段</p>
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {
    boolean auto() default true;
}
