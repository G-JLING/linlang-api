package api.linlang.file.database.annotations;

import java.lang.annotation.*;

/**
 * 将类声明为可由数据库服务持久化的实体。
 *
 * <p>实体字段可以使用本包中的字段注解补充列、主键、索引和约束信息。</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
}
