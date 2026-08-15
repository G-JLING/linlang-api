package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指定实体对应的数据表名称和可选说明。
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Table {

    /**
     * @return 数据表名称
     */
    String name();

    /**
     * @return 数据表说明；空字符串表示不声明说明
     */
    String comment() default "";
}
