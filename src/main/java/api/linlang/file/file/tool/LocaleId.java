package api.linlang.file.file.tool;

import java.util.Locale;

/**
 * locale 名称归一化工具。
 */
public final class LocaleId {
    private LocaleId(){}

    /**
     * 将常见 locale 写法归一化为 {@code language_REGION}：
     * <ul>
     *   <li>enGB / en-GB / en_gb / EN_gb → en_GB</li>
     *   <li>zhCN → zh_CN</li>
     * </ul>
     *
     * @param raw 原始 locale 字符串
     * @return 规范 locale；空值返回 {@code en_GB}
     */
    public static String normalize(String raw) {
        if (raw == null) return "en_GB";
        String s = raw.trim();
        if (s.isEmpty()) return "en_GB";
        s = s.replace('-', '_');
        // "enGB" -> "en_GB" 简单处理：两段或驼峰
        if (!s.contains("_") && s.length() == 4) {
            s = s.substring(0,2) + "_" + s.substring(2);
        }
        String[] parts = s.split("_", -1);
        if (parts.length >= 2) {
            String lang = parts[0].toLowerCase(Locale.ROOT);
            String reg  = parts[1].toUpperCase(Locale.ROOT);
            return lang + "_" + reg;
        }
        return s.toLowerCase(Locale.ROOT);
    }
}
