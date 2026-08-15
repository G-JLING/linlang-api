package api.linlang.file.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明实体字段对应的数据库列不允许为 {@code NULL}。
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface NotNull {}
