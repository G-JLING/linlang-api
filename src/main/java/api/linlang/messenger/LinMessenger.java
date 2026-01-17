package api.linlang.messenger;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 消息发送者
 */
public interface LinMessenger {


    void sendText(Object recipient, String template, Object... kv);
    void sendKey(Object recipient, String key, Object... kv);

    void sendText(Object recipient, String template, Map<String, ?> vars);
    void sendKey(Object recipient, String key, Map<String, ?> vars);

    /* Title（模板 / 语言键） */

    void sendTitleText(Object recipient, String title, String subtitle,
                       int fadeIn, int stay, int fadeOut, Object... kv);

    void sendTitleKey(Object recipient, String titleKey, String subKey,
                      int fadeIn, int stay, int fadeOut, Object... kv);

    void sendTitleText(Object recipient, String title, String subtitle,
                       int fadeIn, int stay, int fadeOut, Map<String, ?> vars);

    void sendTitleKey(Object recipient, String titleKey, String subKey,
                      int fadeIn, int stay, int fadeOut, Map<String, ?> vars);

    /* ActionBar（模板 / 语言键） */

    void sendActionBarText(Object recipient, String template, Object... kv);
    void sendActionBarKey(Object recipient, String key, Object... kv);
    void sendActionBarText(Object recipient, String template, Map<String, ?> vars);
    void sendActionBarKey(Object recipient, String key, Map<String, ?> vars);

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