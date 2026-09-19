package api.linlang.file.file.config;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 已由配置服务报告的加载失败，可包含多个文件的问题。
 *
 * <p>批量重载会处理其余文件后抛出此异常。失败文件保留原有活动值，
 * 成功文件正常更新；调用方不应再次打印同一异常堆栈。</p>
 */
public final class ConfigLoadException extends IllegalStateException {
    private final Map<Path, List<ConfigIssue>> failures;

    /**
     * 建立不可变的配置失败结果。
     *
     * @param failures 文件与对应问题
     */
    public ConfigLoadException(Map<Path, List<ConfigIssue>> failures) {
        super("LIN-FILE-CONFIG-LOAD-FAIL: " + failures.keySet().stream()
                .map(path -> path.getFileName().toString()).toList());
        Map<Path, List<ConfigIssue>> copy = new LinkedHashMap<>();
        failures.forEach((file, issues) -> copy.put(file, List.copyOf(issues)));
        this.failures = Collections.unmodifiableMap(copy);
    }

    /**
     * 返回各文件的问题明细。
     *
     * @return 不可修改的失败映射
     */
    public Map<Path, List<ConfigIssue>> failures() { return failures; }
}
