package api.linlang.file.file.config;

import api.linlang.text.TextSource;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 配置中的单段文本，可表示字面值或受语言服务管理的引用。
 *
 * <p>在配置对象中声明此类型后，文件服务自动解析普通文本、单行引用及引用对象。
 * 每次读取语言引用时使用当前语言；配置重载后应重新访问配置字段。</p>
 */
public interface ConfigText extends TextSource, Supplier<String> {
    /**
     * 返回当前文本，不执行占位符替换。
     *
     * @return 当前文本或回退内容
     */
    @Override
    String get();

    /**
     * 返回可写回配置的来源定义，而非当前翻译。
     *
     * @return 字符串或只读引用映射
     */
    Object source();

    /**
     * 为消息服务解析当前文本。
     *
     * @return 当前文本
     */
    @Override
    default String resolve() {
        return get();
    }

    /**
     * 创建配置字段的字面默认值。
     *
     * @param text 默认文本
     * @return 字面配置文本
     */
    static ConfigText of(String text) {
        String literal = Objects.requireNonNullElse(text, "");
        return new ConfigText() {
            @Override
            public String get() { return literal; }
            @Override
            public Object source() { return literal; }
        };
    }
}
