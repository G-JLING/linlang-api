package api.linlang.text;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 将后置描述符编译为平台无关的文本组件。
 */
final class AdvancedTextParser {

    private static final int MAX_SOURCE_LENGTH = 32768;
    private static final int MAX_HOVER_DEPTH = 8;
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    private static final List<String> RAINBOW = List.of(
            "#ff0000", "#ffff00", "#00ff00", "#00ffff", "#0000ff", "#ff00ff", "#ff0000");

    Component parse(String source) {
        return parse(source, 0);
    }

    private Component parse(String source, int depth) {
        if (source.length() > MAX_SOURCE_LENGTH) {
            throw new IllegalArgumentException("LIN-TEXT-SOURCE-TOO-LONG: length=" + source.length());
        }
        if (depth > MAX_HOVER_DEPTH) {
            throw new IllegalArgumentException("高级字符串悬浮内容嵌套过深");
        }
        List<Component> parts = new ArrayList<>();
        StringBuilder pending = new StringBuilder();
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '\\' && index + 1 < source.length()) {
                char escaped = source.charAt(index + 1);
                if (escaped == '[' || escaped == ']' || escaped == '\\') {
                    pending.append(escaped);
                    index += 2;
                    continue;
                }
            }
            if (source.startsWith("[]", index)) {
                flush(parts, pending, "", depth);
                index += 2;
                continue;
            }
            if (source.startsWith("[@", index)) {
                int end = descriptorEnd(source, index + 2);
                if (end < 0) throw new IllegalArgumentException("描述符未闭合，位置 " + index);
                if (pending.length() == 0) {
                    throw new IllegalArgumentException("描述符前缺少文本，位置 " + index);
                }
                flush(parts, pending, source.substring(index + 2, end), depth);
                index = end + 1;
                continue;
            }
            pending.append(current);
            index++;
        }
        flush(parts, pending, "", depth);
        return Component.empty().children(parts);
    }

    private void flush(List<Component> parts, StringBuilder pending, String descriptor, int depth) {
        if (pending.length() == 0) return;
        String value = pending.toString();
        pending.setLength(0);
        List<String> gradient = List.of();
        boolean rainbow = false;
        boolean keybind = false;
        boolean translate = false;
        int clicks = 0;
        Component component = legacy(value);
        for (String raw : split(descriptor)) {
            String entry = raw.trim();
            if (entry.isEmpty()) continue;
            int separator = separator(entry);
            String key = (separator < 0 ? entry : entry.substring(0, separator))
                    .trim().toLowerCase(Locale.ROOT);
            String argument = separator < 0 ? "true" : decode(entry.substring(separator + 1).trim());
            switch (key) {
                case "color" -> component = component.color(color(argument));
                case "bold" -> component = component.decoration(TextDecoration.BOLD, bool(key, argument));
                case "italic" -> component = component.decoration(TextDecoration.ITALIC, bool(key, argument));
                case "underlined" -> component = component.decoration(TextDecoration.UNDERLINED, bool(key, argument));
                case "strikethrough" -> component = component.decoration(TextDecoration.STRIKETHROUGH, bool(key, argument));
                case "obfuscated" -> component = component.decoration(TextDecoration.OBFUSCATED, bool(key, argument));
                case "font" -> component = component.font(Key.key(required(key, argument)));
                case "insertion" -> component = component.insertion(argument);
                case "hover" -> component = component.hoverEvent(HoverEvent.showText(parse(argument, depth + 1)));
                case "gradient" -> gradient = colors(argument);
                case "rainbow" -> rainbow = bool(key, argument);
                case "keybind" -> keybind = bool(key, argument);
                case "translate" -> translate = bool(key, argument);
                case "click.run" -> { component = component.clickEvent(ClickEvent.runCommand(required(key, argument))); clicks++; }
                case "click.suggest" -> { component = component.clickEvent(ClickEvent.suggestCommand(required(key, argument))); clicks++; }
                case "click.copy" -> { component = component.clickEvent(ClickEvent.copyToClipboard(required(key, argument))); clicks++; }
                case "click.url" -> { component = component.clickEvent(ClickEvent.openUrl(required(key, argument))); clicks++; }
                default -> throw new IllegalArgumentException("未知的高级字符串描述符: " + key);
            }
        }
        if (clicks > 1) throw new IllegalArgumentException("同一片段只能设置一种点击行为");
        if (keybind && translate) throw new IllegalArgumentException("keybind 与 translate 不能同时使用");
        if (keybind || translate) {
            Component replacement = keybind ? Component.keybind(value) : Component.translatable(value);
            component = replacement.style(component.style());
        }
        if (!gradient.isEmpty() || rainbow) {
            component = gradient(PLAIN.serialize(component), component, rainbow ? RAINBOW : gradient);
        }
        parts.add(component);
    }

    private static Component legacy(String text) {
        if (!text.matches("(?s).*[&§][0-9a-fA-Fk-oK-OrR].*")) {
            return Component.text(text);
        }
        return LEGACY.deserialize(text.replace('§', '&'));
    }

    private static Component gradient(String text, Component styled, List<String> stops) {
        int[] points = text.codePoints().toArray();
        List<Component> parts = new ArrayList<>(points.length);
        for (int index = 0; index < points.length; index++) {
            double position = points.length == 1 ? 0 : (double) index / (points.length - 1);
            double scaled = position * (stops.size() - 1);
            int left = Math.min((int) scaled, stops.size() - 2);
            double fraction = scaled - left;
            int first = color(stops.get(left)).value();
            int second = color(stops.get(left + 1)).value();
            int red = interpolate(first >> 16 & 255, second >> 16 & 255, fraction);
            int green = interpolate(first >> 8 & 255, second >> 8 & 255, fraction);
            int blue = interpolate(first & 255, second & 255, fraction);
            parts.add(Component.text(new String(Character.toChars(points[index])))
                    .style(styled.style()).color(TextColor.color(red, green, blue)));
        }
        return Component.empty().children(parts);
    }

    private static int interpolate(int first, int second, double fraction) {
        return (int) Math.round(first + (second - first) * fraction);
    }

    static TextColor color(String input) {
        String value = required("color", input).toLowerCase(Locale.ROOT).replace('-', '_');
        TextColor result = value.startsWith("#")
                ? TextColor.fromHexString(value) : NamedTextColor.NAMES.value(value);
        if (result == null) throw new IllegalArgumentException("无效的颜色: " + input);
        return result;
    }

    private static List<String> colors(String value) {
        String[] raw = required("gradient", value).split(",");
        if (raw.length < 2) throw new IllegalArgumentException("渐变至少需要两种颜色");
        List<String> result = new ArrayList<>(raw.length);
        for (String entry : raw) {
            color(entry.trim());
            result.add(entry.trim());
        }
        return List.copyOf(result);
    }

    private static String required(String key, String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(key + " 缺少值");
        return value;
    }

    private static boolean bool(String key, String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException(key + " 只能是 true 或 false");
    }

    private static int descriptorEnd(String source, int start) {
        boolean quoted = false;
        boolean escaped = false;
        for (int index = start; index < source.length(); index++) {
            char current = source.charAt(index);
            if (escaped) { escaped = false; continue; }
            if (current == '\\') { escaped = true; continue; }
            if (current == '"') { quoted = !quoted; continue; }
            if (current == ']' && !quoted) return index;
        }
        return -1;
    }

    private static List<String> split(String descriptor) {
        List<String> entries = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        boolean escaped = false;
        for (int index = 0; index < descriptor.length(); index++) {
            char c = descriptor.charAt(index);
            if (escaped) { current.append(c); escaped = false; continue; }
            if (c == '\\') { current.append(c); escaped = true; continue; }
            if (c == '"') quoted = !quoted;
            if (c == ';' && !quoted) {
                entries.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (quoted) throw new IllegalArgumentException("描述符引号未闭合");
        entries.add(current.toString());
        return entries;
    }

    private static int separator(String entry) {
        boolean quoted = false;
        for (int index = 0; index < entry.length(); index++) {
            char c = entry.charAt(index);
            if (c == '"' && (index == 0 || entry.charAt(index - 1) != '\\')) quoted = !quoted;
            if (c == '=' && !quoted) return index;
        }
        return -1;
    }

    private static String decode(String value) {
        String input = value.length() >= 2 && value.charAt(0) == '"'
                && value.charAt(value.length() - 1) == '"'
                ? value.substring(1, value.length() - 1) : value;
        StringBuilder decoded = new StringBuilder();
        boolean escaped = false;
        for (int index = 0; index < input.length(); index++) {
            char c = input.charAt(index);
            if (escaped) { decoded.append(c); escaped = false; }
            else if (c == '\\') escaped = true;
            else decoded.append(c);
        }
        if (escaped) decoded.append('\\');
        return decoded.toString();
    }
}
