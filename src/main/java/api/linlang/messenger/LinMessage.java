package api.linlang.messenger;

import api.linlang.text.LinText;
import api.linlang.text.TextArgs;
import api.linlang.text.TextSource;

import java.util.Map;
import java.util.Objects;

/**
 * 可复用的一次性消息定义。
 *
 * <p>普通发送场景应优先使用 {@link LinMessenger} 的便捷方法。只有需要覆盖前缀、降级或标题时长时，
 * 才需要直接构造该对象。</p>
 */
public final class LinMessage {

    private final MessageChannel channel;
    private final TextSource content;
    private final TextSource subtitle;
    private final TextArgs args;
    private final PrefixMode prefixMode;
    private final FallbackPolicy fallbackPolicy;
    private final TitleTimes titleTimes;

    private LinMessage(MessageChannel channel, TextSource content, TextSource subtitle,
                       TextArgs args, PrefixMode prefixMode, FallbackPolicy fallbackPolicy,
                       TitleTimes titleTimes) {
        this.channel = Objects.requireNonNull(channel, "channel");
        this.content = Objects.requireNonNull(content, "content");
        this.subtitle = subtitle;
        this.args = Objects.requireNonNull(args, "args");
        this.prefixMode = Objects.requireNonNull(prefixMode, "prefixMode");
        this.fallbackPolicy = Objects.requireNonNull(fallbackPolicy, "fallbackPolicy");
        this.titleTimes = Objects.requireNonNull(titleTimes, "titleTimes");
    }

    /**
     * 创建聊天消息。
     *
     * @param content 消息内容
     * @return 聊天消息
     */
    public static LinMessage chat(TextSource content) {
        return create(MessageChannel.CHAT, content, null);
    }

    /**
     * 创建聊天消息。
     *
     * @param content 高级字符串源码
     * @return 聊天消息
     */
    public static LinMessage chat(String content) {
        return chat(LinText.of(content));
    }

    /**
     * 创建动作栏消息。
     *
     * @param content 消息内容
     * @return 动作栏消息
     */
    public static LinMessage actionBar(TextSource content) {
        return create(MessageChannel.ACTION_BAR, content, null);
    }

    /**
     * 创建动作栏消息。
     *
     * @param content 高级字符串源码
     * @return 动作栏消息
     */
    public static LinMessage actionBar(String content) {
        return actionBar(LinText.of(content));
    }

    /**
     * 创建标题消息。
     *
     * @param title 主标题
     * @param subtitle 副标题
     * @return 标题消息
     */
    public static LinMessage title(TextSource title, TextSource subtitle) {
        return create(MessageChannel.TITLE, title, Objects.requireNonNull(subtitle, "subtitle"));
    }

    /**
     * 创建标题消息。
     *
     * @param title 主标题高级字符串源码
     * @param subtitle 副标题高级字符串源码
     * @return 标题消息
     */
    public static LinMessage title(String title, String subtitle) {
        return title(LinText.of(title), LinText.of(subtitle));
    }

    private static LinMessage create(MessageChannel channel, TextSource content, TextSource subtitle) {
        return new LinMessage(channel, content, subtitle, TextArgs.empty(), PrefixMode.AUTO,
                FallbackPolicy.REJECT, TitleTimes.defaults());
    }

    /**
     * 设置名称和值交替排列的变量。
     *
     * @param keyValues 名称和值交替排列的变量
     * @return 新消息对象
     */
    public LinMessage args(Object... keyValues) {
        return args(TextArgs.of(keyValues));
    }

    /**
     * 设置变量映射。
     *
     * @param values 变量映射
     * @return 新消息对象
     */
    public LinMessage args(Map<String, ?> values) {
        return args(TextArgs.from(values));
    }

    /**
     * 设置变量集合。
     *
     * @param args 变量集合
     * @return 新消息对象
     */
    public LinMessage args(TextArgs args) {
        return copy(Objects.requireNonNull(args, "args"), prefixMode, fallbackPolicy, titleTimes);
    }

    /**
     * 设置前缀策略。
     *
     * @param mode 前缀策略
     * @return 新消息对象
     */
    public LinMessage prefix(PrefixMode mode) {
        return copy(args, Objects.requireNonNull(mode, "mode"), fallbackPolicy, titleTimes);
    }

    /**
     * 明确省略前缀。
     *
     * @return 新消息对象
     */
    public LinMessage withoutPrefix() {
        return prefix(PrefixMode.OMIT);
    }

    /**
     * 设置通道不受支持时的处理策略。
     *
     * @param policy 降级策略
     * @return 新消息对象
     */
    public LinMessage fallback(FallbackPolicy policy) {
        return copy(args, prefixMode, Objects.requireNonNull(policy, "policy"), titleTimes);
    }

    /**
     * 设置标题显示时长。
     *
     * @param times 标题时长
     * @return 新消息对象
     * @throws IllegalStateException 当前消息不是标题时
     */
    public LinMessage times(TitleTimes times) {
        if (channel != MessageChannel.TITLE) {
            throw new IllegalStateException("Title times can only be set on title messages.");
        }
        return copy(args, prefixMode, fallbackPolicy, Objects.requireNonNull(times, "times"));
    }

    /**
     * 设置标题显示时长。
     *
     * @param fadeIn 淡入时长
     * @param stay 停留时长
     * @param fadeOut 淡出时长
     * @return 新消息对象
     */
    public LinMessage times(int fadeIn, int stay, int fadeOut) {
        return times(TitleTimes.of(fadeIn, stay, fadeOut));
    }

    private LinMessage copy(TextArgs args, PrefixMode prefixMode,
                            FallbackPolicy fallbackPolicy, TitleTimes titleTimes) {
        return new LinMessage(channel, content, subtitle, args, prefixMode, fallbackPolicy, titleTimes);
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
     * 返回主消息内容来源。
     *
     * @return 主消息内容来源
     */
    public TextSource content() {
        return content;
    }

    /**
     * 返回副标题来源。
     *
     * @return 标题消息的副标题来源；其他通道为 {@code null}
     */
    public TextSource subtitle() {
        return subtitle;
    }

    /**
     * 返回消息变量。
     *
     * @return 不可变变量集合
     */
    public TextArgs args() {
        return args;
    }

    /**
     * 返回前缀策略。
     *
     * @return 前缀策略
     */
    public PrefixMode prefixMode() {
        return prefixMode;
    }

    /**
     * 返回通道不受支持时的处理策略。
     *
     * @return 降级策略
     */
    public FallbackPolicy fallbackPolicy() {
        return fallbackPolicy;
    }

    /**
     * 返回标题时长。
     *
     * @return 标题时长
     */
    public TitleTimes titleTimes() {
        return titleTimes;
    }
}
