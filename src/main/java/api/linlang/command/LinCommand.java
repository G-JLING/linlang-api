package api.linlang.command;

import api.linlang.file.file.LangText;
import api.linlang.command.group.CommandRoot;

import java.util.*;
import java.util.function.Supplier;

/**
 * Linlang 命令服务。
 *
 * <p>命令由规范字符串、执行器和 {@link CommandOptions} 组成。开发者既可以
 * 使用完整规范逐条注册，也可以通过 {@link #root(String)} 创建可合并的嵌套命令组。</p>
 */
public interface LinCommand {

    /**
     * 注册自定义命令参数类型解析器。
     *
     * <p>解析器既负责把参数文本转换为业务对象，也可以按当前输入动态提供 Tab 补全候选。
     * 实现若不支持扩展参数类型，可以保留默认行为。</p>
     *
     * @param resolver 参数类型解析器
     * @throws UnsupportedOperationException 当前实现不支持自定义解析器时
     */
    default void addResolver(TypeResolver resolver) {
        throw new UnsupportedOperationException("Custom command type resolvers are not supported");
    }

    /**
     * 获取或创建根命令。
     *
     * <p>同一命令服务中名称相同的根命令会合并。通过根命令注册的相对规范与
     * {@link #register(String, CommandExecutor, CommandOptions)} 注册的完整规范
     * 最终进入同一套路由。</p>
     *
     * @param namespace 根命令命名空间
     * @return 根命令
     */
    CommandRoot root(String namespace);

    /**
     * 使用默认选项注册命令。
     *
     * <p>命令默认不要求权限，并允许玩家和控制台执行。</p>
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @return 当前命令服务
     */
    default LinCommand register(String spec, CommandExecutor exec) {
        return register(spec, exec, CommandOptions.options());
    }

    /**
     * 使用统一选项注册命令。
     *
     * @param spec 命令规范字符串
     * @param exec 命令执行器
     * @param options 命令选项
     * @return 当前命令服务
     */
    LinCommand register(String spec, CommandExecutor exec, CommandOptions options);

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
     * @param relative 是否相对于命令组权限前缀
     */
    record Permission(String node, boolean relative) {
        /**
         * 创建绝对权限要求。
         *
         * @param node 权限节点
         */
        public Permission(String node) {
            this(node, false);
        }

        /**
         * @param n 权限节点
         * @return 权限要求
         */
        public static Permission perms(String n){ return new Permission(n, false); }

        /**
         * 创建相对于命令组权限前缀的权限片段。
         *
         * <p>逐条注册没有命令组上下文，此时该片段会作为完整权限节点使用。</p>
         *
         * @param segment 权限片段
         * @return 相对权限要求
         */
        public static Permission segment(String segment) {
            return new Permission(segment, true);
        }
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
         * <p>返回 {@code null} 或空列表时，命令服务会在参数尚未输入时显示参数说明，
         * 没有说明则显示参数名。</p>
         *
         * @param ctx 解析上下文
         * @param prefix 当前输入前缀
         * @return 补全候选，可为 {@code null} 或空列表
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
