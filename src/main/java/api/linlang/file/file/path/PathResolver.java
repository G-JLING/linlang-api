package api.linlang.file.file.path;

import java.nio.file.Path;

/**
 * 文件服务使用的数据目录解析器。
 *
 * @hidden
 */
public interface PathResolver {
    /**
     * @return 当前插件的数据目录
     */
    Path root();

    /**
     * 在数据目录下解析子路径。
     *
     * @param first 第一个路径片段
     * @param more 后续路径片段
     * @return 解析后的子路径
     */
    default Path sub(String first, String... more){ return root().resolve(Path.of(first, more)); }
}
