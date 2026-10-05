package api.linlang.messenger;

import api.linlang.messenger.transport.MessageTransport;
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
     * <p>无法规范化、选择传输器或完成投递时，运行时通过 Problem 系统记录故障，
     * 不再把传输层异常传播到业务调用栈。</p>
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

}
