package api.linlang.file.file.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 为字段指定自定义键名，覆盖 {@link NamingStyle} 的自动命名。
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface Key {
    /**
     * @return 字段对应的文件键名
     */
    String value();
}
