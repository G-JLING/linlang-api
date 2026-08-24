package api.linlang.messenger.transport;

import api.linlang.messenger.FallbackPolicy;
import api.linlang.messenger.MessageChannel;
import api.linlang.messenger.TitleTimes;
import api.linlang.text.LinText;

import java.util.Objects;

/**
 * 交给消息传输层的规范化消息。
 *
 * <p>其中的语言引用和变量已经解析，内容不再依赖语言对象。该模型不包含 Bukkit 类型，
 * 可由未来的网络模块序列化并发送到其他服务器。</p>
 */
public final class TransportMessage {

    private final MessageChannel channel;
    private final LinText content;
    private final LinText subtitle;
    private final TitleTimes titleTimes;
    private final FallbackPolicy fallbackPolicy;

    /**
     * 创建规范化消息。
     *
     * @param channel 消息通道
     * @param content 主消息内容
     * @param subtitle 副标题；非标题消息可以为 {@code null}
     * @param titleTimes 标题时长
     * @param fallbackPolicy 通道不受支持时的处理策略
     */
    public TransportMessage(MessageChannel channel, LinText content, LinText subtitle,
                            TitleTimes titleTimes, FallbackPolicy fallbackPolicy) {
        this.channel = Objects.requireNonNull(channel, "channel");
        this.content = Objects.requireNonNull(content, "content");
        this.subtitle = subtitle;
        this.titleTimes = Objects.requireNonNull(titleTimes, "titleTimes");
        this.fallbackPolicy = Objects.requireNonNull(fallbackPolicy, "fallbackPolicy");
        if (channel == MessageChannel.TITLE && subtitle == null) {
            throw new IllegalArgumentException("Title messages require a subtitle.");
        }
        if (channel != MessageChannel.TITLE && subtitle != null) {
            throw new IllegalArgumentException("Only title messages can contain a subtitle.");
        }
    }

    /**
     * 返回消息通道。
     *
     * @return 消息通道
     */
    public MessageChannel channel() {
        return channel;
    }

    /**
     * 返回已经规范化的主消息内容。
     *
     * @return 主消息内容
     */
    public LinText content() {
        return content;
    }

    /**
     * 返回副标题。
     *
     * @return 标题消息的副标题；其他通道为 {@code null}
     */
    public LinText subtitle() {
        return subtitle;
    }

    /**
     * 返回标题时长。
     *
     * @return 标题时长
     */
    public TitleTimes titleTimes() {
        return titleTimes;
    }

    /**
     * 返回通道不受支持时的处理策略。
     *
     * @return 降级策略
     */
    public FallbackPolicy fallbackPolicy() {
        return fallbackPolicy;
    }
}
