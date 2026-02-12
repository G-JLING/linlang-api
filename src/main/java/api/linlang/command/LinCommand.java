package api.linlang.command;

import java.util.*;
import java.util.function.Supplier;

/**
 * 琳琅命令服务。
 */
public interface LinCommand {

    LinCommand register(String spec, LinCommand.CommandExecutor exec, Permission perm, ExecTarget target, Desc desc,
                        Map<String, Map<String, String>> labelsI18n);

    /**
     * 注册命令（延迟 i18n 版本）。
     * <p>描述与参数标签通过 {@link I18nSupplier} 延迟取值：语言切换后无需重注册命令，help/usage 会自动使用新语言。</p>
     *
     * @param spec 命令规范字符串
     * @param exec 执行器
     * @param perm 权限
     * @param target 执行目标
     * @param descProvider 命令描述的延迟提供者（可为 null）
     * @param labelProviders 参数标签提供者：paramName -> supplier（可为 null/空）
     */
    LinCommand registerLazy(
            String spec,
            LinCommand.CommandExecutor exec,
            Permission perm,
            ExecTarget target,
            I18nSupplier descProvider,
            Map<String, I18nSupplier> labelProviders
    );

    /**
     * 注册命令（延迟 i18n 版本，无参数标签）。
     */
    default LinCommand registerLazy(
            String spec,
            LinCommand.CommandExecutor exec,
            Permission perm,
            ExecTarget target,
            I18nSupplier descProvider
    ) {
        return registerLazy(spec, exec, perm, target, descProvider, Map.of());
    }

    default LinCommand register(
            String spec,
            LinCommand.CommandExecutor exec,
            Permission perm,
            ExecTarget target,
            Desc desc
    ) {
        return register(spec, exec, perm, target, desc, Map.of());
    }

    /**
     * 延迟 i18n 提供者：在渲染 usage/help 时根据当前语言动态取值。
     * <p>
     * 典型用法：直接引用语言文件服务 bind 后的字段（字段会在语言切换/重载时就地刷新）。
     * </p>
     */
    @FunctionalInterface
    interface I18nSupplier {
        /**
         * @param localeTag 当前语言（例如 zh_CN / en_GB）。实现可以忽略该参数。
         * @return 文本（可为 null，代表无值/回退）。
         */
        String get(String localeTag);

        /**
         * 将一个无参 Supplier 包装为 i18n supplier（忽略 locale）。
         */
        static I18nSupplier of(Supplier<String> s) {
            return locale -> s == null ? null : s.get();
        }

        /**
         * 将静态 i18n Map 包装为 i18n supplier（宽松匹配 zh_CN / zh-CN / zh）。
         */
        static I18nSupplier from(Map<String, String> i18n) {
            return locale -> {
                if (i18n == null || i18n.isEmpty()) return null;
                if (locale == null || locale.isBlank()) {
                    return i18n.getOrDefault("zh_CN", i18n.values().iterator().next());
                }
                String tag = locale;
                String dash = tag.replace('_', '-');
                String langOnly = tag.contains("_") ? tag.substring(0, tag.indexOf('_'))
                        : (tag.contains("-") ? tag.substring(0, tag.indexOf('-')) : tag);

                String v = i18n.get(tag);
                if (v == null) v = i18n.get(dash);
                if (v == null) v = i18n.get(langOnly);
                if (v == null) v = i18n.get("zh_CN");
                if (v == null) v = i18n.get("en_GB");
                if (v == null) v = i18n.values().iterator().next();
                return v;
            };
        }
    }

    interface Labels {
        Labels add(String key, String locale, String text);
        Map<String, Map<String,String>> build();

        static Labels create() { return new Impl(); }

        final class Impl implements Labels {
            private final Map<String, Map<String,String>> m = new LinkedHashMap<>();
            public Labels add(String key, String locale, String text) {
                m.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(locale, text);
                return this;
            }
            public Map<String, Map<String,String>> build() { return m; }
        }
    }

