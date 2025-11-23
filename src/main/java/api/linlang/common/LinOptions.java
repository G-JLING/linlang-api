package api.linlang.common;

import java.util.function.Function;

public final class LinOptions {
    public String initialLocale;
    public Boolean usePluginLogger;
    public String fixedPrefix;
    public Function<Object, String> prefixProvider;

    public LinOptions initialLocale(String v) {
        this.initialLocale = v;
        return this;
    }

    public LinOptions pluginLogger(boolean v) {
        this.usePluginLogger = v;
        return this;
    }

    public LinOptions fixedPrefix(String v) {
        this.fixedPrefix = v;
        this.prefixProvider = null;
        return this;
    }

    public LinOptions dynamicPrefix(Function<Object, String> f) {
        this.prefixProvider = f;
        this.fixedPrefix = null;
        return this;
    }

    /**
     * 将本选项应用到 Linlang 实例（不触发 reload，由调用方决定何时 reload）
     */
    public void applyTo(Linlang lin) {
        if (usePluginLogger != null) lin.withPluginLogger(usePluginLogger);
        if (initialLocale != null) lin.withInitialLanguage(initialLocale);
        if (prefixProvider != null) lin.withCommandPrefixProvider(prefixProvider);
        else if (fixedPrefix != null) lin.withCommandPrefix(fixedPrefix);
    }
}