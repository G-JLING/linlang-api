package api.linlang.common;

import java.util.ServiceLoader;

/**
 * 入口门面：负责发现并缓存运行时的 {@link Linlang} 实现，并提供装配便捷方法。
 *
 * <p><b>发现顺序</b>：优先通过 Bukkit 的 ServicesManager；否则回退 Java SPI（META-INF/services）。</p>
 * <p><b>线程安全</b>：惰性发现 + {@code volatile} 缓存。</p>
 * <p><b>使用建议</b>：非琳琅托管配置文件，可用 {@link #setup(Object, LinOptions)}；否则 {@link #init(Object, java.util.function.Function)}，
 */
public final class Lin {

    // 当前 API 接口版本
    public static final String API_VERSION = "1.0.4.5";

    /**
     * 获取已注册的运行时实现；若不存在则抛出 {@link IllegalStateException}。
     *
     * @return 非空的运行时实例
     * @throws IllegalStateException 未安装兼容的 LinlangRuntime 时
     */
    public static Linlang find() {
        Linlang x = getOrNull();
        if (x == null) {
            throw new IllegalStateException(
                    "Linlang runtime not found. Install LinlangRuntime plugin compatible with API " +
                            API_VERSION + "."
            );
        }
        return x;
    }

    /**
     * 尝试发现并返回运行时实现；若不存在返回 {@code null}。
     * <p>首次调用会执行发现并缓存结果，后续调用将复用缓存。</p>
     *
     * @return 运行时实例，或 {@code null}
     */
    public static Linlang getOrNull() {
        if (cached != null) return cached;

        try {
            Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
            Object sm = bukkit.getMethod("getServicesManager").invoke(null);
            Object svc = sm.getClass().getMethod("load", Class.class).invoke(sm, Linlang.class);
            if (svc != null) return cached = (Linlang) svc;
        } catch (Throwable ignore) {}

        for (Linlang impl : ServiceLoader.load(Linlang.class)) {
            return cached = impl;
        }
        return null;
    }

    /**
     * 装载：发现 → 注入环境上下文 → 应用选项 → {@code reload()}。
     * <p>若 {@code opts} 为 {@code null}，仅注入上下文并重载一次。</p>
     *
     * @param platformContext 宿主上下文（Bukkit 传 {@code JavaPlugin}，其他平台传相应句柄）
     * @param opts 装配选项，可为 {@code null}
     * @return 已就绪的运行时实例
     */
    public static Linlang setup(Object platformContext, LinOptions opts){
        var lin = find();
        if (lin instanceof Linlang.Configurable c) {
            c.withPlatformContext(platformContext);
            if (opts != null) opts.applyTo(c);
            c.reload();
        }
        return lin;
    }

    /**
     * 适用于琳琅托管文件的装载：首先注入上下文，再通过回调读取配置/语言以构建选项，随后应用并 {@code reload()}。
     * <p>典型：配置由琳琅托管，需要先绑定配置/语言再决定前缀与语言。</p>
     *
     * @param platformContext 宿主上下文
     * @param optionsBuilder 回调：接受 {@link Linlang}，返回要应用的 {@link LinOptions}，可为 {@code null}
     * @return 已就绪的运行时实例
     */
    public static Linlang init(Object platformContext,
                               java.util.function.Function<Linlang, LinOptions> optionsBuilder) {
        var lin = find();
        if (lin instanceof Linlang.Configurable c) {
            c.withPlatformContext(platformContext);
            LinOptions opts = (optionsBuilder != null) ? optionsBuilder.apply(lin) : null;
            if (opts != null) opts.applyTo(c);
            c.reload();
        }
        return lin;
    }

    /**
     * 只做“发现 → 注入上下文 → 应用选项”，不触发 {@code reload()}。
     * <p>适合两阶段装配：先 {@code configure(...)}，完成自定义初始化后再显式调用 {@code reload()}。</p>
     *
     * @param platformContext 宿主上下文
     * @param opts 装配选项，可为 {@code null}
     * @return 运行时实例（已应用选项但未重载）
     */
    public static Linlang configure(Object platformContext, LinOptions opts){
        var lin = find();
        if (lin instanceof Linlang.Configurable c) {
            c.withPlatformContext(platformContext);
            if (opts != null) opts.applyTo(c);
        }
        return lin;
    }

    private static volatile Linlang cached;

    private Lin() {}
}