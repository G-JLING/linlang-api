package api.linlang.runtime;

import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * 入口门面，其发现并缓存运行时的 {@link Linlang} 实现，是唯一使用入口。
 */
public final class Lin {

    /**
     * 当前 API 接口版本
     *
     * @hidden
     */
    public static final String API_VERSION = "1.0.4.6";

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
     * @hidden
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
     * 装载：发现 → 注入环境上下文 → {@code reload()}
     * <p>用于分步骤初始化琳琅服务</p>
     *
     * @param platformContext 运行环境上下文，在主类中传递 <code>this</code> 即可
     * @return 已就绪的运行时实例，但未进行任何个性化设置
     */
    public static Linlang init(Object platformContext){
        var lin = find();
        if (lin instanceof Linlang.Configurable c) {
            c.withPlatformContext(platformContext);
            c.reload();
        }
        return lin;
    }

    /**
     * 装载：发现 -> 注入环境上下文 -> 通过回调读取个性化选项 {@link LinOptions} -> 应用并 {@code reload()}
     * <p>用于一站式初始化琳琅服务，且配置由琳琅托管，需要先绑定配置/语言再决定前缀与语言</p>
     *
     * @param platformContext 运行环境上下文，在主类中传递自身即可
     * @param linOptions 琳琅个性化设置 {@link LinOptions} 实例
     * @return 已就绪的运行时实例，且进行了个性化设置
     */
    public static Linlang setup(Object platformContext, LinOptions linOptions){
        var lin = find();
        if (lin instanceof Linlang.Configurable c) {
            c.withPlatformContext(platformContext);
            if (linOptions != null) linOptions.applyTo(c);
            c.reload();
        }
        return lin;
    }


    /**
     * 装载：发现 -> 注入环境上下文 -> 通过回调读取个性化选项 {@link LinOptions} -> 应用并 {@code reload()}
     * <p>用于一站式初始化琳琅服务，且配置由琳琅托管，需要先绑定配置/语言再决定前缀与语言</p>
     *
     * <p>在 <a href="http://jling.me/p/linlang/project/初始化">初始化</a> 页面中，您可以看到使用示例</p>
     *
     * @param platformContext 运行环境上下文，在主类中传递自身即可
     * @param optionsBuilder 函数式接口，回调：接受 {@link Linlang}，返回要应用的 {@link LinOptions}，可为 {@code null}
     * @return 已就绪的运行时实例，且进行了个性化设置
     */
    public static Linlang setup(Object platformContext, Function<Linlang, LinOptions> optionsBuilder) {
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
     * 接受 {@link LinOptions} 实例，对琳琅服务进行个性化设置
     * <p>适合两阶段装配：先<code>Linlang lin = Lin.init()</code> 获得琳琅服务，然后使用此方法完成自定义初始化/p>
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