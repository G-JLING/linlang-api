package api.linlang.command.message;

/**
 * 命令提示与错误消息提供者。
 *
 * <p>运行时通常按照“注册覆盖、语言文件、内建默认值”的优先级解析消息。</p>
 */
public interface CommandMessages {
    /**
     * 根据键获取消息。
     * @param key 消息键，如 "prompt.click.block"
     * @param kv  可选参数，用于字符串格式化 (key1, val1, key2, val2 …)
     * @return 格式化后的消息；未知键通常回显键名
     */
    String get(String key, Object... kv);

    /**
     * @return 使用内建中文文本的默认消息提供者
     */
    static CommandMessages defaults() {
        return DefaultCommandMessage.INSTANCE;
    }
}
