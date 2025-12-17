package api.linlang.audit;

import java.util.Objects;

/**
 * 审计与日志静态门面
 * <p>初始化琳琅运行时后，通过此类安装为具体平台实现 {@link LinLogger}</p>
 */
public final class LinLog {

    /**
     * 日志提供者 SPI
     *
     * <p>由运行时在启动时安装具体实现。业务代码不应直接实现或使用本接口，
     * 只需通过 {@link LinLog} 和 {@link LinLogger} 进行日志与审计输出</p>
     *
     * @hidden
     */
    public interface Provider {
        /**
         * 输出一条日志（无 owner 上下文，通常视为运行时全局日志）。
         *
         * @param level 日志级别，如 DEBUG/INFO/WARN/ERROR/OP/STARTUP/INIT
         * @param msg   消息内容
         * @param kv    扩展键值对：key, value, key, value...
         */
        void log(String level, String msg, Object... kv);

        /**
         * 输出一条日志（带 owner 上下文，例如某个插件或组件）。
         *
         * @param owner owner 线索（如插件实例或类对象）
         * @param level 日志级别
         * @param msg   消息内容
         * @param kv    扩展键值对
         */
        default void log(Object owner, String level, String msg, Object... kv) {
            log(level, msg, kv);
        }

        /**
         * 记录一条审计事件（无 owner）。
         *
         * @param event 事件名称
         * @param kv    扩展键值对
         */
        void audit(String event, Object... kv);

        /**
         * 记录一条审计事件（带 owner）。
         *
         * @param owner owner 线索（如插件实例或类对象）
         * @param event 事件名称
         * @param kv    扩展键值对
         */
        default void audit(Object owner, String event, Object... kv) {
            audit(event, kv);
        }

        /**
         * 刷新已缓存的 STARTUP 日志到控制台。
         */
        default void flushStartupToConsole() {}

        /**
         * 刷新已缓存的 OP 日志到所有在线 OP。
         */
        default void flushOpToOnlineOps() {}

        /**
         * 将已缓存的 OP 日志输出到指定对象（对象类型由具体实现决定）。
         *
         * @param op 目标对象，例如 Bukkit 的 Player
         */
        default void flushOpTo(Object op) {}
    }

    /** 当前安装的 Provider，默认使用空实现（不输出任何内容）。 */
    private static volatile Provider P = new Noop();

    /**
     * 安装日志提供者。
     *
     * <p>运行时在启动阶段调用本方法，将平台相关实现注入。
     * 若传入 {@code null}，则回退为不做任何输出的空实现。</p>
     *
     * @param p 要安装的 Provider 实例
     */
    public static void install(Provider p) {
        P = (p == null ? new Noop() : p);
    }

    // ----------------------------------------------------------------------
    // 按 owner 获取 Logger
    // ----------------------------------------------------------------------

    /**
     * 为指定业务类创建一个绑定 owner 的 {@link LinLogger}。
     *
     *
     * <p>Provider 可通过 {@code ownerHint} 反推出插件归属</p>
     *
     * @param ownerHint 作为 owner 线索的类，通常是插件主类或业务类自身
     * @return 绑定到该 owner 的日志接口
     */
    public static LinLogger getLogger(Class<?> ownerHint) {
        Objects.requireNonNull(ownerHint, "ownerHint");
        final Object owner = ownerHint;

        return new LinLogger() {
            @Override
            public void debug(String msg, Object... kv) {
                P.log(owner, "DEBUG", msg, kv);
            }

            @Override
            public void info(String msg, Object... kv) {
                P.log(owner, "INFO", msg, kv);
            }

            @Override
            public void warn(String msg, Object... kv) {
                P.log(owner, "WARN", msg, kv);
            }

            @Override
            public void error(String msg, Throwable t, Object... kv) {
                Object[] kv2 = kv;
                if (t != null) kv2 = append(kv, "err", t.toString());
                P.log(owner, "ERROR", msg, kv2);
            }

            @Override
            public void op(String msg, Object... kv) {
                P.log(owner, "OP", msg, kv);
            }

            @Override
            public void startup(String msg, Object... kv) {
                P.log(owner, "STARTUP", msg, kv);
            }

            @Override
            public void init(String msg, Object... kv) {
                P.log(owner, "INIT", msg, kv);
            }

            @Override
            public void audit(String event, Object... kv) {
                P.audit(owner, event, kv);
            }
        };
    }

    // ----------------------------------------------------------------------
    // 静态日志方法（runtime 全局）
    // ----------------------------------------------------------------------

    /** 输出 DEBUG 级别日志（无 owner，上下文视为运行时全局）。
     * @hidden
     */
    public static void debug(String m, Object... kv) {
        P.log("DEBUG", m, kv);
    }

