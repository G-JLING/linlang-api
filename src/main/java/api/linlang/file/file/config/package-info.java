/**
 * 配置文本引用与加载诊断类型。
 *
 * <p>{@link api.linlang.file.file.config.ConfigText} 和
 * {@link api.linlang.file.file.config.ConfigList} 可以在配置中保存字面值或语言引用，
 * 读取时解析当前内容，写回时保留来源定义。
 * {@link api.linlang.file.file.config.ConfigIssue} 用配置路径标识具体问题；
 * 批量加载失败时可通过 {@link api.linlang.file.file.config.ConfigLoadException}
 * 查看各文件的问题明细。</p>
 */
package api.linlang.file.file.config;
