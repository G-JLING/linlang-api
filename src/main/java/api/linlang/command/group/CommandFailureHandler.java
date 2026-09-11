package api.linlang.command.group;

/**
 * 命令组的失败回调。
 */
@FunctionalInterface
public interface CommandFailureHandler {

    /**
     * 在命令未能完成时调用。
     *
     * @param failure 命令失败信息
     * @throws Exception 回调执行失败时
     */
    void handle(CommandFailure failure) throws Exception;
}