    /** 输出 INFO 级别日志（无 owner，上下文视为运行时全局）。
     * @hidden
     */
    public static void info(String m, Object... kv) {
        P.log("INFO", m, kv);
    }

    /** 输出 WARN 级别日志（无 owner，上下文视为运行时全局）。
     * @hidden
     */
    public static void warn(String m, Object... kv) {
        P.log("WARN", m, kv);
    }

    /**
     * 输出 INIT 日志，通常用于模块 / 运行时初始化阶段。
     * @hidden
     */
    public static void init(String m, Object... kv) {
        P.log("INIT", m, kv);
    }

    /**
     * 输出 ERROR 日志，可选附带异常（无 owner）
     *
     * @param m   消息内容
     * @param t   异常对象，可为 {@code null}
     * @param kv  扩展键值对
     * @hidden
     */
    public static void error(String m, Throwable t, Object... kv) {
        Object[] kv2 = kv;
        if (t != null) kv2 = append(kv, "err", t.toString());
        P.log("ERROR", m, kv2);
    }

    /**
     * 便捷重载：无异常时只传消息和 kv
     *
     * @param m  消息内容
     * @param kv 扩展键值对
     * @hidden
     */
    public static void error(String m, Object... kv) {
        error(m, null, kv);
    }

    /**
     * 输出 OP 通道日志，推送给在线 OP 或进入 OP 队列
     * @hidden
     */
    public static void op(String m, Object... kv) {
        P.log("OP", m, kv);
    }

    /**
     * 输出 STARTUP 通道日志，用于服务器启动阶段的延迟输出
     * @hidden
     */
    public static void startup(String m, Object... kv) {
        P.log("STARTUP", m, kv);
    }

    // --- 带 owner 的静态辅助 ---

    /**
     * 针对指定 owner 输出 OP 日志。
     *
     * @param owner owner 线索
     * @param m     消息内容
     * @param kv    扩展键值对
     */
    public static void op(Object owner, String m, Object... kv) {
        P.log(owner, "OP", m, kv);
    }

    /**
     * 针对指定 owner 输出 STARTUP 日志。
     *
     * @param owner owner 线索
     * @param m     消息内容
     * @param kv    扩展键值对
     */
    public static void startup(Object owner, String m, Object... kv) {
        P.log(owner, "STARTUP", m, kv);
    }

    /**
     * 针对指定 owner 输出 INIT 日志。
     *
     * @param owner owner 线索
     * @param m     消息内容
     * @param kv    扩展键值对
     */
    public static void init(Object owner, String m, Object... kv) {
        P.log(owner, "INIT", m, kv);
    }

    // ----------------------------------------------------------------------
    // 审计
    // ----------------------------------------------------------------------

    /**
     * 记录一条审计事件
     *
     * @param event 事件名称
     * @param kv    扩展键值对
     * @hidden
     */
    public static void audit(String event, Object... kv) {
        P.audit(event, kv);
    }

    /**
     * 记录一条审计事件
     *
     * @param owner owner 线索
     * @param event 事件名称
     * @param kv    扩展键值对
     */
    public static void audit(Object owner, String event, Object... kv) {
        P.audit(owner, event, kv);
    }

    // ----------------------------------------------------------------------
    // Flush 辅助
    // ----------------------------------------------------------------------

    /**
     * 刷新 STARTUP 队列到控制台
     */
    public static void flushStartupToConsole() {
        P.flushStartupToConsole();
    }

    /**
     * 刷新 OP 队列到所有在线 OP
     */
    public static void flushOpToOnlineOps() {
        P.flushOpToOnlineOps();
    }

    /**
     * 将 OP 队列发送到指定对象（具体类型由 Provider 实现约定）
     *
     * @param op 目标对象，例如 Bukkit 的 Player
     */
    public static void flushOpTo(Object op) {
        P.flushOpTo(op);
    }

    // ----------------------------------------------------------------------
    // 内部工具
    // ----------------------------------------------------------------------

    /** 空实现 Provider：不输出任何日志与审计。
     * @hidden
     */
    private static final class Noop implements Provider {
        @Override public void log(String l, String m, Object... kv) {}
        @Override public void audit(String e, Object... kv) {}
    }

    /**
     * 将两个可变参数数组拼接为一个新数组。
     *
     * @param a 第一个数组
     * @param b 第二个数组
     * @return 新数组，包含 a 与 b 的所有元素
     * @hidden
     */
    private static Object[] append(Object[] a, Object... b) {
        Object[] r = new Object[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    private LinLog() {}
}