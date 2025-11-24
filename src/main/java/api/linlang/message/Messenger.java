package api.linlang.message;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 平台无关的消息发送门面。
 * <p>约定：占位符语法为 {@code {key}}；颜色由各平台适配实现（如 & 转 §，或 HEX）。</p>
 * <p>目标对象（玩家/控制台/发送者）以 {@code Object recipient} 传入，由具体适配器判定类型。</p>
 */
public interface Messenger {

    /* 配置 */

    /** 设置固定前缀（仅用于聊天/控制台；Title/ActionBar 不附带前缀）。 */
    Messenger withPrefix(String prefix);

    /** 设置动态前缀提供者（运行时计算；仅用于聊天/控制台）。 */
    Messenger withPrefixProvider(Supplier<String> supplier);

    /* 文本（模板） */

    void sendText(Object recipient, String template, Object... kv);
    void sendText(Object recipient, String template, Map<String, ?> vars);

    /* 文本（语言键） */

    void sendKey(Object recipient, String key, Object... kv);
    void sendKey(Object recipient, String key, Map<String, ?> vars);

    /* Title（模板 / 语言键） */

    void sendTitleText(Object recipient, String title, String subtitle,
                       int fadeIn, int stay, int fadeOut, Object... kv);

    void sendTitleText(Object recipient, String title, String subtitle,
                       int fadeIn, int stay, int fadeOut, Map<String, ?> vars);

    void sendTitleKey(Object recipient, String titleKey, String subKey,
                      int fadeIn, int stay, int fadeOut, Object... kv);

    void sendTitleKey(Object recipient, String titleKey, String subKey,
                      int fadeIn, int stay, int fadeOut, Map<String, ?> vars);

    /* ActionBar（模板 / 语言键） */

    void sendActionBarText(Object recipient, String template, Object... kv);
    void sendActionBarText(Object recipient, String template, Map<String, ?> vars);

    void sendActionBarKey(Object recipient, String key, Object... kv);
    void sendActionBarKey(Object recipient, String key, Map<String, ?> vars);

    /* 工具：可变参数转 Map（与适配实现保持一致的便捷方法） */
    final class Vars {
        public static Map<String, Object> of(Object... kv) {
            if (kv == null || kv.length == 0) return java.util.Map.of();
            if ((kv.length & 1) == 1) throw new IllegalArgumentException("odd kv length");
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            for (int i = 0; i < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
            return m;
        }
    }
}