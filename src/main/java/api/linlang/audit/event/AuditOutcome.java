package api.linlang.audit.event;

/**
 * 审计事件的执行结果。
 */
public enum AuditOutcome {
    SUCCESS,
    FAILURE,
    DENIED,
    UNKNOWN
}
