package api.linlang.audit.log;

/**
 * 普通日志的投递通道。
 *
 * <p>通道只决定日志的用途和投递位置，不代表严重程度。</p>
 */
public enum LogChannel {
    STANDARD,
    INIT,
    OP,
    STARTUP,
    BANNER
}
