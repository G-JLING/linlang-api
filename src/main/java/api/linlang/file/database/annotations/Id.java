package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 将实体字段声明为主键。
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {
    /**
     * @return 是否由数据库自动生成主键值
     */
    boolean auto() default true;
}
