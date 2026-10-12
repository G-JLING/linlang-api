package api.linlang.audit.event;

/**
 * 审计事件的执行结果。
 */
public enum AuditOutcome {
    /**
     * 操作成功。
     */
    SUCCESS,
    /**
     * 操作失败。
     */
    FAILURE,
    /**
     * 操作被拒绝。
     */
    DENIED,
    /**
     * 尚未确定执行结果。
     */
    UNKNOWN
}
