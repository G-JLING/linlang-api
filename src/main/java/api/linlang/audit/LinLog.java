package api.linlang.audit;

/**
 * 审计与日志
 * <p>初始化琳琅服务后可用</p>
 *
 * <p>审计与日志服务提供静态调用门面 {@link LinLog}</p>
 */
public final class LinLog {
    /**
     * 日志实现者
     *
     * @hidden
     */
    public interface Provider {
        /**
         * 输出一条日志
         *
         * @param level 级别，如 DEBUG/INFO/WARN/ERROR
         * @param msg   人类可读的消息文本
         * @param kv    扩展键值对：<code>key, value, key, value...</code>
         */
        void log(String level, String msg, Object... kv);

        /**
         * 记录一条审计事件（用于重要操作留痕）
         *
         * @param event 事件名称/类型
         * @param kv    结构化字段：<code>key, value, ...</code>
         */
        void audit(String event, Object... kv);

        default void flushStartupToConsole() {}
        default void flushOpToOnlineOps() {}
        default void flushOpTo(Object op) {}
    }
    private static volatile Provider P = new Noop();

    /**
     * 安装日志提供者
     * <p>若传入 {@code null}，则回退为空实现（不输出）</p>
     *
     * @param p 提供者实例
     * @hidden
     */
    public static void install(Provider p){ P = (p==null? new Noop(): p); }

    /** 输出 <code>level=DEBUG</code> 日志
     * @param m  日志字符串
     * @param kv 键值对
     */
    public static void debug(String m, Object...kv){ P.log("DEBUG", m, kv); }

    /** 输出 <code>level=INFO</code> 日志
     * @param m  日志字符串
     * @param kv 键值对
     */
    public static void info (String m, Object...kv){ P.log("INFO",  m, kv); }

    /** 输出 <code>level=WARN</code> 日志
     * @param m  日志字符串
     * @param kv 键值对
     */
    public static void warn (String m, Object...kv){ P.log("WARN",  m, kv); }

    /** 输出 <code>level=INFO</code> 模块加载日志
     *
     * @param m  日志字符串
     * @param kv 键值对
     * @hidden
     * */
    public static void init(String m, Object...kv){ P.log("INIT",  m, kv); }
    /**
     * 输出 <code>level=ERROR</code> 日志
     * <p>若提供异常，将其摘要追加到键值中</p>
     *
     * @param m  日志字符串
     * @param kv 键值对
     */
    public static void error(String m, Throwable t, Object...kv){
        Object[] kv2 = kv;
        if (t != null) kv2 = append(kv, "err", t.toString());
        P.log("ERROR", m, kv2);
    }

    /** 输出 <code>level=INFO</code> 在线 OP 可见的日志。无 OP 在线时，消息将进入待发队列，等待 {@link #flushOpToOnlineOps()} 刷新后重新尝试发送
     *
     * @param m  日志字符串
     * @param kv 键值对
     */
    public static void op(String m, Object...kv){ P.log("OP",  m, kv); }
    
    /** 输出 <code>level=INFO</code> 服务器启动完成时打印的日志。服务器尚未启动完成时，消息将进入待发队列，等待 {@link #flushStartupToConsole()} 刷新后重新尝试发送
     * @param m  日志字符串
     * @param kv 键值对
     */
    public static void startup(String m, Object...kv){P.log("STARTUP",  m, kv);}
    
    /**
     * 记录审计事件
     * <p>用于安全、合规、关键路径操作的留痕与检索</p>
     *
     * @param event  日志字符串
     * @param kv     键值对
     */
    public static void audit(String event, Object...kv){ P.audit(event, kv); }

    /** 立即发送并刷新 {@link #startup(String, Object...)} 队列缓存的日志 */
    public static void flushStartupToConsole() { P.flushStartupToConsole(); }

    /** 立即发送并刷新 {@link #op(String, Object...)} 队列缓存的日志 */
    public static void flushOpToOnlineOps() { P.flushOpToOnlineOps(); }

    /** 立即向指定的 OP 发送并刷新 {@link #startup(String, Object...)} 队列缓存的日志
     *
     * @param op 指定的 OP
     */
    public static void flushOpTo(Object op) { P.flushOpTo(op); }

    /**
     * 空实现：未安装 Provider 时使用，不做任何输出
     *
     * @hidden
     */
    private static final class Noop implements Provider {
        public void log(String l,String m,Object...kv) {}
        public void audit(String e,Object...kv) {}
    }
    /**
     * 将两个可变参数数组拼接为一个新数组
     *
     * @hidden
     */
    private static Object[] append(Object[] a, Object...b){
        Object[] r = new Object[a.length+b.length];
        System.arraycopy(a,0,r,0,a.length);
        System.arraycopy(b,0,r,a.length,b.length);
        return r;
    }

    private LinLog() {}
}