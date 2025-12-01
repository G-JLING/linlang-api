// called.linlang.annotations.file.I18nComment.java
package api.linlang.file.file.annotations;

import java.lang.annotation.*;

/**
 * 多语言注释
 * <p>注意，此注解不应用于配置文件类，即被 {@link ConfigFile} 注解的文件类</p>
 *
 * <p>此注解应用于类和字段</p>
 *
 * <pre><code>
 * {@literal @}I18nComment(locale = "zh_CN", lines = "欢迎语")
 * {@literal @}I18nComment(locale = "en_GB", lines = "Welcome messenger")
 *  public String welcome;
 * </code></pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(I18nComments.class)
public @interface I18nComment {
    /**
     * 语言代码，遵循 language_REGION 规则，如 zh_CN
     */
    String locale();

    /**
     * 注释内容。每一个数组元素为一行
     */
    String[] lines();
}
