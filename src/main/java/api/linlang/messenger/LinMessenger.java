package api.linlang.messenger;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 与平台无关的消息发送服务。
 *
 * <p>{@code Text} 方法直接渲染模板文本，{@code Key} 方法先通过语言服务解析路径键。
 * {@code kv} 参数按“名称、值”成对传入；Map 重载适合已经组织好的变量集合。</p>
 */
public interface LinMessenger {

    /**
     * 发送聊天文本模板。
     *
     * @param recipient 平台接收者
     * @param template 文本模板
     * @param kv 交替排列的变量名和值
     */
    void sendText(Object recipient, String template, Object... kv);

    /**
     * 发送语言键对应的聊天文本。
     *
     * @param recipient 平台接收者
     * @param key 语言路径键
     * @param kv 交替排列的变量名和值
     */
    void sendKey(Object recipient, String key, Object... kv);

    /**
     * 使用变量映射发送聊天文本模板。
     *
     * @param recipient 平台接收者
     * @param template 文本模板
     * @param vars 模板变量
     */
    void sendText(Object recipient, String template, Map<String, ?> vars);

    /**
     * 使用变量映射发送语言键对应的聊天文本。
     *
     * @param recipient 平台接收者
     * @param key 语言路径键
     * @param vars 模板变量
     */
    void sendKey(Object recipient, String key, Map<String, ?> vars);

    /* Title（模板 / 语言键） */

    /**
     * 发送标题文本模板。
     *
     * @param recipient 平台接收者
     * @param title 主标题模板
     * @param subtitle 副标题模板
     * @param fadeIn 淡入时长
     * @param stay 停留时长
     * @param fadeOut 淡出时长
     * @param kv 交替排列的变量名和值
     */
    void sendTitleText(Object recipient, String title, String subtitle,
                       int fadeIn, int stay, int fadeOut, Object... kv);

    /**
     * 发送语言键对应的标题。
     *
     * @param recipient 平台接收者
     * @param titleKey 主标题语言键
     * @param subKey 副标题语言键
     * @param fadeIn 淡入时长
     * @param stay 停留时长
     * @param fadeOut 淡出时长
     * @param kv 交替排列的变量名和值
     */
    void sendTitleKey(Object recipient, String titleKey, String subKey,
                      int fadeIn, int stay, int fadeOut, Object... kv);

    /**
     * 使用变量映射发送标题文本模板。
     *
     * @param recipient 平台接收者
     * @param title 主标题模板
     * @param subtitle 副标题模板
     * @param fadeIn 淡入时长
     * @param stay 停留时长
     * @param fadeOut 淡出时长
     * @param vars 模板变量
     */
    void sendTitleText(Object recipient, String title, String subtitle,
                       int fadeIn, int stay, int fadeOut, Map<String, ?> vars);

    /**
     * 使用变量映射发送语言键对应的标题。
     *
     * @param recipient 平台接收者
     * @param titleKey 主标题语言键
     * @param subKey 副标题语言键
     * @param fadeIn 淡入时长
     * @param stay 停留时长
     * @param fadeOut 淡出时长
     * @param vars 模板变量
     */
    void sendTitleKey(Object recipient, String titleKey, String subKey,
                      int fadeIn, int stay, int fadeOut, Map<String, ?> vars);

    /* ActionBar（模板 / 语言键） */

    /**
     * 发送动作栏文本模板。
     *
     * @param recipient 平台接收者
     * @param template 文本模板
     * @param kv 交替排列的变量名和值
     */
    void sendActionBarText(Object recipient, String template, Object... kv);

    /**
     * 发送语言键对应的动作栏文本。
     *
     * @param recipient 平台接收者
     * @param key 语言路径键
     * @param kv 交替排列的变量名和值
     */
    void sendActionBarKey(Object recipient, String key, Object... kv);

    /**
     * 使用变量映射发送动作栏文本模板。
     *
     * @param recipient 平台接收者
     * @param template 文本模板
     * @param vars 模板变量
     */
    void sendActionBarText(Object recipient, String template, Map<String, ?> vars);

    /**
     * 使用变量映射发送语言键对应的动作栏文本。
     *
     * @param recipient 平台接收者
     * @param key 语言路径键
     * @param vars 模板变量
     */
    void sendActionBarKey(Object recipient, String key, Map<String, ?> vars);

    /**
     * 模板变量映射工具。
     */
    final class Vars {
        /**
         * 通过“名称、值”对构造变量映射。
         *
         * @param kv 交替排列的变量名和值
         * @return 保持输入顺序的变量映射
         * @throws IllegalArgumentException 参数数量为奇数时
         */
        public static Map<String, Object> of(Object... kv) {
            if (kv == null || kv.length == 0) return java.util.Map.of();
            if ((kv.length & 1) == 1) throw new IllegalArgumentException("odd kv length");
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            for (int i = 0; i < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
            return m;
        }
    }
}
