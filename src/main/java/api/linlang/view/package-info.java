/**
 * 基于资源定义的界面服务。
 *
 * <p>{@link api.linlang.view.LinView} 负责打开界面、注册动态数据源和业务 Hook。
 * 每次打开都会创建独立会话；会话状态、静态控件覆盖与动态区访问分别由
 * {@link api.linlang.view.session.GuiSession} 的成员提供。</p>
 *
 * <p>资源文件定义外观与动作，业务代码通过 {@code spi} 子包提供数据和点击处理。
 * 应在打开相关界面之前注册 Source 和 Hook。</p>
 */
package api.linlang.view;
