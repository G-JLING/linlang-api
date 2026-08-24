package api.linlang.messenger;

/**
 * 接收者不支持目标消息通道时的处理策略。
 */
public enum FallbackPolicy {
    /**
     * 拒绝投递并报告调用错误。
     */
    REJECT,

    /**
     * 降级为聊天消息。
     */
    CHAT,

    /**
     * 忽略本次投递。
     */
    IGNORE
}
