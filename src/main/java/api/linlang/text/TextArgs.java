package api.linlang.text;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 高级字符串的命名变量集合。
 *
 * <p>普通对象会作为安全的普通文本插入，{@link TextSource} 会作为高级文本插入。</p>
 */
public final class TextArgs {

    private static final TextArgs EMPTY = new TextArgs(Map.of());

    private final Map<String, Object> values;

    private TextArgs(Map<String, ?> values) {
        Map<String, Object> copy = new LinkedHashMap<>();
        if (values != null) {
            values.forEach((key, value) -> copy.put(String.valueOf(key), value));
        }
        this.values = Collections.unmodifiableMap(copy);
    }

    /**
     * 返回空变量集合。
     *
     * @return 空变量集合
     */
    public static TextArgs empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 通过名称和值交替排列的参数创建变量集合。
     *
     * @param keyValues 名称和值交替排列的参数
     * @return 不可变变量集合
     * @throws IllegalArgumentException 参数数量为奇数时
     */
    public static TextArgs of(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) return EMPTY;
        if ((keyValues.length & 1) == 1) {
            throw new IllegalArgumentException("Text arguments must use name-value pairs.");
        }
        Map<String, Object> values = new LinkedHashMap<>();
        for (int index = 0; index < keyValues.length; index += 2) {
            values.put(String.valueOf(keyValues[index]), keyValues[index + 1]);
        }
        return new TextArgs(values);
    }

    /**
     * 通过已有映射创建变量集合。
     *
     * @param values 变量映射
     * @return 不可变变量集合
     */
    public static TextArgs from(Map<String, ?> values) {
        if (values == null || values.isEmpty()) return EMPTY;
        return new TextArgs(values);
    }

    /**
     * 返回不可变变量映射。
     *
     * @return 变量映射
     */
    public Map<String, Object> values() {
        return values;
    }

    /**
     * 判断变量集合是否为空。
     *
     * @return 没有变量时为 {@code true}
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * 显式区分普通变量与可信高级文本变量。
     */
    public static final class Builder {
        private final Map<String, Object> values = new LinkedHashMap<>();

        public Builder plain(String name, Object value) {
            values.put(name, value == null ? "" : String.valueOf(value));
            return this;
        }

        public Builder rich(String name, TextSource value) {
            values.put(name, value);
            return this;
        }

        public TextArgs build() {
            return new TextArgs(values);
        }
    }
}
