package api.linlang.command.group;

import api.linlang.command.CommandOptions;
import api.linlang.command.LinCommand;

import java.util.Map;

/**
 * 可复用、可嵌套的命令组。
 *
 * <p>命令组提供公共命令路径、权限路径和结果回调。多次从同一根命令取得相同路径的
 * 命令组时，运行时会把它们合并为同一个逻辑节点。公共权限和结果回调应在该组的
 * 第一条后代命令注册前完成配置，避免注册顺序改变既有命令契约。</p>
 *
 * <pre>{@code
 * CommandRoot root = commands.root("rs")
 *         .permissionPrefix("rainbowedit")
 *         .onSuccess(context -> auditSuccess(context))
 *         .onFailure(failure -> auditFailure(failure));
 *
 * CommandGroup rename = root.group("control")
 *         .permissionSegment("control")
 *         .group("rename")
 *         .permissionSegment("rename");
 *
 * rename.register(
 *         "item <id:int>",
 *         context -> renameItem(context.get("id")),
 *         CommandOptions.options()
 *                 .relativePermission("item")
 *                 .player()
 *                 .i18n(i18n)
 * );
 * }</pre>
 */
public interface CommandGroup {

    /**
     * 使用默认选项在当前组下注册命令。
     *
     * @param spec 相对命令规范
     * @param executor 命令执行器
     * @return 当前命令组
     */
    default CommandGroup register(String spec, LinCommand.CommandExecutor executor) {
        return register(spec, executor, CommandOptions.options());
    }

    /**
     * 使用统一选项在当前组下注册命令。
     *
     * @param spec 相对命令规范
     * @param executor 命令执行器
     * @param options 命令选项
     * @return 当前命令组
     */
    default CommandGroup register(
            String spec,
            LinCommand.CommandExecutor executor,
            CommandOptions options
    ) {
        CommandOptions value = java.util.Objects.requireNonNull(options, "options");
        return registerLazy(
                spec,
                executor,
                value.permission(),
                value.target(),
                value.description(),
                value.labels()
        );
    }

    /**
     * @return 从根命令开始的完整命令路径
     */
    String path();

    /**
     * 获取或创建子命令组。
     *
     * @param namespace 子命令命名空间
     * @return 子命令组
     */
    CommandGroup group(String namespace);

    /**
     * 设置当前组对权限路径贡献的片段。
     *
     * @param segment 权限片段；传入 {@code null} 或空白文本表示不贡献片段
     * @return 当前命令组
     */
    CommandGroup permissionSegment(String segment);

    /**
     * 添加所有后代命令都必须满足的独立权限要求。
     *
     * @param permission 权限要求；为 {@code null} 时忽略
     * @return 当前命令组
     */
    CommandGroup requires(LinCommand.Permission permission);

    /**
     * 添加叶子命令成功后的公共回调。
     *
     * <p>回调为 {@code null} 时忽略。嵌套组按照最具体的组到根组依次调用。</p>
     *
     * @param handler 成功回调
     * @return 当前命令组
     */
    CommandGroup onSuccess(CommandSuccessHandler handler);

    /**
     * 添加叶子命令失败后的公共回调。
     *
     * <p>回调为 {@code null} 时忽略。嵌套组按照最具体的组到根组依次调用。</p>
     *
     * @param handler 失败回调
     * @return 当前命令组
     */
    CommandGroup onFailure(CommandFailureHandler handler);

    /**
     * 在当前组下注册相对命令规范。
     */
    CommandGroup register(
            String spec,
            LinCommand.CommandExecutor executor,
            LinCommand.Permission permission,
            LinCommand.ExecTarget target,
            LinCommand.Desc description,
            Map<String, Map<String, String>> labels
    );

    /**
     * 在当前组下注册使用动态语言字段的相对命令规范。
     */
    CommandGroup registerLazy(
            String spec,
            LinCommand.CommandExecutor executor,
            LinCommand.Permission permission,
            LinCommand.ExecTarget target,
            LinCommand.I18nSupplier description,
            Map<String, LinCommand.I18nSupplier> labels
    );

    /**
     * 在当前组下注册使用语言字段引用的相对命令规范。
     */
    default CommandGroup register(
            String spec,
            LinCommand.CommandExecutor executor,
            LinCommand.Permission permission,
            LinCommand.ExecTarget target,
            LinCommand.I18n i18n
    ) {
        if (i18n == null) {
            return registerLazy(spec, executor, permission, target, null, Map.of());
        }
        return registerLazy(spec, executor, permission, target, i18n.description(), i18n.labels());
    }

    /**
     * 在当前组下注册不包含参数标签的静态命令。
     */
    default CommandGroup register(
            String spec,
            LinCommand.CommandExecutor executor,
            LinCommand.Permission permission,
            LinCommand.ExecTarget target,
            LinCommand.Desc description
    ) {
        return register(spec, executor, permission, target, description, Map.of());
    }
}