    /**
     * 延迟参数标签构建器：每个参数只需要提供一个 supplier（通常直接引用语言对象字段）。
     */
    interface LazyLabels {
        LazyLabels add(String paramName, I18nSupplier supplier);
        Map<String, I18nSupplier> build();

        static LazyLabels create() { return new Impl(); }

        final class Impl implements LazyLabels {
            private final Map<String, I18nSupplier> m = new LinkedHashMap<>();
            @Override
            public LazyLabels add(String paramName, I18nSupplier supplier) {
                if (paramName == null) throw new IllegalArgumentException("paramName");
                if (supplier == null) throw new IllegalArgumentException("supplier");
                m.put(paramName, supplier);
                return this;
            }
            @Override
            public Map<String, I18nSupplier> build() { return m; }
        }
    }

    /**
     * 便捷构造：paramName, supplier, paramName, supplier...
     */
    static Map<String, I18nSupplier> lazyLabels(Object... kv2) {
        Map<String, I18nSupplier> out = new LinkedHashMap<>();
        if (kv2 == null || kv2.length == 0) return out;
        if ((kv2.length & 1) == 1) throw new IllegalArgumentException("odd kv length");
        for (int i = 0; i + 1 < kv2.length; i += 2) {
            String key = String.valueOf(kv2[i]);
            I18nSupplier sup = (I18nSupplier) kv2[i + 1];
            out.put(key, sup);
        }
        return out;
    }

    static Map<String, Map<String,String>> labels(Object... kv3) {
        Map<String, Map<String,String>> out = new LinkedHashMap<>();
        for (int i = 0; i + 2 < kv3.length; i += 3) {
            String key = (String) kv3[i];
            String loc = (String) kv3[i+1];
            String txt = (String) kv3[i+2];
            out.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(loc, txt);
        }
        return out;
    }

    default LinCommand register(String spec, CommandExecutor exec, Permission perm,
                                ExecTarget target, Desc desc, Labels labels) {
        return register(spec, exec, perm, target, desc, labels.build());
    }

    /**
     * 注册命令（延迟 i18n 版本，使用 LazyLabels 构建参数标签）。
     */
    default LinCommand registerLazy(
            String spec,
            CommandExecutor exec,
            Permission perm,
            ExecTarget target,
            I18nSupplier descProvider,
            LazyLabels labels
    ) {
        return registerLazy(spec, exec, perm, target, descProvider, labels == null ? Map.of() : labels.build());
    }


    interface Ctx {
        // 平台 sender（Bukkit: CommandSender）
        Object sender();
        <T> T get(String name);
        <T> T getOr(String name, T def);
        default <T> T requirePlayer(String err){ throw new IllegalStateException(err); }
        Locale locale();
    }

    @FunctionalInterface interface CommandExecutor { void run(Ctx ctx) throws Exception; }
    enum ExecTarget { PLAYER, CONSOLE, ALL }
    record Permission(String node) { public static Permission perms(String n){ return new Permission(n); } }
    record Desc(Map<String,String> i18n) { public static Desc desc(Object...kv){ var m=new LinkedHashMap<String,String>(); for(int i=0;i+1<kv.length;i+=2)m.put((String)kv[i],(String)kv[i+1]); return new Desc(m);} }

    // 类型解析 SPI
    interface TypeResolver {
        boolean supports(String typeId);                           // enum / int / float / string / regex / minecraft:item 等
        Object parse(ParseCtx ctx, String token) throws Exception; // 已解析对象
        List<String> complete(ParseCtx ctx, String prefix);        // Tab 候选
    }
    interface ParseCtx {
        Map<String,Object> vars();                                 // 已解析参数（供后续约束使用）
        Map<String,String> meta();                                 // 形如 min/max/regex/tag 的补充
        Object platform();                                         // Bukkit Plugin/Server 等
        Object sender();                                           // 平台 sender
    }
}