package api.linlang.runtime;

import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * Linlang 运行时发现与插件级门面的统一入口。
 *
 * <p>通常使用 {@link #setup(Object, LinOptions)} 完成发现、插件上下文注入、选项应用和重载。</p>
 */
public final class Lin {

    /**
     * 当前 API 接口版本
     *
     * @hidden
     */
    public static final String API_VERSION = "2.2.1.0";

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
            return requireFacade(out, m);
        } catch (NoSuchMethodException ignored) {
        } catch (ReflectiveOperationException e) {
            throw facadeCreationFailed(e);
        }

        // 回退：查找兼容的单参数工厂方法
        for (java.lang.reflect.Method m : implClass.getMethods()) {
            if (!m.getName().equals("createFacade") && !m.getName().equals("create")) continue;
            Class<?>[] params = m.getParameterTypes();
            if (params.length != 1) continue;
            if (!params[0].isAssignableFrom(ctxClass)) continue;
            try {
                Object out = m.invoke(lin, platformContext);
                return requireFacade(out, m);
            } catch (ReflectiveOperationException e) {
                throw facadeCreationFailed(e);
            }
        }

        return lin;
    }

    private static Linlang requireFacade(Object value, java.lang.reflect.Method method) {
        if (value instanceof Linlang facade) return facade;
        throw new IllegalStateException("Facade factory returned an incompatible value: " + method);
    }

    private static IllegalStateException facadeCreationFailed(ReflectiveOperationException exception) {
        Throwable cause = exception;
        if (exception instanceof java.lang.reflect.InvocationTargetException invocation
                && invocation.getCause() != null) {
            cause = invocation.getCause();
        }
        return new IllegalStateException("Failed to create Linlang facade.", cause);
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
        boolean bukkitAvailable = false;
        try {
            Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
            bukkitAvailable = true;
            Object sm = bukkit.getMethod("getServicesManager").invoke(null);
            Object svc = sm.getClass().getMethod("load", Class.class).invoke(sm, Linlang.class);
            if (svc != null) return cached = (Linlang) svc;
            cached = null;
        } catch (Throwable ignore) {
        }

        if (bukkitAvailable) return null;
        if (cached != null) return cached;

        for (Linlang impl : ServiceLoader.load(Linlang.class)) {
            return cached = impl;
        }
        return null;
    }

    /**
     * 发现运行时、创建插件级门面、注入平台上下文并重载。
     *
     * @param platformContext 运行环境上下文，在主类中传递 <code>this</code> 即可
     * @return 已就绪但未应用个性化设置的插件级门面
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
     * 发现运行时、创建插件级门面、应用选项并重载。
     *
     * @param platformContext 运行环境上下文，在主类中传递自身即可
     * @param linOptions      琳琅个性化设置 {@link LinOptions} 实例
     * @return 已应用选项并完成重载的插件级门面
     */
    public static Linlang setup(Object platformContext, LinOptions linOptions) {
        var lin = find();
        lin = maybeCreateFacade(lin, platformContext);
        if (lin instanceof Linlang.Parametric p) {
            p.withPlatformContext(platformContext);
            if (linOptions != null) linOptions.applyParameters(p);
        }
        if (lin instanceof Linlang.Configurable c && linOptions != null) {
            linOptions.applyTo(c);
        }
        lin.reload();
        return lin;
    }

    /**
     * 发现运行时，并通过回调在插件级门面可用后创建和应用选项。
     *
     * @param platformContext 运行环境上下文，在主类中传递自身即可
     * @param optionsBuilder  函数式接口，回调：接受 {@link Linlang}，返回要应用的 {@link LinOptions}，可为 {@code null}
     * @return 已应用选项并完成重载的插件级门面
     */
    public static Linlang setup(Object platformContext, Function<Linlang, LinOptions> optionsBuilder) {
        var lin = find();
        lin = maybeCreateFacade(lin, platformContext);
        if (lin instanceof Linlang.Parametric p) {
            p.withPlatformContext(platformContext);
        }
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
     * 应用 {@link LinOptions}，但不主动重载运行时。
     *
     * <p>适合需要由调用方决定重载时机的两阶段装配。</p>
     *
     * @param platformContext 宿主上下文
     * @param opts            装配选项，可为 {@code null}
     * @return 已应用选项但未重载的插件级门面
     */
    public static Linlang configure(Object platformContext, LinOptions opts) {
        var lin = find();
        lin = maybeCreateFacade(lin, platformContext);
        if (lin instanceof Linlang.Parametric p) {
            p.withPlatformContext(platformContext);
            if (opts != null) opts.applyParameters(p);
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
