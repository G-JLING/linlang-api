package api.linlang.command;

import api.linlang.file.file.LangText;

import java.util.*;
import java.util.function.Supplier;

/**
 * Linlang 命令服务。
 *
 * <p>命令由规范字符串、执行器、权限、执行目标、描述和参数标签组成。规范字符串的
 * 解析规则由运行时实现提供。</p>
 */
public interface LinCommand {

    /**
     * 注册带静态国际化文本的命令。
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @param perm 权限要求，可为 {@code null}
     * @param target 允许的执行目标
     * @param desc 命令描述，可为 {@code null}
     * @param labelsI18n 参数名到 locale 文本的映射
     * @return 当前命令服务
     */
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
     * @return 当前命令服务
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
     * 注册不包含参数标签的延迟国际化命令。
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @param perm 权限要求，可为 {@code null}
     * @param target 允许的执行目标
     * @param descProvider 命令描述提供者，可为 {@code null}
     * @return 当前命令服务
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

    /**
     * 使用语言字段引用注册命令。
     *
     * <p>命令描述和参数标签在生成帮助及用法文本时解析，因此语言切换或语言文件重载后
     * 不需要重新注册命令。参数标签由参数名匹配</p>
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @param perm 权限要求，可为 {@code null}
     * @param target 允许的执行目标
     * @param i18n 命令国际化文本，可为 {@code null}
     * @return 当前命令服务
     */
    default LinCommand register(
            String spec,
            LinCommand.CommandExecutor exec,
            Permission perm,
            ExecTarget target,
            I18n i18n
    ) {
        if (i18n == null) {
            return registerLazy(spec, exec, perm, target, null, Map.of());
        }
        return registerLazy(spec, exec, perm, target, i18n.description(), i18n.labels());
    }

    /**
     * 注册不包含参数标签的静态国际化命令。
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @param perm 权限要求，可为 {@code null}
     * @param target 允许的执行目标
     * @param desc 命令描述，可为 {@code null}
     * @return 当前命令服务
     */
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
         * 将无参文本提供者包装为国际化提供者。
         *
         * @param s 文本提供者，可为 {@code null}
         * @return 忽略 locale 的国际化提供者
         */
        static I18nSupplier of(Supplier<String> s) {
            return locale -> s == null ? null : s.get();
        }

        /**
         * 将语言字段引用包装为国际化提供者。
         *
         * @param text 语言字段引用，可为 {@code null}
         * @return 按渲染 locale 解析引用的提供者
         */
        static I18nSupplier from(LangText text) {
            return locale -> text == null ? null : text.resolve(locale);
        }

        /**
         * 将静态 locale 映射包装为国际化提供者。
         *
         * <p>查找顺序为完整标签、短横线标签、语言标签、{@code zh_CN}、{@code en_GB}，
         * 最后回退到映射中的首个值。</p>
         *
         * @param i18n locale 到文本的映射
         * @return 使用宽松 locale 匹配的国际化提供者
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

    /**
     * 静态国际化参数标签构建器。
     */
    interface Labels {
        /**
         * 添加参数标签。
         *
         * @param key 命令参数名
         * @param locale locale 标签
         * @param text 显示文本
         * @return 当前构建器
         */
        Labels add(String key, String locale, String text);

        /**
         * @return 参数名到 locale 文本的映射
         */
        Map<String, Map<String,String>> build();

        /**
         * @return 新的标签构建器
         */
        static Labels create() { return new Impl(); }

        /**
         * {@link Labels} 的默认实现。
         */
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
     * 延迟国际化参数标签构建器。
     */
    interface LazyLabels {
        /**
         * 添加参数标签提供者。
         *
         * @param paramName 命令参数名
         * @param supplier 文本提供者
         * @return 当前构建器
         */
        LazyLabels add(String paramName, I18nSupplier supplier);

        /**
         * 添加语言字段引用。
         *
         * @param paramName 命令参数名
         * @param text 语言字段引用
         * @return 当前构建器
         */
        default LazyLabels add(String paramName, LangText text) {
            return add(paramName, I18nSupplier.from(text));
        }

        /**
         * @return 参数名到文本提供者的映射
         */
        Map<String, I18nSupplier> build();

        /**
         * @return 新的延迟标签构建器
         */
        static LazyLabels create() { return new Impl(); }

        /**
         * {@link LazyLabels} 的默认实现。
         */
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
     * 命令描述与参数标签的动态国际化文本。
     *
     * <p>该构建器保存 {@link LangText} 的解析过程，而不是注册时的字符串值。</p>
     */
    interface I18n {

        /**
         * 设置命令描述。
         *
         * @param text 语言字段引用
         * @return 当前构建器
         */
        I18n desc(LangText text);

        /**
         * 设置参数标签。
         *
         * @param paramName 命令规范中的参数名
         * @param text 语言字段引用
         * @return 当前构建器
         */
        I18n label(String paramName, LangText text);

        /**
         * 返回命令描述提供者。
         *
         * @return 描述提供者，可为 {@code null}
         */
        I18nSupplier description();

        /**
         * 返回参数标签提供者。
         *
         * @return 不可修改的参数标签映射
         */
        Map<String, I18nSupplier> labels();

        /**
         * 创建命令国际化文本构建器。
         *
         * @return 新构建器
         */
        static I18n create() {
            return new Impl();
        }

        /**
         * {@link I18n} 的默认实现。
         */
        final class Impl implements I18n {
            private I18nSupplier description;
            private final Map<String, I18nSupplier> labels = new LinkedHashMap<>();

