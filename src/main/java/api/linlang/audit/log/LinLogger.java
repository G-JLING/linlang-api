package api.linlang.audit.log;

import api.linlang.audit.LinAudit;
import api.linlang.audit.LinLog;

/**
 * 绑定到平台的具体琳琅日志服务实现
 *
 * <p>琳琅日志服务提供多种常用的参数使用方式：</p>
 * <ul>
 *     <li>空占位符：按 <code>{}</code>顺序填入参数值</li>
 *     <li>准确占位符：用于格式化字符串中的精准替换</li>
 *     <li>尾部字段：未用于占位符的键值对会附加到消息末尾</li>
 * </ul>
 *
 * <p>{@code Throwable} 应通过带异常参数的 WARN 或 ERROR 重载传入，
 * 以便运行时保留完整原因链。</p>
 */
public interface LinLogger {


    /**
     * 输出 DEBUG 级别日志。
     *
     * @param msg 日志消息，支持占位符格式
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    void debug(String msg, Object... kv);

    /**
     * 输出 INFO 级别日志。
     *
     * @param msg 日志消息，支持占位符格式
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    void info(String msg, Object... kv);

    /**
     * 输出 WARN 级别日志
     *
     * @param msg 日志消息，支持占位符格式
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    void warn(String msg, Object... kv);

    /**
     * 输出 WARN 级别日志并保留异常原因链。
     *
     * @param msg 日志消息，支持占位符格式
     * @param t   异常对象
     * @param kv  占位参数或扩展字段
     */
    void warn(String msg, Throwable t, Object... kv);

    /**
     * 输出 ERROR 级日志，可选附带异常
     *
     * @param msg 日志消息，支持占位符格式
     * @param t   异常对象，可为 null
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    void error(String msg, Throwable t, Object... kv);

    /**
     * 方便重载：没有 Throwable 时可以只传消息和 kv
     *
     * @param msg 日志消息，支持占位符格式
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    default void error(String msg, Object... kv) {
        error(msg, null, kv);
    }

    // --- 特殊通道：OP / STARTUP / INIT ---

    /**
     * 输出 OP 通道日志：发送给在线 OP 或进入 OP 队列
     *
     * @param msg 日志消息，支持占位符格式
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    void op(String msg, Object... kv);

    /**
     * 输出启动通道日志：服务器尚未完全启动时进入 STARTUP 队列
     *
     * @param msg 日志消息，支持占位符格式
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    void startup(String msg, Object... kv);

    /**
     * 输出 INIT 通道日志：通常用于模块/插件初始化阶段
     *
     * @param msg 日志消息，支持占位符格式
     * @param kv  键值对参数，用于填充消息中的占位符
     */
    void init(String msg, Object... kv);

    // --- 审计事件（按 owner 归属） ---

    /**
     * 记录一条审计事件
     *
     * @param event 审计事件名称
     * @param kv    审计事件相关的键值对数据
     * <p>新代码优先使用 {@link LinAudit#record(String, Object...)}。</p>
     */
    void audit(String event, Object... kv);

    // --- Flush 辅助方法：直接转发到 LinLog ---

    /**
     * 刷新 STARTUP 队列到控制台或广播
     */
    default void flushStartupToConsole() {
        LinLog.flushStartupToConsole();
    }

    /**
     * 刷新 OP 队列到在线 OP
     */
    default void flushOpToOnlineOps() {
        LinLog.flushOpToOnlineOps();
    }

    /**
     * 向指定 OP 刷新 OP 队列（平台自定义 op 类型）
     *
     * @param op 目标 OP 对象
     */
    default void flushOpTo(Object op) {
        LinLog.flushOpTo(op);
    }
}
