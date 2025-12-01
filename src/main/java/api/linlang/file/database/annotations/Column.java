package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明为 Column
 *
 * <p>此注解应用于字段</p>
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {

    /**
     * 名称
     */
    String name() default "";

    /**
     * 最大长度
     */
    int length() default 0;

    /**
     * 是否可空
     */
    boolean nullable() default true;

    /**
     * 默认值
     */
    String defaultValue() default "";
}