            @Override
            public I18n desc(LangText text) {
                this.description = I18nSupplier.from(Objects.requireNonNull(text, "text"));
                return this;
            }

            @Override
            public I18n label(String paramName, LangText text) {
                if (paramName == null || paramName.isBlank()) {
                    throw new IllegalArgumentException("paramName");
                }
                labels.put(paramName, I18nSupplier.from(Objects.requireNonNull(text, "text")));
                return this;
            }

            @Override
            public I18nSupplier description() {
                return description;
            }

            @Override
            public Map<String, I18nSupplier> labels() {
                return Collections.unmodifiableMap(new LinkedHashMap<>(labels));
            }
        }
    }

    /**
     * 通过“参数名、提供者”对构造延迟标签映射。
     *
     * @param kv2 交替排列的参数名和 {@link I18nSupplier}
     * @return 标签映射
     * @throws IllegalArgumentException 参数数量为奇数时
     * @throws ClassCastException 提供者不是 {@link I18nSupplier} 时
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

    /**
     * 通过“参数名、locale、文本”三元组构造静态标签映射。
     *
     * @param kv3 重复排列的参数名、locale 和文本
     * @return 标签映射
     */
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

    /**
     * 使用标签构建器注册静态国际化命令。
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @param perm 权限要求，可为 {@code null}
     * @param target 允许的执行目标
     * @param desc 命令描述，可为 {@code null}
     * @param labels 参数标签构建器
     * @return 当前命令服务
     */
    default LinCommand register(String spec, CommandExecutor exec, Permission perm,
                                ExecTarget target, Desc desc, Labels labels) {
        return register(spec, exec, perm, target, desc, labels.build());
    }

    /**
     * 使用标签构建器注册延迟国际化命令。
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @param perm 权限要求，可为 {@code null}
     * @param target 允许的执行目标
     * @param descProvider 命令描述提供者，可为 {@code null}
     * @param labels 参数标签构建器，可为 {@code null}
     * @return 当前命令服务
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


    /**
     * 单次命令执行上下文。
     */
    interface Ctx {
        /**
         * @return 平台命令发送者
         */
        Object sender();

        /**
         * 获取已经解析的参数。
         *
         * @param name 参数名
         * @param <T> 期望类型
         * @return 参数值；不存在时通常为 {@code null}
         */
        <T> T get(String name);

        /**
         * 获取已经解析的参数或默认值。
         *
         * @param name 参数名
         * @param def 默认值
         * @param <T> 期望类型
         * @return 参数值或默认值
         */
        <T> T getOr(String name, T def);

        /**
         * 获取玩家发送者。
         *
         * <p>平台实现应覆盖此方法并返回其玩家类型；默认实现始终抛出异常。</p>
         *
         * @param err 发送者不是玩家时的错误消息
         * @param <T> 平台玩家类型
         * @return 玩家对象
         */
        default <T> T requirePlayer(String err){ throw new IllegalStateException(err); }

        /**
         * @return 当前命令语言
         */
        Locale locale();
    }

    /**
     * 命令执行器。
     */
    @FunctionalInterface
    interface CommandExecutor {
        /**
         * 执行命令。
         *
         * @param ctx 命令上下文
         * @throws Exception 命令业务执行失败时
         */
        void run(Ctx ctx) throws Exception;
    }

    /**
     * 命令允许的发送者类型。
     */
    enum ExecTarget {
        /** 仅玩家。 */
        PLAYER,
        /** 仅控制台。 */
        CONSOLE,
        /** 玩家或控制台。 */
        ALL
    }

    /**
     * 命令权限要求。
     *
     * @param node 权限节点
     */
    record Permission(String node) {
        /**
         * @param n 权限节点
         * @return 权限要求
         */
        public static Permission perms(String n){ return new Permission(n); }
    }

    /**
     * 命令描述的静态国际化文本。
     *
     * @param i18n locale 到描述文本的映射
     */
    record Desc(Map<String,String> i18n) {
        /**
         * 通过“locale、文本”对构造命令描述。
         *
         * @param kv 交替排列的 locale 与文本
         * @return 命令描述
         */
        public static Desc desc(Object...kv){ var m=new LinkedHashMap<String,String>(); for(int i=0;i+1<kv.length;i+=2)m.put((String)kv[i],(String)kv[i+1]); return new Desc(m);}
    }

    /**
     * 自定义命令参数类型解析器。
     */
    interface TypeResolver {
        /**
         * 判断是否支持参数类型。
         *
         * @param typeId 类型 ID
         * @return 支持时为 {@code true}
         */
        boolean supports(String typeId);

        /**
         * 解析单个命令参数。
         *
         * @param ctx 解析上下文
         * @param token 原始参数文本
         * @return 解析后的值
         * @throws Exception 参数无效或解析失败时
         */
        Object parse(ParseCtx ctx, String token) throws Exception;

        /**
         * 提供参数补全候选。
         *
         * @param ctx 解析上下文
         * @param prefix 当前输入前缀
         * @return 补全候选
         */
        List<String> complete(ParseCtx ctx, String prefix);
    }

    /**
     * 参数解析上下文。
     */
    interface ParseCtx {
        /**
         * @return 此前已经解析的参数
         */
        Map<String,Object> vars();

        /**
         * @return 参数规范中的约束元数据
         */
        Map<String,String> meta();

        /**
         * @return 平台上下文
         */
        Object platform();

        /**
         * @return 平台命令发送者
         */
        Object sender();
    }
}
