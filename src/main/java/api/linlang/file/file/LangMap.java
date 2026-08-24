package api.linlang.file.file;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 指向语言文件字符串映射字段的稳定引用。
 *
 * <p>解析结果和默认值均为不可修改的有序映射快照。映射键来自语言文件，
 * 映射值必须是可以转换为字符串的标量值。非空默认映射中的键会参与语言文件的
 * 缺失键补齐；开放键映射应通过 {@code LangMap.of()} 创建。</p>
 */
public interface LangMap extends LangValue<Map<String, String>> {

    /**
     * 创建尚未绑定的语言映射引用。
     *
     * @param fallback 缺失键默认值
     * @return 尚未绑定的语言映射引用
     */
    static LangMap of(Map<String, String> fallback) {
        return new DefaultLangMap(fallback);
    }

    /**
     * 通过交替排列的键和值创建尚未绑定的语言映射引用。
     *
     * @param entries 交替排列的键和值
     * @return 尚未绑定的语言映射引用
     * @throws IllegalArgumentException 参数数量为奇数时
     */
    static LangMap of(String... entries) {
        if (entries != null && (entries.length & 1) == 1) {
            throw new IllegalArgumentException("entries");
        }
        Map<String, String> values = new LinkedHashMap<>();
        if (entries != null) {
            for (int i = 0; i < entries.length; i += 2) {
                values.put(entries[i], entries[i + 1]);
            }
        }
        return new DefaultLangMap(values);
    }
}

final class DefaultLangMap implements LangMap {

    private final Map<String, String> fallback;

    DefaultLangMap(Map<String, String> fallback) {
        Map<String, String> values = new LinkedHashMap<>();
        if (fallback != null) {
            for (var entry : fallback.entrySet()) {
                String key = entry.getKey() == null ? "" : entry.getKey();
                String value = entry.getValue() == null ? "" : entry.getValue();
                values.put(key, value);
            }
        }
        this.fallback = Collections.unmodifiableMap(values);
    }

    @Override
    public String key() {
        return "";
    }

    @Override
    public Map<String, String> fallback() {
        return fallback;
    }

    @Override
    public Map<String, String> resolve() {
        return fallback;
    }

    @Override
    public Map<String, String> resolve(String locale) {
        return fallback;
    }

    @Override
    public String toString() {
        return fallback.toString();
    }
}
