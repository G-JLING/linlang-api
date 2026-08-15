package api.linlang.file.database.annotations;

import java.lang.annotation.*;

/**
 * 排除不需要持久化的实体字段。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Transient {

}
