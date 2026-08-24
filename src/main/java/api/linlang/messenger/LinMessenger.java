package api.linlang.messenger;

import api.linlang.messenger.transport.MessageTransport;
import api.linlang.text.TextArgs;
import api.linlang.text.TextSource;

import java.util.Map;

/**
 * Linlang 的一次性文本消息投递服务。
 *
 * <p>普通聊天、动作栏和标题消息均可通过便捷方法在一条语句内发送。
 * 需要覆盖默认投递策略时，可以创建 {@link LinMessage} 后交给 {@link #send(Object, LinMessage)}。</p>
 */
public interface LinMessenger {

    /**
     * 投递完整消息定义。
     *
     * @param recipient 接收者
     * @param message 消息定义
     */
    void send(Object recipient, LinMessage message);

    /**
     * 注册消息传输层。
     *
     * <p>相同标识的传输层应替换旧实现。该入口主要供运行时扩展与未来网络模块使用。</p>
     *
     * @param transport 消息传输层
     * @return 当前消息服务
     */
    LinMessenger registerTransport(MessageTransport transport);

    /**
     * 移除指定消息传输层。
     *
     * @param id 传输层标识
     * @return 找到并移除时为 {@code true}
     */
    boolean unregisterTransport(String id);

    /**
     * 发送聊天高级字符串。
     *
     * @param recipient 接收者
     * @param text 高级字符串源码
     * @param args 名称和值交替排列的变量
     */
    default void send(Object recipient, String text, Object... args) {
        send(recipient, LinMessage.chat(text).args(args));
    }

    /**
     * 发送聊天文本来源。
     *
     * @param recipient 接收者
     * @param text 文本来源
     * @param args 名称和值交替排列的变量
     */
    default void send(Object recipient, TextSource text, Object... args) {
        send(recipient, LinMessage.chat(text).args(args));
    }

    /**
     * 使用变量映射发送聊天高级字符串。
     *
     * @param recipient 接收者
     * @param text 高级字符串源码
     * @param args 变量映射
     */
    default void send(Object recipient, String text, Map<String, ?> args) {
        send(recipient, LinMessage.chat(text).args(args));
    }

    /**
     * 使用变量映射发送聊天文本来源。
     *
     * @param recipient 接收者
     * @param text 文本来源
     * @param args 变量映射
     */
    default void send(Object recipient, TextSource text, Map<String, ?> args) {
        send(recipient, LinMessage.chat(text).args(args));
    }

    /**
     * 发送动作栏高级字符串。
     *
     * @param recipient 接收者
     * @param text 高级字符串源码
     * @param args 名称和值交替排列的变量
     */
    default void actionBar(Object recipient, String text, Object... args) {
        send(recipient, LinMessage.actionBar(text).args(args));
    }

    /**
     * 发送动作栏文本来源。
     *
     * @param recipient 接收者
     * @param text 文本来源
     * @param args 名称和值交替排列的变量
     */
    default void actionBar(Object recipient, TextSource text, Object... args) {
        send(recipient, LinMessage.actionBar(text).args(args));
    }

    /**
     * 使用变量映射发送动作栏高级字符串。
     *
     * @param recipient 接收者
     * @param text 高级字符串源码
     * @param args 变量映射
     */
    default void actionBar(Object recipient, String text, Map<String, ?> args) {
        send(recipient, LinMessage.actionBar(text).args(args));
    }

    /**
     * 使用变量映射发送动作栏文本来源。
     *
     * @param recipient 接收者
     * @param text 文本来源
     * @param args 变量映射
     */
    default void actionBar(Object recipient, TextSource text, Map<String, ?> args) {
        send(recipient, LinMessage.actionBar(text).args(args));
    }

    /**
     * 使用默认时长发送标题高级字符串。
     *
     * @param recipient 接收者
     * @param title 主标题高级字符串源码
     * @param subtitle 副标题高级字符串源码
     * @param args 名称和值交替排列的变量
     */
    default void title(Object recipient, String title, String subtitle, Object... args) {
        send(recipient, LinMessage.title(title, subtitle).args(args));
    }

    /**
     * 使用默认时长发送标题文本来源。
     *
     * @param recipient 接收者
     * @param title 主标题来源
     * @param subtitle 副标题来源
     * @param args 名称和值交替排列的变量
     */
    default void title(Object recipient, TextSource title, TextSource subtitle, Object... args) {
        send(recipient, LinMessage.title(title, subtitle).args(args));
    }

    /**
     * 使用指定时长发送标题高级字符串。
     *
     * @param recipient 接收者
     * @param title 主标题高级字符串源码
     * @param subtitle 副标题高级字符串源码
     * @param times 标题时长
     * @param args 名称和值交替排列的变量
     */
    default void title(Object recipient, String title, String subtitle,
                       TitleTimes times, Object... args) {
        send(recipient, LinMessage.title(title, subtitle).times(times).args(args));
    }

