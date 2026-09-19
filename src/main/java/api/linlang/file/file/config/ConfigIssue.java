package api.linlang.file.file.config;

import java.util.Objects;

/**
 * 配置中的可定位问题，不包含原始配置值。
 *
 * @param key 配置路径，根节点使用 $
 * @param message 原因与修正提示
 */
public record ConfigIssue(String key, String message) {
    public ConfigIssue {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(message, "message");
    }
}
