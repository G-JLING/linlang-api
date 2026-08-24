package api.linlang.text;

/**
 * 可在发送时解析为高级字符串源码的文本来源。
 *
 * <p>固定的 {@link LinText} 与动态的语言字段引用均可实现该接口。消息服务会在每次发送时解析来源，
 * 因此动态来源可以跟随语言文件重载。</p>
 */
public interface TextSource {

    /**
     * 解析当前文本源码。
     *
     * @return 高级字符串源码，不为 {@code null}
     */
    String resolve();

    /**
     * 按指定语言解析文本源码。
     *
     * <p>固定文本默认忽略语言参数。支持多语言的来源可以重写此方法。</p>
     *
     * @param locale 语言标签
     * @return 高级字符串源码，不为 {@code null}
     */
    default String resolve(String locale) {
        return resolve();
    }
}
