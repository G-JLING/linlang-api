package api.linlang.audit.log;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

/**
 * 在日志门面与运行时 Provider 之间传递的不可变日志记录。
 *
 * @hidden
 */
public final class LogRecord {

    private final Instant timestamp;
    private final LogLevel level;
    private final LogChannel channel;
    private final String message;
    private final Object[] arguments;
    private final Throwable cause;

    private LogRecord(Instant timestamp,
                      LogLevel level,
                      LogChannel channel,
                      String message,
                      Throwable cause,
                      Object[] arguments) {
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.level = Objects.requireNonNull(level, "level");
        this.channel = Objects.requireNonNull(channel, "channel");
        this.message = message == null ? "" : message;
        this.cause = cause;
        this.arguments = arguments == null ? new Object[0] : arguments.clone();
    }

    /**
     * 创建一条普通日志记录。
     *
     * @param level     日志等级
     * @param channel   投递通道
     * @param message   消息模板
     * @param cause     异常原因
     * @param arguments 占位参数或扩展字段
     * @return 新日志记录
     * @hidden
     */
    public static LogRecord of(LogLevel level,
                               LogChannel channel,
                               String message,
                               Throwable cause,
                               Object... arguments) {
        return new LogRecord(Instant.now(), level, channel, message, cause, arguments);
    }

    public Instant timestamp() {
        return timestamp;
    }

    public LogLevel level() {
        return level;
    }

    public LogChannel channel() {
        return channel;
    }

    public String message() {
        return message;
    }

    public Object[] arguments() {
        return arguments.clone();
    }

    public Throwable cause() {
        return cause;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
                "level=" + level +
                ", channel=" + channel +
                ", message='" + message + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
