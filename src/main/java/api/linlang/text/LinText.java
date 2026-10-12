package api.linlang.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 不可变的高级字符串，由可组合的 Adventure 文本组件构成。
 *
 * <p>{@link #parse(String)} 接受 {@code 文本[@描述符]} 语法，也接受旧式颜色代码。
 * 描述符按分号分隔；支持 {@code color}、{@code bold}、{@code italic}、
 * {@code underlined}、{@code strikethrough}、{@code obfuscated}、{@code font}、
 * {@code insertion}、{@code gradient}、{@code rainbow}、{@code keybind}、
 * {@code translate}、{@code hover} 和 {@code click.run/suggest/copy/url}。
 * 同一片段只能有一种点击行为。变量应在解析后通过 {@link #bind(TextArgs)} 绑定。</p>
 */
public final class LinText implements TextSource {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final Map<String, Component> PARSED = new LinkedHashMap<>(512, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Component> eldest) {
            return size() > 512;
        }
    };

    private final Component component;
    private final String source;

    private LinText(Component component, String source) {
        this.component = Objects.requireNonNull(component, "component");
        this.source = source;
    }

    /**
     * 解析高级字符串源码；{@code null} 等同于空字符串。
     *
     * <p>解析结果缓存为组件树，返回值本身不可变。非法描述符会直接报错。</p>
     *
     * @param source 高级字符串源码
     * @return 已解析的文本
     * @throws IllegalArgumentException 描述符无效、未闭合或源码超出长度限制时
     */
    public static LinText parse(String source) {
        String input = Objects.requireNonNullElse(source, "");
        Component compiled;
        synchronized (PARSED) {
            compiled = PARSED.get(input);
            if (compiled == null) {
                compiled = new AdvancedTextParser().parse(input);
                PARSED.put(input, compiled);
            }
        }
        return new LinText(compiled, input);
    }

    /**
     * {@link #parse(String)} 的别名。
     *
     * @param source 高级字符串源码
     * @return 已解析的文本
     */
    public static LinText of(String source) {
        return parse(source);
    }

    /**
     * 将值作为普通文本创建组件，不解析描述符或颜色代码。
     *
     * @param value 文本值，{@code null} 视为空字符串
     * @return 普通文本
     */
    public static LinText plain(Object value) {
        return new LinText(Component.text(String.valueOf(value == null ? "" : value)), null);
    }

    /**
     * 按顺序组合文本来源，此方法立即解析各部分。
     *
     * <p>若需要在语言切换后重新读取动态来源，请使用 {@link TextSource#join(TextSource...)}。</p>
     *
     * @param parts 文本来源；数组为 {@code null} 时返回空文本
     * @return 组合后的不可变文本
     */
    public static LinText join(TextSource... parts) {
        Builder builder = builder();
        if (parts != null) {
            for (TextSource part : parts) builder.append(part);
        }
        return builder.build();
    }

    /**
     * 创建按片段构造文本的构造器。
     *
     * @return 新构造器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 在当前文本末尾追加一个来源并返回新对象。
     *
     * @param next 追加的文本来源
     * @return 组合后的文本
     */
    public LinText append(TextSource next) {
        return join(this, next);
    }

    /**
     * 在组件树上绑定命名变量。
     *
     * @param args 变量集合；{@code null} 视为空集合
     * @return 绑定后的新文本
     */
    public LinText bind(TextArgs args) {
        return bind(new TextContext("", args));
    }

    /**
     * 在指定语言和变量上下文中绑定文本。
     *
     * <p>普通变量仅作为文本插入，不会重新解析成描述符；可信的 {@link TextSource}
     * 变量可以携带自身样式。绑定不会修改当前对象。</p>
     *
     * @param context 解析上下文；{@code null} 视为空上下文
     * @return 绑定后的新文本
     */
    public LinText bind(TextContext context) {
        return new LinText(TextBindings.bind(component,
                context == null ? TextContext.empty() : context), null);
    }

    /**
     * 序列化为不含样式和事件的纯文本。
     *
     * @return 纯文本
     */
    public String toPlainText() {
        return PLAIN.serialize(component);
    }

    /**
     * 序列化为传统 {@code §} 颜色代码文本。
     *
     * <p>传统字符串不能保留点击、悬浮等组件事件。</p>
     *
     * @return 传统颜色代码文本
     */
    public String toLegacyText() {
        return LEGACY.serialize(component);
    }

    /**
     * 序列化为 Adventure 组件 JSON。
     *
     * @return 组件 JSON
     */
    public String toJson() {
        return GsonComponentSerializer.gson().serialize(component);
    }

    /**
     * 从 Adventure 组件 JSON 还原文本。
     *
     * @param json 组件 JSON
     * @return 还原后的文本
     */
    public static LinText fromJson(String json) {
        return new LinText(GsonComponentSerializer.gson().deserialize(json), null);
    }

    /**
     * 返回原始源码；没有源码的组件返回其纯文本表示。
     *
     * @return 源码或纯文本
     */
    public String source() {
        return source == null ? toPlainText() : source;
    }

    /**
     * 返回不可变的 Adventure 组件。
     *
     * @return 文本组件
     */
    public Component component() {
        return component;
    }

    @Override
    public LinText resolveText(TextContext context) {
        return bind(context);
    }

    @Override
    public String resolve() {
        return source();
    }

    @Override
    public String toString() {
        return toPlainText();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LinText text && component.equals(text.component);
    }

    @Override
    public int hashCode() {
        return component.hashCode();
    }

    /**
     * 使用代码构造文本片段。
     *
     * <p>样式和事件方法只作用于最后追加的片段；在追加任何片段前调用会抛出异常。</p>
     */
    public static final class Builder {
        private final List<Component> parts = new ArrayList<>();

        /**
         * 追加普通文本，不解析高级字符串语法。
         *
         * @param text 文本，{@code null} 视为空字符串
         * @return 当前构造器
         */
        public Builder append(String text) {
            parts.add(Component.text(Objects.requireNonNullElse(text, "")));
            return this;
        }

        /**
         * 立即解析并追加文本来源。
         *
         * @param text 文本来源
         * @return 当前构造器
         */
        public Builder append(TextSource text) {
            parts.add(Objects.requireNonNull(text, "text").resolveText(TextContext.empty()).component());
            return this;
        }

        /**
         * 为最后一个片段设置命名颜色或十六进制颜色。
         *
         * @param color 颜色名或 {@code #RRGGBB}
         * @return 当前构造器
         */
        public Builder color(String color) {
            return change(last -> last.color(AdvancedTextParser.color(color)));
        }

        /**
         * 将最后一个片段设为粗体。
         *
         * @return 当前构造器
         */
        public Builder bold() {
            return change(last -> last.decoration(TextDecoration.BOLD, true));
        }

        /**
         * 将最后一个片段设为斜体。
         *
         * @return 当前构造器
         */
        public Builder italic() {
            return change(last -> last.decoration(TextDecoration.ITALIC, true));
        }

        /**
         * 为最后一个片段设置悬浮文本。
         *
         * @param text 悬浮内容
         * @return 当前构造器
         */
        public Builder hover(TextSource text) {
            return change(last -> last.hoverEvent(HoverEvent.showText(
                    Objects.requireNonNull(text, "text").resolveText(TextContext.empty()).component())));
        }

        /**
         * 为最后一个片段设置点击执行命令的事件。
         *
         * @param command 命令文本
         * @return 当前构造器
         */
        public Builder runCommand(String command) {
            return change(last -> last.clickEvent(ClickEvent.runCommand(command)));
        }

        /**
         * 为最后一个片段设置点击建议命令的事件。
         *
         * @param command 命令文本
         * @return 当前构造器
         */
        public Builder suggestCommand(String command) {
            return change(last -> last.clickEvent(ClickEvent.suggestCommand(command)));
        }

        /**
         * 为最后一个片段设置点击打开网址的事件。
         *
         * @param url 网址
         * @return 当前构造器
         */
        public Builder openUrl(String url) {
            return change(last -> last.clickEvent(ClickEvent.openUrl(url)));
        }

        /**
         * 为最后一个片段设置点击复制文本的事件。
         *
         * @param value 要复制的文本
         * @return 当前构造器
         */
        public Builder copy(String value) {
            return change(last -> last.clickEvent(ClickEvent.copyToClipboard(value)));
        }

        /**
         * 创建不可变的组合文本。
         *
         * @return 构造完成的文本
         */
        public LinText build() {
            return new LinText(Component.empty().children(parts), null);
        }

        private Builder change(java.util.function.UnaryOperator<Component> action) {
            if (parts.isEmpty()) throw new IllegalStateException("先添加文本，再设置样式");
            int index = parts.size() - 1;
            parts.set(index, action.apply(parts.get(index)));
            return this;
        }
    }
}
