package api.linlang.command.group;

import api.linlang.command.LinCommand;

/**
 * 命令服务中的根命令。
 */
public interface CommandRoot extends CommandGroup {

    /**
     * 设置用于组合相对权限片段的权限前缀。
     *
     * @param prefix 权限前缀；传入 {@code null} 或空白文本表示不设置
     * @return 当前根命令
     */
    CommandRoot permissionPrefix(String prefix);

    @Override
    CommandRoot permissionSegment(String segment);

    @Override
    CommandRoot requires(LinCommand.Permission permission);

    @Override
    CommandRoot onSuccess(CommandSuccessHandler handler);

    @Override
    CommandRoot onFailure(CommandFailureHandler handler);
}
