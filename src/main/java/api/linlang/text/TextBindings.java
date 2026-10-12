package api.linlang.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 在组件树上绑定命名变量，避免将普通变量重新解析为描述符。
 */
final class TextBindings {

    private TextBindings() {
    }

    static Component bind(Component input, TextContext context) {
        Map<String, Object> values = context.args().values();
        if (values.isEmpty()) return input;

        Component result = input;
        ClickEvent click = result.clickEvent();
        if (click != null) {
            result = result.clickEvent(ClickEvent.clickEvent(click.action(),
                    replacePlain(click.value(), values, context)));
        }
        HoverEvent<?> hover = result.hoverEvent();
        if (hover != null && hover.action() == HoverEvent.Action.SHOW_TEXT) {
            result = result.hoverEvent(HoverEvent.showText(bind((Component) hover.value(), context)));
        }

        List<Component> children = new ArrayList<>();
        if (result instanceof TextComponent text) {
            children.addAll(replace(text.content(), values, context));
            result = text.content("");
        }
        for (Component child : result.children()) children.add(bind(child, context));
        return result.children(children);
    }

    private static List<Component> replace(String input, Map<String, Object> values, TextContext context) {
        List<Component> result = new ArrayList<>();
        StringBuilder plain = new StringBuilder();
        int index = 0;
        while (index < input.length()) {
            if (input.startsWith("{{", index)) {
                plain.append('{');
                index += 2;
                continue;
            }
            if (input.charAt(index) == '{') {
                int end = input.indexOf('}', index + 1);
                if (end > index) {
                    String key = input.substring(index + 1, end);
                    Object value = values.get(key);
                    if (validKey(key) && value != null) {
                        if (plain.length() > 0) {
                            result.add(Component.text(plain.toString()));
                            plain.setLength(0);
                        }
                        result.add(value instanceof TextSource rich
                                ? rich.resolveText(new TextContext(context.locale(), TextArgs.empty())).component()
                                : Component.text(String.valueOf(value)));
                        index = end + 1;
                        continue;
                    }
                }
            }
            plain.append(input.charAt(index++));
        }
        if (plain.length() > 0) result.add(Component.text(plain.toString()));
        return result;
    }

    private static String replacePlain(String input, Map<String, Object> values, TextContext context) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < input.length()) {
            if (input.startsWith("{{", index)) {
                result.append('{');
                index += 2;
                continue;
            }
            if (input.charAt(index) == '{') {
                int end = input.indexOf('}', index + 1);
                if (end > index) {
                    String key = input.substring(index + 1, end);
                    Object value = values.get(key);
                    if (validKey(key) && value != null) {
                        result.append(value instanceof TextSource rich
                                ? rich.resolveText(new TextContext(context.locale(), TextArgs.empty())).toPlainText()
                                : value);
                        index = end + 1;
                        continue;
                    }
                }
            }
            result.append(input.charAt(index++));
        }
        return result.toString();
    }

    private static boolean validKey(String key) {
        if (key.isEmpty()) return false;
        for (int index = 0; index < key.length(); index++) {
            char c = key.charAt(index);
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '.') return false;
        }
        return true;
    }
}
