package api.linlang.audit.problem;

import java.util.Objects;

/**
 * 内建问题代码的查询结果。
 *
 * <p>定义由运行时直接提供，不依赖配置或语言文件。说明文本用于代码查询，
 * 实际抛出的 Java 异常仍然只需要携带问题代码和 cause。</p>
 */
public final class ProblemDefinition {

    private final String code;
    private final String component;
    private final String description;
    private final String resolution;
    private final String documentation;

    /**
     * 建立一项内建问题代码定义。
     *
     * @param code          稳定的问题代码
     * @param component     所属组件
     * @param description   问题含义
     * @param resolution    建议的排查方式，可以为空
     * @param documentation 相关文档路径，可以为空
     */
    public ProblemDefinition(String code,
                             String component,
                             String description,
                             String resolution,
                             String documentation) {
        this.code = requireText(code, "code");
        this.component = requireText(component, "component");
        this.description = requireText(description, "description");
        this.resolution = resolution == null ? "" : resolution;
        this.documentation = documentation == null ? "" : documentation;
    }

    /**
     * 返回稳定的问题代码。
     *
     * @return 问题代码
     */
    public String code() {
        return code;
    }

    /**
     * 返回问题所属组件。
     *
     * @return 组件名称
     */
    public String component() {
        return component;
    }

    /**
     * 返回问题含义。
     *
     * @return 问题说明
     */
    public String description() {
        return description;
    }

    /**
     * 返回建议的排查方式。
     *
     * @return 排查建议，可能为空字符串
     */
    public String resolution() {
        return resolution;
    }

    /**
     * 返回相关文档路径。
     *
     * @return 文档路径，可能为空字符串
     */
    public String documentation() {
        return documentation;
    }

    private static String requireText(String value, String name) {
        String result = Objects.requireNonNull(value, name).trim();
        if (result.isEmpty()) throw new IllegalArgumentException(name);
        return result;
    }
}
