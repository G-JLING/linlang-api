package api.linlang.audit;

import api.linlang.audit.event.AuditEvent;
import api.linlang.audit.log.LinLogger;
import api.linlang.audit.log.LogChannel;
import api.linlang.audit.log.LogLevel;
import api.linlang.audit.log.LogRecord;
import api.linlang.audit.problem.LinProblem;
import api.linlang.audit.problem.LinProblemReporter;
import api.linlang.audit.problem.ProblemDefinition;

import java.util.Objects;
import java.util.List;
import java.util.Optional;

/**
 * 日志、审计与问题报告的静态兼容门面。
 *
 * <p>插件业务代码优先通过 {@code lin.linAudit()} 取得绑定当前插件的统一入口。
 * 本类保留给运行时内部代码和旧版本调用方式使用。</p>
 */
public final class LinLog {

    /**
     * 日志与审计提供者 SPI。
     *
     * <p>运行时只需安装一个 Provider。Provider 根据 owner 将记录路由到不同插件，
     * 三种记录可以分别写入普通日志、审计日志和问题日志。</p>
     *
     * @hidden
     */
    public interface Provider {

        void publish(Object owner, LogRecord record);

        void publishAudit(Object owner, AuditEvent event);

        void publishProblem(Object owner, LinProblem problem);

        default Optional<ProblemDefinition> lookupProblem(String code) {
            return Optional.empty();
        }

        default List<ProblemDefinition> listProblems() {
            return List.of();
        }

        default void flush(Object owner) {}

        default void flushStartupToConsole(Object owner) {}

        default void flushOpToOnlineOps(Object owner) {}

        default void flushOpTo(Object owner, Object op) {}
    }

    private static final Provider NOOP = new Noop();
    private static volatile Provider provider = NOOP;

    /**
     * 安装运行时 Provider。
     *
     * @param next 新 Provider，传入 null 时恢复为空实现
     * @hidden
     */
    public static void install(Provider next) {
        provider = next == null ? NOOP : next;
    }

    /**
     * 仅当指定 Provider 仍是当前实现时卸载它。
     *
     * @param expected 预期的当前 Provider
     * @hidden
     */
    public static void uninstall(Provider expected) {
        if (provider == expected) {
            provider = NOOP;
        }
    }

    /**
     * 为指定业务类创建统一审计入口。
     *
     * @param ownerHint 插件主类或业务类
     * @return 绑定该 owner 的统一入口
     */
    public static LinAudit getAudit(Class<?> ownerHint) {
        return forOwner(Objects.requireNonNull(ownerHint, "ownerHint"));
    }

    /**
     * 为平台 owner 创建统一审计入口。
     *
     * @param ownerHint 平台 owner 或业务类
     * @return 绑定该 owner 的统一入口
     * @hidden
     */
    public static LinAudit forOwner(Object ownerHint) {
        LinLogger logger = loggerFor(ownerHint);
        LinProblemReporter problems = new LinProblemReporter() {
            @Override
            public void report(LinProblem problem) {
                provider.publishProblem(ownerHint, Objects.requireNonNull(problem, "problem"));
            }

            @Override
            public Optional<ProblemDefinition> lookup(String code) {
                if (code == null || code.isBlank()) return Optional.empty();
                return provider.lookupProblem(code.trim());
            }

            @Override
            public List<ProblemDefinition> list() {
                return List.copyOf(provider.listProblems());
            }
        };
        return new LinAudit() {
            @Override
            public LinLogger logger() {
                return logger;
            }

            @Override
            public LinProblemReporter problem() {
                return problems;
            }

            @Override
            public void record(AuditEvent event) {
                provider.publishAudit(ownerHint, Objects.requireNonNull(event, "event"));
            }

            @Override
            public void flush() {
                provider.flush(ownerHint);
            }
        };
    }

    /**
     * 为指定业务类创建普通日志成员。
     *
     * @param ownerHint 插件主类或业务类
     * @return 绑定该 owner 的日志成员
     */
    public static LinLogger getLogger(Class<?> ownerHint) {
        return loggerFor(Objects.requireNonNull(ownerHint, "ownerHint"));
    }

