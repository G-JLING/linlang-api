package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指定字符串或二进制字段的最大列长度。
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Length {

    /**
     * @return 最大长度
     */
    int value();
}
