package api.linlang.file.file.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个容器，不要使用，因为用起来很麻烦
 *
 * <pre><code>
 * {@literal @}I18nComments({
 *   {@literal @}I18nComment(locale = "zh_CN", lines = {"中文注释1", "中文注释2"}),
 *   {@literal @}I18nComment(locale = "en_GB", lines = {"English comment 1", "English comment 2"})
 * }) </code></pre>
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface I18nComments {
    I18nComment[] value();
}
