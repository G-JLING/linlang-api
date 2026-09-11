package api.linlang.runtime.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 四段数字版本；比较时忽略预发布标记与构建元数据。
 */
public record LinVersion(int a, int b, int c, int d) implements Comparable<LinVersion> {
    private static final Pattern FORMAT = Pattern.compile(
            "([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)"
                    + "(?:-[0-9A-Za-z]+(?:[.-][0-9A-Za-z]+)*)?"
                    + "(?:\\+[0-9A-Za-z]+(?:[.-][0-9A-Za-z]+)*)?");

    /**
     * 创建非负的四段版本。
     */
    public LinVersion {
        if (a < 0 || b < 0 || c < 0 || d < 0) {
            throw new IllegalArgumentException("Version components must be non-negative");
        }
    }

    /**
     * 解析 A.B.C.D，可带 SNAPSHOT 等后缀；无效格式直接拒绝。
     *
     * @param value 版本字符串
     * @return 数字版本
     * @throws IllegalArgumentException 格式错误或任一段超过整数范围
     */
    public static LinVersion parse(String value) {
        Matcher matcher = FORMAT.matcher(value == null ? "" : value.trim());
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid Linlang version: " + value);
        try {
            return new LinVersion(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Linlang version component exceeds integer range: " + value, exception);
        }
    }

    @Override
    public int compareTo(LinVersion other) {
        int order = Integer.compare(a, other.a);
        if (order == 0) order = Integer.compare(b, other.b);
        if (order == 0) order = Integer.compare(c, other.c);
        if (order == 0) order = Integer.compare(d, other.d);
        return order;
    }

    @Override
    public String toString() {
        return a + "." + b + "." + c + "." + d;
    }
}
