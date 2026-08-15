package api.linlang.file.file.annotations;

import api.linlang.file.file.migrator.Migrator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明配置对象当前支持的文档版本。
 *
 * <p>读取旧版本文件时，配置服务会在填充对象之前执行已注册的 {@link Migrator}。
 * 文件版本高于当前版本或迁移链不完整时，绑定会失败。</p>
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface ConfigVersion {
    /**
     * @return 当前配置文件版本号
     */
    int value();

    /**
     * @return 配置文档中保存版本号的键
     */
    String key() default "_linlang-version";
}
