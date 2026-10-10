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
 * 不可变的高级字符串，由可组合的文本组件构成。
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

    public static LinText of(String source) {
        return parse(source);
    }

    public static LinText plain(Object value) {
        return new LinText(Component.text(String.valueOf(value == null ? "" : value)), null);
    }

    public static LinText join(TextSource... parts) {
        Builder builder = builder();
        if (parts != null) {
            for (TextSource part : parts) builder.append(part);
        }
        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public LinText append(TextSource next) {
        return join(this, next);
    }

    public LinText bind(TextArgs args) {
        return bind(new TextContext("", args));
    }

    public LinText bind(TextContext context) {
        return new LinText(TextBindings.bind(component,
                context == null ? TextContext.empty() : context), null);
    }

    public String toPlainText() {
        return PLAIN.serialize(component);
    }

    public String toLegacyText() {
        return LEGACY.serialize(component);
    }

    public String toJson() {
        return GsonComponentSerializer.gson().serialize(component);
    }

    public static LinText fromJson(String json) {
        return new LinText(GsonComponentSerializer.gson().deserialize(json), null);
    }

    public String source() {
        return source == null ? toPlainText() : source;
    }

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
     */
    public static final class Builder {
        private final List<Component> parts = new ArrayList<>();

        public Builder append(String text) {
            parts.add(Component.text(Objects.requireNonNullElse(text, "")));
            return this;
        }

        public Builder append(TextSource text) {
            parts.add(Objects.requireNonNull(text, "text").resolveText(TextContext.empty()).component());
            return this;
        }

        public Builder color(String color) {
            return change(last -> last.color(AdvancedTextParser.color(color)));
        }

        public Builder bold() {
            return change(last -> last.decoration(TextDecoration.BOLD, true));
        }

        public Builder italic() {
            return change(last -> last.decoration(TextDecoration.ITALIC, true));
        }

        public Builder hover(TextSource text) {
            return change(last -> last.hoverEvent(HoverEvent.showText(
                    Objects.requireNonNull(text, "text").resolveText(TextContext.empty()).component())));
        }

        public Builder runCommand(String command) {
            return change(last -> last.clickEvent(ClickEvent.runCommand(command)));
        }

        public Builder suggestCommand(String command) {
            return change(last -> last.clickEvent(ClickEvent.suggestCommand(command)));
        }

        public Builder openUrl(String url) {
            return change(last -> last.clickEvent(ClickEvent.openUrl(url)));
        }

        public Builder copy(String value) {
            return change(last -> last.clickEvent(ClickEvent.copyToClipboard(value)));
        }

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
