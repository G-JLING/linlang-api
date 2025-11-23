package api.linlang.common;

import java.util.ServiceLoader;

public final class Lin {
    private static volatile Linlang cached;

    public static Linlang require() {
        Linlang x = current();
        if (x == null) {
            throw new IllegalStateException(
                    "Linlang runtime not found. Install LinlangRuntime plugin compatible with API " +
                            Linlang.API_VERSION + "."
            );
        }
        return x;
    }

    public static Linlang current() {
        if (cached != null) return cached;

        // 1) Bukkit 优先：从 ServicesManager 加载
        try {
            Class<?> bukkit = Class.forName("org.bukkit.Bukkit");
            Object sm = bukkit.getMethod("getServicesManager").invoke(null);
            Object svc = sm.getClass().getMethod("load", Class.class).invoke(sm, Linlang.class);
            if (svc != null) return cached = (Linlang) svc;
        } catch (Throwable ignore) {}

        // 2) 退回 SPI（META-INF/services）
        for (Linlang impl : ServiceLoader.load(Linlang.class)) {
            return cached = impl;
        }
        return null;
    }

    private Lin() {}
}