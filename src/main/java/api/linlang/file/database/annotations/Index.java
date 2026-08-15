package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 为实体声明数据库索引。
 *
 * <p>注解放置在参与索引的字段上。单列索引可以省略 {@link #columns()}；
 * 复合索引可以显式列出字段或列名，具体解释由数据库实现决定。</p>
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Index {
    /**
     * @return 索引名称；空字符串表示由实现生成
     */
    String name() default "";

    /**
     * @return 复合索引包含的字段或列名
     */
    String[] columns() default {};
}
