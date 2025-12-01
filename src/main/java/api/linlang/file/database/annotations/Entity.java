package api.linlang.file.database.annotations;

import java.lang.annotation.*;

/**
 * 声明为数据实体
 *
 * <p>此注解应用于类</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
}