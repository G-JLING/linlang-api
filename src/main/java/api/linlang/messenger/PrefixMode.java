package api.linlang.messenger;

/**
 * 消息前缀策略。
 */
public enum PrefixMode {
    /**
     * 聊天消息使用前缀，标题与动作栏不使用前缀。
     */
    AUTO,

    /**
     * 明确使用前缀。
     */
    INCLUDE,

    /**
     * 明确省略前缀。
     */
    OMIT
}
