package api.linlang.runtime;

import java.util.function.Function;

/**
 * Linlang 初始化选项。
 *
 * <p>可传给 {@link Lin#setup(Object, LinOptions)} 或 {@link Lin#configure(Object, LinOptions)}。
 * 未设置的字段不会覆盖运行时默认值。</p>
 */
public final class LinOptions {
    /**
     * 期望的全局 locale；为 {@code null} 时不修改。
     */
    public String totalLocale;

    /**
     * 是否使用插件日志通道；为 {@code null} 时不修改。
     */
    public Boolean usePluginLogger;

    /**
     * 静态全局消息前缀；与 {@link #totalPrefixProvider} 互斥。
     */
    public String totalPrefix;

    /**
     * 动态全局消息前缀提供者；与 {@link #totalPrefix} 互斥。
     */
    public Function<Object, String> totalPrefixProvider;

    /**
     * 设置琳琅的启动语言（运行参数）
     *
     * @param v 地区代码，遵循 <code>language_REGION</code> 格式，如 <code>zh_CN</code>
     * @return 当前选项对象
     */
    public LinOptions totalLocale(String v) {
        this.totalLocale = v;
        return this;
    }

    /**
     * 设置琳琅审计与日志服务的日志方式
     *
     * <p>是否使得琳琅审计（LinLog）归于平台包装的日志通道。如 Bukkit 的 <code>plugin.getLogger()</code>
     *
     * @param v 布尔值
     * @return 当前选项对象
     */
    public LinOptions pluginLogger(boolean v) {
        this.usePluginLogger = v;
        return this;
    }

    /**
     * 设置琳琅内建提示消息的前缀
     *
     * <p>设置琳琅内建提示消息的前缀，可便于消息识别。默认情况下，无前缀或提示为 [linlang]</p>
     *
     * <p>与 {@link #dynamicTotalPrefix(Function)} 选其一</p>
     *
     * @param prefix 前缀文本
     * @return 当前选项对象
     */
    public LinOptions totalPrefix(String prefix) {
        this.totalPrefix = prefix;
        this.totalPrefixProvider = null;
        return this;
    }

    /**
     * 设置动态的内建提示消息前缀。
     *
     * <p>与 {@link #totalPrefix(String)} 选其一</p>
     *
     * @param func 根据消息接收者计算前缀的函数
     * @return 当前选项对象
     */
    public LinOptions dynamicTotalPrefix(Function<Object, String> func) {
        this.totalPrefixProvider = func;
        this.totalPrefix = null;
        return this;
    }

    /**
     * 将本选项应用到 Linlang，等待 reload() 调用
     *
     * @param lin 可配置的运行时门面
     * @hidden
     */
    public void applyTo(Linlang.Configurable lin) {
        if (usePluginLogger != null) lin.usingPluginLogger(usePluginLogger);
        if (totalPrefixProvider  != null) lin.totalPrefixProvider(totalPrefixProvider);
        else if (totalPrefix != null) lin.totalPrefix(totalPrefix);
    }

    /**
     * 将本选项中的运行参数应用到 Linlang，等待 restart() 调用
     *
     * @param lin 可设置运行参数的门面
     * @hidden
     */
    public void applyParameters(Linlang.Parametric lin) {
        if (totalLocale != null) lin.totalLocale(totalLocale);
    }
}
