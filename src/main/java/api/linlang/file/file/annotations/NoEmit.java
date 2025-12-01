package api.linlang.file.file.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明该类不应被生成为文件
 *
 * <p>此注解应用于类</p>
 * <p>仅在类拥有 {@link ConfigFile} 或 {@link LangPack} 注解时有意义，表明该文件类不应被生成为文件</p>
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface NoEmit { }