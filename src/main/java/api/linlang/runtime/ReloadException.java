package api.linlang.runtime;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 重载的部分或全部步骤失败；各步骤已记录诊断，调用方不应重复打印堆栈。
 */
public final class ReloadException extends IllegalStateException {
    private final Map<String, Throwable> failures;

    /**
     * 保存失败步骤及其原因。
     *
     * @param failures 非空的失败步骤映射
     */
    public ReloadException(Map<String, ? extends Throwable> failures) {
        super("LIN-RUNTIME-RELOAD-FAIL: " + String.join(", ", failures.keySet()));
        if (failures.isEmpty()) throw new IllegalArgumentException("failures");
        this.failures = Collections.unmodifiableMap(new LinkedHashMap<>(failures));
    }

    /**
     * 返回失败步骤；成功步骤不会出现在此映射中。
     *
     * @return 不可修改的失败详情
     */
    public Map<String, Throwable> failures() {
        return failures;
    }
}
