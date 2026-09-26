package api.linlang.audit.problem;

/**
 * @hidden
 *
 * 标记对应 Problem 已经由故障发生处报告的异常。
 *
 * <p>上层边界仍可捕获该异常以停止或汇总流程，但不应再次创建笼统的 Problem。
 * 这可以保留最接近故障现场的问题代码和上下文，避免同一原因被重复报告。</p>
 */
public interface ReportedProblemException {
}
