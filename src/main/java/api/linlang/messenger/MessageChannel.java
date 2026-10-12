package api.linlang.messenger;

/**
 * 一次性文本消息的投递通道。
 */
public enum MessageChannel {
    /**
     * 聊天消息。
     */
    CHAT,
    /**
     * 动作栏消息。
     */
    ACTION_BAR,
    /**
     * 屏幕标题消息。
     */
    TITLE
}
