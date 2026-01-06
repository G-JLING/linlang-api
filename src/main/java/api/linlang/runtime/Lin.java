package api.linlang.runtime;

import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * 琳琅的入口门面，是唯一使用入口
 *
 * <p>其发现一个可用的琳琅服务提供给插件，并提供管理琳琅服务的方法</p>
 */
public final class Lin {

    /**
     * 当前 API 接口版本
     *
     * @hidden
     */
    public static final String API_VERSION = "1.2.1.0";

    /**
     * 尝试通过运行时提供的 factory 方法为指定 platformContext 创建一个 per-plugin 的 facade。
     * 若运行时未提供相应 factory，则返回原始 lin 实例。
     *
     * @param lin
     * @param platformContext
     * @return
     */
    private static Linlang maybeCreateFacade(Linlang lin, Object platformContext) {
        if (lin == null || platformContext == null) return lin;

        Class<?> implClass = lin.getClass();
        Class<?> ctxClass = platformContext.getClass();

        // 优先尝试精确签名 create(Object) 以兼容旧实现
        try {
            java.lang.reflect.Method m = implClass.getMethod("createFacade", Object.class);
            Object out = m.invoke(lin, platformContext);
            if (out instanceof Linlang ll) return ll;
        } catch (NoSuchMethodException ignored) {
            // fall through to generic search
        } catch (Throwable ignored) {
        }

        // 回退：查找任何名为 create 的单参数方法，只要参数类型是 platformContext 的父类/接口即可
        for (java.lang.reflect.Method m : implClass.getMethods()) {
            if (!m.getName().equals("create")) continue;
            Class<?>[] params = m.getParameterTypes();
            if (params.length != 1) continue;
            if (!params[0].isAssignableFrom(ctxClass)) continue;
            try {
                Object out = m.invoke(lin, platformContext);
                if (out instanceof Linlang ll) return ll;
            } catch (Throwable ignored) {
            }
        }

        return lin;
    }


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
        } catch (Throwable ignore) {
        }

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
    public static Linlang init(Object platformContext) {
        var lin = find();
        lin = maybeCreateFacade(lin, platformContext);
        if (lin instanceof Linlang.Parametric p) {
            p.withPlatformContext(platformContext);
        }
        lin.reload();
        return lin;
    }

    /**
     * 装载：发现 -> 注入环境上下文 -> 通过回调读取个性化选项 {@link LinOptions} -> 应用并 {@code reload()}
     * <p>用于一站式初始化琳琅服务，且配置由琳琅托管，需要先绑定配置/语言再决定前缀与语言</p>
     *
     * @param platformContext 运行环境上下文，在主类中传递自身即可
     * @param linOptions      琳琅个性化设置 {@link LinOptions} 实例
     * @return 已就绪的运行时实例，且进行了个性化设置
     */
    public static Linlang setup(Object platformContext, LinOptions linOptions) {
        var lin = find();
        lin = maybeCreateFacade(lin, platformContext);
        if (linOptions != null && lin instanceof Linlang.Parametric p) {
            linOptions.applyParameters(p);
        }
        if (lin instanceof Linlang.Configurable c && linOptions != null) {
            linOptions.applyTo(c);
        }
        lin.reload();
        return lin;
    }

    /**
     * 装载：发现 -> 注入环境上下文 -> 通过回调读取个性化选项 {@link LinOptions} -> 应用并 {@code reload()}
     * <p>用于一站式初始化琳琅服务，且配置由琳琅托管，需要先绑定配置/语言再决定前缀与语言</p>
     *
     * <p>在 <a href="http://jling.me/p/linlang/project/初始化">初始化</a> 页面中，您可以看到使用示例</p>
     *
     * @param platformContext 运行环境上下文，在主类中传递自身即可
     * @param optionsBuilder  函数式接口，回调：接受 {@link Linlang}，返回要应用的 {@link LinOptions}，可为 {@code null}
     * @return 已就绪的运行时实例，且进行了个性化设置
     */
    public static Linlang setup(Object platformContext, Function<Linlang, LinOptions> optionsBuilder) {
        var lin = find();
        lin = maybeCreateFacade(lin, platformContext);
        LinOptions opts = (optionsBuilder != null) ? optionsBuilder.apply(lin) : null;
        if (opts != null && lin instanceof Linlang.Parametric p) {
            opts.applyParameters(p);
        }
        if (lin instanceof Linlang.Configurable c && opts != null) {
            opts.applyTo(c);
        }
        lin.reload();
        return lin;
    }

    /**
     * 接受 {@link LinOptions} 实例，对琳琅服务进行个性化设置
     * <p>适合两阶段装配：先<code>Linlang lin = Lin.init()</code> 获得琳琅服务，然后使用此方法完成自定义初始化/p>
     *
     * @param platformContext 宿主上下文
     * @param opts            装配选项，可为 {@code null}
     * @return 运行时实例（已应用选项但未重载）
     */
    public static Linlang configure(Object platformContext, LinOptions opts) {
        var lin = find();
        lin = maybeCreateFacade(lin, platformContext);
        if (opts != null && lin instanceof Linlang.Parametric p) {
            opts.applyParameters(p);
        }
        if (lin instanceof Linlang.Configurable c && opts != null) {
            opts.applyTo(c);
        }
        return lin;
    }


    private static volatile Linlang cached;

    private Lin() {
    }
}