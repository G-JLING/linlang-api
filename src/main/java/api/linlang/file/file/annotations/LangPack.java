package api.linlang.file.file.annotations;

import api.linlang.file.file.FileType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 声明语言对象的资源目录、文件格式与 locale 策略。
 *
 * <p>此注解应用于语言对象类。语言内容由 resources 与磁盘中的语言资源文件提供。</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LangPack {
    /**
     * @return 相对于插件数据目录和 {@code resources/langservice} 的语言包路径
     */
    String filePath() default "lang";

    /**
     * 旧版语言包路径属性。
     *
     * @deprecated 请使用 {@link #filePath()}
     * @return 相对语言包路径
     */
    @Deprecated
    String path() default "";

    /**
     * @return 语言文件格式
     */
    FileType format() default FileType.YAML;

    /**
     * @return 当前全局语言不可用时使用的默认 locale
     */
    String defaultLocale() default "en_GB";

    /**
     * @return 是否允许绑定时生成和补齐磁盘语言文件
     */
    boolean emit() default true;

    /**
     * @return 是否将 {@code enGB}、{@code en-GB} 等名称归一化为 {@code en_GB}
     */
    boolean normalizeLocale() default true;
}
