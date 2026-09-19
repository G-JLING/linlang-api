package api.linlang.file.file.config;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * 配置中的整段文本列表，可表示字面列表或语言列表引用。
 */
public interface ConfigList extends Supplier<List<String>> {
    /**
     * 返回当前语言下的只读列表快照。
     *
     * @return 文本列表或回退内容
     */
    @Override
    List<String> get();

    /**
     * 返回可写回配置的来源定义，不将引用展开为翻译。
     *
     * @return 只读字面列表、引用字符串或只读引用映射
     */
    Object source();

    /**
     * 创建配置字段的字面列表默认值。
     *
     * @param lines 默认列表，元素不可为 null
     * @return 字面配置列表
     */
    static ConfigList of(Collection<String> lines) {
        List<String> literal = List.copyOf(lines);
        return new ConfigList() {
            @Override
            public List<String> get() { return literal; }
            @Override
            public Object source() { return literal; }
        };
    }

    /**
     * 创建配置字段的字面列表默认值。
     *
     * @param lines 默认文本行
     * @return 字面配置列表
     */
    static ConfigList of(String... lines) {
        return of(List.of(lines));
    }
}
