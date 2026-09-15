package api.linlang.command;

import api.linlang.file.file.LangText;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 单条命令的注册选项。
 *
 * <p>未设置执行目标时默认允许玩家和控制台执行。权限、描述和参数标签均为可选项。
 * 该对象用于收拢命令注册中的附加参数，不改变命令规范与执行器的职责。</p>
 *
 * <pre>{@code
 * commands.register(
 *         "rs reload",
 *         context -> reload(),
 *         CommandOptions.options()
 *                 .permission("rainbowedit.reload")
 *                 .desc(lang.command.reload.description)
 * );
 * }</pre>
 */
public final class CommandOptions {

    private LinCommand.Permission permission;
    private LinCommand.ExecTarget target = LinCommand.ExecTarget.ALL;
    private LinCommand.I18nSupplier description;
    private final Map<String, LinCommand.I18nSupplier> labels = new LinkedHashMap<>();

    private CommandOptions() {
    }

    /**
     * 创建使用默认值的命令选项。
     *
     * @return 新的命令选项
     */
    public static CommandOptions options() {
        return new CommandOptions();
    }

    /**
     * 设置绝对权限节点。
     *
     * @param node 权限节点
     * @return 当前选项
     */
    public CommandOptions permission(String node) {
        this.permission = LinCommand.Permission.perms(node);
        return this;
    }

    /**
     * 设置权限要求。
     *
     * <p>传入 {@code null} 可以清除已经设置的权限。</p>
     *
     * @param permission 权限要求，可为 {@code null}
     * @return 当前选项
     */
    public CommandOptions permission(LinCommand.Permission permission) {
        this.permission = permission;
        return this;
    }

    /**
     * 设置相对于命令组权限前缀的权限片段。
     *
     * <p>逐条注册没有命令组上下文，此时该片段会作为完整权限节点使用。</p>
     *
     * @param segment 权限片段
     * @return 当前选项
     */
    public CommandOptions relativePermission(String segment) {
        this.permission = LinCommand.Permission.segment(segment);
        return this;
    }

    /**
     * 设置允许执行命令的发送者类型。
     *
     * @param target 执行目标
     * @return 当前选项
     */
    public CommandOptions target(LinCommand.ExecTarget target) {
        this.target = Objects.requireNonNull(target, "target");
        return this;
    }

    /**
     * 限制命令只能由玩家执行。
     *
     * @return 当前选项
     */
    public CommandOptions player() {
        return target(LinCommand.ExecTarget.PLAYER);
    }

    /**
     * 限制命令只能由控制台执行。
     *
     * @return 当前选项
     */
    public CommandOptions console() {
        return target(LinCommand.ExecTarget.CONSOLE);
    }

    /**
     * 允许玩家和控制台执行命令。
     *
     * @return 当前选项
     */
    public CommandOptions all() {
        return target(LinCommand.ExecTarget.ALL);
    }

    /**
     * 设置语言字段引用形式的命令描述。
     *
     * @param text 命令描述
     * @return 当前选项
     */
    public CommandOptions desc(LangText text) {
        this.description = LinCommand.I18nSupplier.from(Objects.requireNonNull(text, "text"));
        return this;
    }

    /**
     * 设置自定义的延迟命令描述提供者。
     *
     * @param provider 描述提供者，可为 {@code null}
     * @return 当前选项
     */
    public CommandOptions descProvider(LinCommand.I18nSupplier provider) {
        this.description = provider;
        return this;
    }

    /**
     * 设置语言字段引用形式的参数标签。
     *
     * @param name 命令规范中的参数名
     * @param text 参数标签
     * @return 当前选项
     */
    public CommandOptions label(String name, LangText text) {
        return labelProvider(name, LinCommand.I18nSupplier.from(Objects.requireNonNull(text, "text")));
    }

    /**
     * 设置自定义的延迟参数标签提供者。
     *
     * @param name 命令规范中的参数名
     * @param provider 参数标签提供者
     * @return 当前选项
     */
    public CommandOptions labelProvider(String name, LinCommand.I18nSupplier provider) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name");
        }
        labels.put(name, Objects.requireNonNull(provider, "provider"));
        return this;
    }

    /**
     * 只设置命令描述。
     *
     * @param description 命令描述
     * @return 当前选项
     */
    public CommandOptions i18n(LangText description) {
        return desc(description);
    }

    /**
     * 设置命令描述和一个参数标签。
     *
     * @param description 命令描述
     * @param name 命令规范中的参数名
     * @param text 参数标签
     * @return 当前选项
     */
    public CommandOptions i18n(LangText description, String name, LangText text) {
        return desc(description).label(name, text);
    }

    /**
     * 设置命令描述和多个参数标签。
     *
     * <p>{@code additionalLabels} 在第一组标签之后，继续按“参数名、语言字段”成对排列。</p>
     *
     * <pre>{@code
     * options.i18n(text.set, "idx", text.line, "text", text.text);
     * }</pre>
     *
     * @param description 命令描述
     * @param name 第一个参数名
     * @param text 第一个参数标签
     * @param additionalLabels 其余参数名和语言字段
     * @return 当前选项
     * @throws IllegalArgumentException 其余标签不是完整键值对或类型不正确时
     */
    public CommandOptions i18n(
            LangText description,
            String name,
            LangText text,
            Object... additionalLabels
    ) {
        validateLabelPairs(additionalLabels);
        i18n(description, name, text);
        for (int index = 0; index < additionalLabels.length; index += 2) {
            label((String) additionalLabels[index], (LangText) additionalLabels[index + 1]);
        }
        return this;
    }

    private static void validateLabelPairs(Object[] labels) {
        if (labels == null || (labels.length & 1) != 0) {
            throw new IllegalArgumentException("labels must contain name and LangText pairs");
        }
        for (int index = 0; index < labels.length; index += 2) {
            if (!(labels[index] instanceof String name) || name.isBlank()) {
                throw new IllegalArgumentException("label name must be a non-blank String at index " + index);
            }
            if (!(labels[index + 1] instanceof LangText)) {
                throw new IllegalArgumentException("label value must be a LangText at index " + (index + 1));
            }
        }
    }

    /**
     * 导入已有的命令国际化配置。
     *
     * @param i18n 命令国际化配置
     * @return 当前选项
     */
    public CommandOptions i18n(LinCommand.I18n i18n) {
        LinCommand.I18n source = Objects.requireNonNull(i18n, "i18n");
        this.description = source.description();
        this.labels.clear();
        this.labels.putAll(source.labels());
        return this;
    }

    /**
     * @return 权限要求，可为 {@code null}
     */
    public LinCommand.Permission permission() {
        return permission;
    }

    /**
     * @return 执行目标
     */
    public LinCommand.ExecTarget target() {
        return target;
    }

    /**
     * @return 命令描述提供者，可为 {@code null}
     */
    public LinCommand.I18nSupplier description() {
        return description;
    }

    /**
     * @return 不可修改的参数标签映射
     */
    public Map<String, LinCommand.I18nSupplier> labels() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(labels));
    }
}
