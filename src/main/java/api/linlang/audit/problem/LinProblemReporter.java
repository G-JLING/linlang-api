package api.linlang.audit.problem;

import java.util.List;
import java.util.Optional;

/**
 * 问题报告与内建代码查询成员。
 */
public interface LinProblemReporter {

    /**
     * 记录问题代码、上下文和原始 Java 异常。
     *
     * @param problem 问题记录
     */
    void report(LinProblem problem);

    /**
     * 快速报告问题。
     *
     * @param code    稳定的问题代码
     * @param cause   原始 Java 异常，可以为 null
     * @param context 上下文字段，按照 key、value 顺序提供
     */
    default void report(String code, Throwable cause, Object... context) {
        report(LinProblem.of(code, cause, context));
    }

    /**
     * 查询运行时内建的问题代码。
     *
     * @param code 问题代码
     * @return 找到的定义
     */
    Optional<ProblemDefinition> lookup(String code);

    /**
     * 列出运行时内建的全部问题代码。
     *
     * @return 按代码排序的只读定义列表
     */
    default List<ProblemDefinition> list() {
        return List.of();
    }
}
