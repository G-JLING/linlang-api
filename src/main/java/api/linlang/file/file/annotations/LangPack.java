package api.linlang.file.file.annotations;

import api.linlang.file.file.FileType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明语言对象的资源目录与文件格式
 *
 * <p>此注解应用于语言对象类。语言内容由 resources 与磁盘中的语言资源文件提供。</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LangPack {
    /**
     * 相对语言根目录的资源路径
     */
    String filePath() default "lang";

    /**
     * 相对语言根目录的资源路径
     *
     * @deprecated 请使用 {@link #filePath()}
     */
    @Deprecated
    String path() default "";

    /** 文件格式：YAML/JSON */
    FileType format() default FileType.YAML;

    /** 默认语言文件名（不存在时生成它） */
    String defaultLocale() default "en_GB";

    /** 是否在 bind 时自动生成内建语言文件（缺失补齐也算生成行为） */
    boolean emit() default true;

    /** 归一化策略：是否把 enGB/en-GB 归一化到 en_GB */
    boolean normalizeLocale() default true;
}
