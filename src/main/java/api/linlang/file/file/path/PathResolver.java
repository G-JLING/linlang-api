// linlang-core/src/main/java/io/linlang/file/runtime/PathResolver.java
package api.linlang.file.file.path;

import java.nio.file.Path;

/**
 * 文件路径解析器
 * @hidden
 */
public interface PathResolver {
    Path root();
    default Path sub(String first, String... more){ return root().resolve(Path.of(first, more)); }
}