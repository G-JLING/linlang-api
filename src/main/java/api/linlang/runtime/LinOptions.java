package api.linlang.runtime;

import java.util.function.Function;

/**
 * 琳琅服务的个性化设置类，仅适用于 {@link Lin#configure(Object, LinOptions)}
 *
 * <p>您可以不进行个性化设置，不会影响琳琅工作。但可能会使得一些服务的表现不像您想得那样。</p>
 */
public final class LinOptions {
    public String initialLocale;
    public Boolean usePluginLogger;
    public String fixedPrefix;
    public Function<Object, String> prefixProvider;

    /**
     * 设置琳琅的启动语言（运行参数）
     *
     * @param v 地区代码，遵循 <code>language_REGION</code> 格式，如 <code>zh_CN</code>
     */
    public LinOptions initialLocale(String v) {
        this.initialLocale = v;
        return this;
    }

    /**
     * 设置琳琅审计与日志服务的日志方式
     *
     * <p>是否使得琳琅审计（LinLog）归于平台包装的日志通道。如 Bukkit 的 <code>plugin.getLogger()</code>
     *
     * @param v 布尔值
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
     * <p>与 {@link #dynamicFixedPrefix(Function)} 选其一</p>
     *
     * @param prefix 前缀文本
     */
    public LinOptions fixedPrefix(String prefix) {
        this.fixedPrefix = prefix;
        this.prefixProvider = null;
        return this;
    }

    /**
     * 设置琳琅内建提示消息的前缀
     *
     * 设置琳琅内建提示消息的前缀，可便于消息识别。默认情况下，无前缀或提示为 [linlang]</p>
     *
     * <p>与 {@link #fixedPrefix(String)} 选其一</p>
     *
     * @param func 函数式接口
     */
    public LinOptions dynamicFixedPrefix(Function<Object, String> func) {
        this.prefixProvider = func;
        this.fixedPrefix = null;
        return this;
    }

    /**
     * 将本选项应用到 Linlang，等待 reload() 调用
     *
     * @hidden
     */
    public void applyTo(Linlang.Configurable lin) {
        if (usePluginLogger != null) lin.withPluginLogger(usePluginLogger);
        if (prefixProvider  != null) lin.withCommandPrefixProvider(prefixProvider);
        else if (fixedPrefix != null) lin.withCommandPrefix(fixedPrefix);
    }

    /**
     * 将本选项中的运行参数应用到 Linlang，等待 restart() 调用
     *
     * @hidden
     */
    public void applyParameters(Linlang.Parametric lin) {
        if (initialLocale != null) lin.withInitialLanguage(initialLocale);
    }
}