    /**
     * 使用指定时长发送标题文本来源。
     *
     * @param recipient 接收者
     * @param title 主标题来源
     * @param subtitle 副标题来源
     * @param times 标题时长
     * @param args 名称和值交替排列的变量
     */
    default void title(Object recipient, TextSource title, TextSource subtitle,
                       TitleTimes times, Object... args) {
        send(recipient, LinMessage.title(title, subtitle).times(times).args(args));
    }

    /**
     * 发送聊天文本模板。
     *
     * @deprecated 请使用 {@link #send(Object, String, Object...)}。
     */
    @Deprecated
    default void sendText(Object recipient, String template, Object... args) {
        send(recipient, template, args);
    }

    /**
     * 使用变量映射发送聊天文本模板。
     *
     * @deprecated 请使用 {@link #send(Object, String, Map)}。
     */
    @Deprecated
    default void sendText(Object recipient, String template, Map<String, ?> args) {
        send(recipient, template, args);
    }

    /**
     * 通过语言路径键发送聊天消息。
     *
     * @deprecated 请把 {@code LangText} 字段直接传给 {@link #send(Object, TextSource, Object...)}。
     */
    @Deprecated
    default void sendKey(Object recipient, String key, Object... args) {
        throw legacyLanguageKeysUnsupported();
    }

    /**
     * 通过语言路径键发送聊天消息。
     *
     * @deprecated 请把 {@code LangText} 字段直接传给 {@link #send(Object, TextSource, Map)}。
     */
    @Deprecated
    default void sendKey(Object recipient, String key, Map<String, ?> args) {
        throw legacyLanguageKeysUnsupported();
    }

    /**
     * 发送标题文本模板。
     *
     * @deprecated 请使用 {@link #title(Object, String, String, TitleTimes, Object...)}。
     */
    @Deprecated
    default void sendTitleText(Object recipient, String title, String subtitle,
                               int fadeIn, int stay, int fadeOut, Object... args) {
        send(recipient, LinMessage.title(title, subtitle)
                .times(fadeIn, stay, fadeOut)
                .args(args)
                .fallback(FallbackPolicy.CHAT));
    }

    /**
     * 使用变量映射发送标题文本模板。
     *
     * @deprecated 请创建 {@link LinMessage} 并传入变量映射。
     */
    @Deprecated
    default void sendTitleText(Object recipient, String title, String subtitle,
                               int fadeIn, int stay, int fadeOut, Map<String, ?> args) {
        send(recipient, LinMessage.title(title, subtitle)
                .times(fadeIn, stay, fadeOut)
                .args(args)
                .fallback(FallbackPolicy.CHAT));
    }

    /**
     * 通过语言路径键发送标题。
     *
     * @deprecated 请把 {@code LangText} 字段直接传给标题方法。
     */
    @Deprecated
    default void sendTitleKey(Object recipient, String titleKey, String subtitleKey,
                              int fadeIn, int stay, int fadeOut, Object... args) {
        throw legacyLanguageKeysUnsupported();
    }

    /**
     * 通过语言路径键发送标题。
     *
     * @deprecated 请把 {@code LangText} 字段直接传给标题方法。
     */
    @Deprecated
    default void sendTitleKey(Object recipient, String titleKey, String subtitleKey,
                              int fadeIn, int stay, int fadeOut, Map<String, ?> args) {
        throw legacyLanguageKeysUnsupported();
    }

    /**
     * 发送动作栏文本模板。
     *
     * @deprecated 请使用 {@link #actionBar(Object, String, Object...)}。
     */
    @Deprecated
    default void sendActionBarText(Object recipient, String template, Object... args) {
        actionBar(recipient, template, args);
    }

    /**
     * 使用变量映射发送动作栏文本模板。
     *
     * @deprecated 请使用 {@link #actionBar(Object, String, Map)}。
     */
    @Deprecated
    default void sendActionBarText(Object recipient, String template, Map<String, ?> args) {
        actionBar(recipient, template, args);
    }

    /**
     * 通过语言路径键发送动作栏消息。
     *
     * @deprecated 请把 {@code LangText} 字段直接传给动作栏方法。
     */
    @Deprecated
    default void sendActionBarKey(Object recipient, String key, Object... args) {
        throw legacyLanguageKeysUnsupported();
    }

    /**
     * 通过语言路径键发送动作栏消息。
     *
     * @deprecated 请把 {@code LangText} 字段直接传给动作栏方法。
     */
    @Deprecated
    default void sendActionBarKey(Object recipient, String key, Map<String, ?> args) {
        throw legacyLanguageKeysUnsupported();
    }

    private static UnsupportedOperationException legacyLanguageKeysUnsupported() {
        return new UnsupportedOperationException("Language path keys are not supported by this messenger.");
    }

    /**
     * 旧版模板变量工具。
     *
     * @deprecated 请使用 {@link TextArgs#of(Object...)}。
     */
    @Deprecated
    final class Vars {

        private Vars() {
        }

        /**
         * 通过名称和值交替排列的参数创建变量映射。
         *
         * @param keyValues 名称和值交替排列的参数
         * @return 不可变变量映射
         */
        public static Map<String, Object> of(Object... keyValues) {
            return TextArgs.of(keyValues).values();
        }
    }
}
