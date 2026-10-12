package api.linlang.audit.log;

/**
 * 普通日志的投递通道。
 *
 * <p>通道只决定日志的用途和投递位置，不代表严重程度。</p>
 */
public enum LogChannel {
    /**
     * 普通日志通道。
     */
    STANDARD,
    /**
     * 仅写入文件的日志通道。
     */
    FILE,
    /**
     * 初始化阶段日志通道。
     */
    INIT,
    /**
     * 管理员消息通道。
     */
    OP,
    /**
     * 启动阶段日志通道。
     */
    STARTUP,
    /**
     * 启动横幅通道。
     */
    BANNER
}
