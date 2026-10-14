/**
 * 单个 viewer 的界面会话及其控件集合。
 *
 * <p>{@link api.linlang.view.session.GuiSession} 持有状态、静态区和动态区；
 * {@link api.linlang.view.session.StaticView} 支持按 UID 覆盖资源定义的控件，
 * {@link api.linlang.view.session.DynamicAreas} 用于访问已加载的动态区。
 * 会话仅在所属 {@link api.linlang.view.LinView} 生命周期内有效。</p>
 */
package api.linlang.view.session;
