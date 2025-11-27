package api.linlang.runtime;

import java.util.function.Function;

public final class LinOptions {
    public String initialLocale;
    public Boolean usePluginLogger;
    public String fixedPrefix;
    public Function<Object, String> prefixProvider;

    /**
     * Linlang 启动的本地化
     *
     * @param v 地区代码，遵循 <code>region_LANGUAGE</code> 格式，如 <code>zh_CN</code>
     * @return
     */
    public LinOptions initialLocale(String v) {
        this.initialLocale = v;
        return this;
    }

    /**
     * <p>是否使得琳琅审计（LinLog）归于 <code>plugin.getLogger()</code> 作为琳琅审计（LinLog）的日志</p>
     *
     * @param v 布尔值
     * @return
     */
    public LinOptions pluginLogger(boolean v) {
        this.usePluginLogger = v;
        return this;
    }

    /**
     * <p>设置琳琅内建提示消息的前缀，可便于消息识别。默认情况下，无前缀或提示为 [linlang]</p>
     *
     * <p>与 <code>dynamicFixedPrefix()</code> 互斥</p>
     *
     * @param v 前缀文本
     * @return
     */
    public LinOptions fixedPrefix(String v) {
        this.fixedPrefix = v;
        this.prefixProvider = null;
        return this;
    }

    /**
     * <p>设置琳琅内建提示消息的前缀，可便于消息识别。默认情况下，无前缀或提示为 [linlang]</p>
     * <b>动态前缀</b>
     * <p>与 <code>fixedPrefix()</code> 互斥</p>
     *
     * @param f
     * @return
     */
    public LinOptions dynamicFixedPrefix(Function<Object, String> f) {
        this.prefixProvider = f;
        this.fixedPrefix = null;
        return this;
    }

    /**
     * 将本选项应用到 Linlang 的“配置器视图”（不触发 reload，由调用方决定何时 reload）
     */
    public void applyTo(Linlang.Configurable lin) {
        if (usePluginLogger != null) lin.withPluginLogger(usePluginLogger);
        if (initialLocale   != null) lin.withInitialLanguage(initialLocale);
        if (prefixProvider  != null) lin.withCommandPrefixProvider(prefixProvider);
        else if (fixedPrefix != null) lin.withCommandPrefix(fixedPrefix);
    }
}