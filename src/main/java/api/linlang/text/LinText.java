package api.linlang.text;

import java.util.Objects;

/**
 * 固定的 Linlang 高级字符串。
 *
 * <p>高级字符串使用后置描述符表达样式，例如
 * {@code 点击这里[@color=yellow;click.run="/help"]}。该对象只保存不可变源码，
 * 具体解析与平台渲染由运行时完成。</p>
 */
public final class LinText implements TextSource {

    private final String source;

    private LinText(String source) {
        this.source = Objects.requireNonNullElse(source, "");
    }

    /**
     * 创建固定高级字符串。
     *
     * @param source 高级字符串源码
     * @return 固定文本来源
     */
    public static LinText of(String source) {
        return new LinText(source);
    }

    /**
     * 返回原始高级字符串源码。
     *
     * @return 原始源码
     */
    public String source() {
        return source;
    }

    @Override
    public String resolve() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof LinText text && source.equals(text.source);
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }
}
