package api.linlang.file.database.annotations;

import java.lang.annotation.*;

/**
 * 声明为数据实体
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
}