package api.linlang.command.group;

import api.linlang.command.LinCommand;

/**
 * 命令组的成功回调。
 */
@FunctionalInterface
public interface CommandSuccessHandler {

    /**
     * 在叶子命令执行器正常完成后调用。
     *
     * @param context 命令执行上下文
     * @throws Exception 回调执行失败时
     */
    void handle(LinCommand.Ctx context) throws Exception;
}
