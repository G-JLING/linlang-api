/**
 * 高级字符串的解析、组合和变量绑定。
 *
 * <p>文本源码支持 {@code 内容[@color=gold;bold]} 形式的后置描述符；
 * {@code []} 用于结束当前片段，{@code \[} 和 {@code \]} 表示字面中括号。
 * 普通变量以 {@code {name}} 引用，由 {@link api.linlang.text.TextArgs} 在解析后绑定，
 * 因此变量值不会被再次当作描述符执行。需要嵌入可信高级文本时使用
 * {@link api.linlang.text.TextArgs.Builder#rich(String, api.linlang.text.TextSource)}。</p>
 *
 * <p>聊天等支持组件的平台入口可保留点击和悬浮事件；转换为传统颜色代码或纯文本时，
 * 这些交互能力不会保留。</p>
 */
package api.linlang.text;