    private static LinLogger loggerFor(Object owner) {
        return new LinLogger() {
            @Override
            public void debug(String msg, Object... values) {
                publish(owner, LogLevel.DEBUG, LogChannel.STANDARD, msg, null, values);
            }

            @Override
            public void info(String msg, Object... values) {
                publish(owner, LogLevel.INFO, LogChannel.STANDARD, msg, null, values);
            }

            @Override
            public void file(String msg, Object... values) {
                publish(owner, LogLevel.INFO, LogChannel.FILE, msg, null, values);
            }

            @Override
            public void warn(String msg, Object... values) {
                publish(owner, LogLevel.WARN, LogChannel.STANDARD, msg, null, values);
            }

            @Override
            public void warn(String msg, Throwable cause, Object... values) {
                publish(owner, LogLevel.WARN, LogChannel.STANDARD, msg, cause, values);
            }

            @Override
            public void error(String msg, Throwable cause, Object... values) {
                publish(owner, LogLevel.ERROR, LogChannel.STANDARD, msg, cause, values);
            }

            @Override
            public void op(String msg, Object... values) {
                publish(owner, LogLevel.INFO, LogChannel.OP, msg, null, values);
            }

            @Override
            public void startup(String msg, Object... values) {
                publish(owner, LogLevel.INFO, LogChannel.STARTUP, msg, null, values);
            }

            @Override
            public void init(String msg, Object... values) {
                publish(owner, LogLevel.INFO, LogChannel.INIT, msg, null, values);
            }

            @Override
            public void audit(String event, Object... fields) {
                provider.publishAudit(owner, AuditEvent.of(event, fields));
            }

            @Override
            public void flushStartupToConsole() {
                provider.flushStartupToConsole(owner);
            }

            @Override
            public void flushOpToOnlineOps() {
                provider.flushOpToOnlineOps(owner);
            }

            @Override
            public void flushOpTo(Object op) {
                provider.flushOpTo(owner, op);
            }
        };
    }

    public static void debug(String message, Object... values) {
        publish(null, LogLevel.DEBUG, LogChannel.STANDARD, message, null, values);
    }

    public static void info(String message, Object... values) {
        publish(null, LogLevel.INFO, LogChannel.STANDARD, message, null, values);
    }

    /**
     * 仅向普通日志文件输出 INFO 级别日志。
     *
     * @param message 日志消息
     * @param values 占位参数或扩展字段
     */
    public static void file(String message, Object... values) {
        publish(null, LogLevel.INFO, LogChannel.FILE, message, null, values);
    }

    public static void warn(String message, Object... values) {
        publish(null, LogLevel.WARN, LogChannel.STANDARD, message, null, values);
    }

    public static void warn(String message, Throwable cause, Object... values) {
        publish(null, LogLevel.WARN, LogChannel.STANDARD, message, cause, values);
    }

    public static void error(String message, Throwable cause, Object... values) {
        publish(null, LogLevel.ERROR, LogChannel.STANDARD, message, cause, values);
    }

    public static void error(String message, Object... values) {
        error(message, null, values);
    }

    public static void init(String message, Object... values) {
        publish(null, LogLevel.INFO, LogChannel.INIT, message, null, values);
    }

    public static void op(String message, Object... values) {
        publish(null, LogLevel.INFO, LogChannel.OP, message, null, values);
    }

    public static void op(Object owner, String message, Object... values) {
        publish(owner, LogLevel.INFO, LogChannel.OP, message, null, values);
    }

    public static void startup(String message, Object... values) {
        publish(null, LogLevel.INFO, LogChannel.STARTUP, message, null, values);
    }

    public static void startup(Object owner, String message, Object... values) {
        publish(owner, LogLevel.INFO, LogChannel.STARTUP, message, null, values);
    }

    public static void init(Object owner, String message, Object... values) {
        publish(owner, LogLevel.INFO, LogChannel.INIT, message, null, values);
    }

    public static void banner(String message) {
        publish(null, LogLevel.INFO, LogChannel.BANNER, message, null);
    }

    /**
     * 旧版 Banner 方法。
     *
     * @param message Banner 文本
     * @deprecated 使用 {@link #banner(String)}
     * @hidden
     */
    @Deprecated
    public static void banr(String message) {
        banner(message);
    }

    public static void audit(String event, Object... fields) {
        provider.publishAudit(null, AuditEvent.of(event, fields));
    }

    public static void audit(Object owner, String event, Object... fields) {
        provider.publishAudit(owner, AuditEvent.of(event, fields));
    }

    public static void problem(LinProblem problem) {
        provider.publishProblem(null, Objects.requireNonNull(problem, "problem"));
    }

    public static void flush() {
        provider.flush(null);
    }

    public static void flushStartupToConsole() {
        provider.flushStartupToConsole(null);
    }

    public static void flushOpToOnlineOps() {
        provider.flushOpToOnlineOps(null);
    }

    public static void flushOpTo(Object op) {
        provider.flushOpTo(null, op);
    }

    private static void publish(Object owner,
                                LogLevel level,
                                LogChannel channel,
                                String message,
                                Throwable cause,
                                Object... values) {
        provider.publish(owner, LogRecord.of(level, channel, message, cause, values));
    }

    private static final class Noop implements Provider {
        @Override
        public void publish(Object owner, LogRecord record) {}

        @Override
        public void publishAudit(Object owner, AuditEvent event) {}

        @Override
        public void publishProblem(Object owner, LinProblem problem) {}
    }

    private LinLog() {}
}
