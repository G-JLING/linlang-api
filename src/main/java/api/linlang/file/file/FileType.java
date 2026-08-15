package api.linlang.file.file;

import api.linlang.file.file.annotations.ConfigFile;
import api.linlang.file.file.annotations.LangPack;

/**
 * 配置文件和语言文件支持的序列化格式。
 *
 * <p>适用于注解 {@link ConfigFile} 和 {@link LangPack}</p>
 */
public enum FileType {
    /** YAML 格式。 */
    YAML,
    /** JSON 格式。 */
    JSON
}
