package api.linlang.file.file.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 禁止配置对象或语言对象生成和写回磁盘文件。
 *
 * <p>该注解不影响磁盘和内建资源的读取，也不影响对象填充。</p>
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface NoEmit { }
