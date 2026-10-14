/**
 * 界面服务的业务扩展接口。
 *
 * <p>{@link api.linlang.view.spi.GuiSource} 加载动态区行数据，
 * {@link api.linlang.view.spi.GuiHook} 处理点击动作。
 * 两者通过 {@link api.linlang.view.LinView#source(String, api.linlang.view.spi.GuiSource)}
 * 和 {@link api.linlang.view.LinView#hook(String, api.linlang.view.spi.GuiHook)} 注册。</p>
 */
package api.linlang.view.spi;
