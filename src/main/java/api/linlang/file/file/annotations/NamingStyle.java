package api.linlang.file.file.annotations;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 定义类中公开字段到文件结构的映射方式。
 *
 * <p>支持三种命名风格：</p>
 * <ul>
 *   <li>{@link Style#KEBAB}：驼峰转 KEBAB（中划线连接）。例如 {@code myValue → my-value}</li>
 *   <li>{@link Style#IDENTITY}：保持 Java 字段名不变。</li>
 *   <li>{@link Style#LIST}：按公开字段声明顺序将对象映射为列表。</li>
 * </ul>
 *
 * <p>该注解同时适用于配置对象和语言对象。</p>
 */
@Retention(RUNTIME) @Target(TYPE)
public @interface NamingStyle {
    /**
     * @return 字段映射风格
     */
    Style value() default Style.KEBAB;

    /**
     * 支持的字段映射风格。
     */
    enum Style {
        /** 保持字段名不变。 */
        IDENTITY,
        /** 将驼峰字段名转换为短横线分隔形式。 */
        KEBAB,
        /** 按公开字段声明顺序映射为列表。 */
        LIST
    }
}
