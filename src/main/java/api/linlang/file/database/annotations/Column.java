package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 配置实体字段对应的数据库列。
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {

    /**
     * @return 列名；空字符串表示使用字段名
     */
    String name() default "";

    /**
     * @return 最大长度；零表示由实现或数据库类型决定
     */
    int length() default 0;

    /**
     * @return 是否允许数据库列为 {@code NULL}
     */
    boolean nullable() default true;

    /**
     * @return 数据库默认值表达式；空字符串表示不声明默认值
     */
    String defaultValue() default "";
}
