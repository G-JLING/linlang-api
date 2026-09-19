package api.linlang.audit.log;

/**
 * 可由日志提供者本地化的结构化日志模板。
 *
 * <p>代码用于稳定标识日志事件，默认文本用于运行时尚未安装对应目录时回退。
 * 模板本身不保存参数，参数仍由具体日志调用提供。</p>
 */
public interface LogTemplate {

    /**
     * 返回稳定的日志代码。
     *
     * @return 日志代码
     */
    String code();

    /**
     * 返回不依赖外部语言文件的默认文本。
     *
     * @return 默认消息模板
     */
    String fallback();
}
