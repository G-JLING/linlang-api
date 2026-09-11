package api.linlang.command.group;

import api.linlang.command.LinCommand;

/**
 * 命令组失败回调接收的失败信息。
 *
 * @param context 命令执行上下文，其中可能只包含已经成功解析的参数
 * @param reason 失败原因
 * @param command 完整命令规范
 * @param cause 原始异常；权限或执行目标不满足时可以为 {@code null}
 */
public record CommandFailure(
        LinCommand.Ctx context,
        Reason reason,
        String command,
        Throwable cause
) {

    /**
     * 命令失败阶段。
     */
    public enum Reason {
        /** 参数解析失败。 */
        ARGUMENT,
        /** 权限不足。 */
        PERMISSION,
        /** 当前发送者不满足执行目标。 */
        EXECUTION_TARGET,
        /** 叶子命令执行器失败。 */
        EXECUTION,
        /** 交互参数等待超时。 */
        INTERACTION_TIMEOUT,
        /** 交互参数被取消。 */
        INTERACTION_CANCELLED
    }
}
