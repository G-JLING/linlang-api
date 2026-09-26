package api.linlang.file.file;

import api.linlang.audit.problem.ReportedProblemException;

import java.util.Objects;

/**
 * @hidden
 *
 * 文件服务已经报告的显式保存失败。
 *
 * <p>该异常用于通知调用方保存没有完成。对应的 Problem 已由文件服务记录</p>
 */
public final class FileSaveException extends IllegalStateException implements ReportedProblemException {
    private final String code;
    private final String target;

    /**
     * 创建保存失败结果。
     *
     * @param code Problem 编码
     * @param target 保存目标
     * @param cause 原始原因
     */
    public FileSaveException(String code, String target, Throwable cause) {
        super(Objects.requireNonNull(code, "code") + ": "
                + Objects.requireNonNull(target, "target"), cause);
        this.code = code;
        this.target = target;
    }

    /**
     * 返回已经报告的 Problem 编码。
     *
     * @return Problem 编码
     */
    public String code() {
        return code;
    }

    /**
     * 返回保存目标的可读标识。
     *
     * @return 文件路径或绑定类型
     */
    public String target() {
        return target;
    }
}
