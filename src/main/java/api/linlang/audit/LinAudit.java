package api.linlang.audit;

import api.linlang.audit.event.AuditEvent;
import api.linlang.audit.log.LinLogger;
import api.linlang.audit.problem.LinProblemReporter;

/**
 * 单个 Linlang 门面的日志、审计与问题报告统一入口。
 *
 * <p>开发者无需分别初始化成员。普通日志通过 {@link #logger()} 输出，
 * 行为审计通过 {@link #record(AuditEvent)} 记录，结构化问题通过
 * {@link #problem()} 报告。</p>
 */
public interface LinAudit {

    /**
     * 返回当前门面绑定的普通日志成员。
     *
     * @return 日志成员
     */
    LinLogger logger();

    /**
     * 返回当前门面绑定的问题报告成员。
     *
     * @return 问题报告成员
     */
    LinProblemReporter problem();

    /**
     * 记录结构化审计事件。
     *
     * @param event 审计事件
     */
    void record(AuditEvent event);

    /**
     * 使用键值对快速记录审计事件。
     *
     * @param event  稳定的事件名称
     * @param fields 事件字段，按照 key、value 顺序提供
     */
    default void record(String event, Object... fields) {
        record(AuditEvent.of(event, fields));
    }

    /**
     * 等待当前入口已经提交的文件记录完成写入。
     */
    void flush();
}
