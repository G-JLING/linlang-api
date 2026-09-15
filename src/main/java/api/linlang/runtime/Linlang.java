package api.linlang.runtime;

import api.linlang.audit.LinAudit;
import api.linlang.audit.LinLog;
import api.linlang.command.LinCommand;
import api.linlang.file.LinFile;
import api.linlang.messenger.LinMessenger;
import api.linlang.view.LinView;

import java.util.function.Function;

/**
 * 单个插件使用的 Linlang 服务门面。
 *
 * <p>通过该门面取得命令、文件、消息和界面服务。门面及其子服务的生命周期通常与插件一致。</p>
 */
public interface Linlang {

    /**
     * 返回 Linlang 运行时实现版本。
     *
     * @return 运行时版本字符串
     * @hidden
     */
    String runtimeVersion();

    /*
      ########################################  服务门面  ########################################
     */

    /**
     * 返回一次性文本消息投递服务。
     *
     * <p>该服务支持聊天、动作栏、标题、高级字符串与语言字段引用。
     * 不受支持的投递通道按照消息声明的降级策略处理。</p>
     *
     * @return 可用的消息服务
     */
    LinMessenger linMessenger();

    /**
     * <p>琳琅的文件服务，包含配置文件、语言文件与数据库（H2 与 MySQL）</p>
     *
     * @return 琳琅文件服务类
     */
    LinFile linFile();

    /**
     * <p>琳琅的命令服务</p>
     *
     * @return 可用的 LinCommand 实例
     */
    LinCommand linCommand();

    /**
     * <p>琳琅的界面服务</p>
     *
     * @return 可用的 LinView 实例
     */
    LinView linView();

    /**
     * 返回当前插件绑定的日志、审计与问题报告入口。
     *
     * <p>该入口已经由运行时完成初始化，其普通日志、审计事件和问题报告
     * 可以分别输出到独立文件。</p>
     *
     * @return 当前插件的统一审计入口
     */
    default LinAudit linAudit() {
        return LinLog.forOwner(getClass());
    }

    /*
      ########################################  服务门面  ########################################
     */

    /**
     * 安全关闭该插件门面持有的服务。
     *
     * <p>应在插件卸载时调用。</p>
     */
    default void close() {}

    /**
     * 统一设置琳琅服务的语言、前缀、日志方式及平台上下文
     *
     * <p>例如琳琅服务消息前缀等。调用 {@link Settings#apply()} 将热应用修改</p>
     *
     * @return 统一设置构建器
     * @throws IllegalStateException 当前运行时不支持动态设置时
     */
    default Settings settings() {
        if (!(this instanceof Configurable c))
            throw new IllegalStateException("This Linlang is not configurable.");
        return new Settings(this, c);
    }

    /**
     * 兼容旧版参数入口。
     *
     * @deprecated 请使用 {@link #settings()} 统一设置。
     *
     * <p>例如平台上下文、全局语言等。调用 {@link Parameters#apply()} 后原地应用，不替换服务</p>
     *
     * @return 运行参数构建器
     * @throws IllegalStateException 当前运行时不支持参数设置时
     */
    @Deprecated
    default Parameters parameters() {
        if (!(this instanceof Parametric p))
            throw new IllegalStateException("This Linlang is not parameterizable.");
        return new Parameters(this, p);
    }

    /**
     * 琳琅服务的统一设置项
     *
     * <p>调用 apply 后原地应用设置，不会重建服务或清空命令注册。</p>
     */
    final class Settings {

        private final Linlang owner;
        private final Configurable c;
        private Boolean pluginLogger;
        private Function<Object, String> prefix;
        private Object platformContext;
        private String locale;

        /**
         * @hidden
         */
        Settings(Linlang owner, Configurable c) {
            this.owner = owner;
            this.c = c;
        }

        /**
         * 设置琳琅审计与日志服务的日志方式
         *
         * <p>是否使得琳琅审计（LinLog）归于平台包装的日志通道。如 Bukkit 的 <code>plugin.getLogger()</code>
         *
     * @param v 布尔值
     * @return 当前设置构建器
         */
        public Settings usingPluginLogger(boolean v) {
            pluginLogger = v;
            return this;
        }

        /**
         * 设置琳琅内建提示消息的前缀
         *
         * <b>琳琅服务的全局参数：前缀名</b>
         *
         * <p>设置琳琅服务（如命令）消息的前缀。默认情况下，前缀为软件注册名</p>
         *
         * <p>与 {@link #dynamicTotalPrefix(Function)} 选其一</p>
         *
     * @param prefix 前缀文本
     * @return 当前设置构建器
         */
        public Settings totalPrefix(String prefix) {
            java.util.Objects.requireNonNull(prefix);
            this.prefix = ignored -> prefix;
            return this;
        }

        /**
         * 设置琳琅内建提示消息的前缀
         *
         * <b>琳琅服务的全局参数：前缀名</b>
         *
         * <p>设置琳琅服务（如命令）消息的前缀。默认情况下，前缀为软件注册名</p>
         *
         * <p>与 {@link #totalPrefix(String)} 选其一</p>
         *
     * @param func 根据当前插件的平台上下文计算前缀的函数
     * @return 当前设置构建器
         */
        public Settings dynamicTotalPrefix(Function<Object, String> func) {
            prefix = java.util.Objects.requireNonNull(func);
            return this;
        }

        /**
         * 设置全局语言，应用后原地更新语言内容。
         *
         * @param locale 地区代码
         * @return 当前设置构建器
         */
        public Settings totalLocale(String locale) {
            if (locale == null || locale.isBlank()) throw new IllegalArgumentException("locale");
            this.locale = locale.trim();
            return this;
        }

        /**
         * 校验平台上下文，不允许更换门面的所属插件。
         *
         * @param context 当前插件的平台对象
         * @return 当前设置构建器
         */
        public Settings platformContext(Object context) {
            this.platformContext = java.util.Objects.requireNonNull(context);
            return this;
        }

        /**
         * 原地应用本次设置
         *
     * <p>只应用本次设置；切换语言时加载目标语言，不重读无关配置，不重建服务。</p>
     *
     * @return 所属 Linlang 门面
         */
        public Linlang apply() {
            if (locale != null || platformContext != null) {
                if (!(owner instanceof Parametric p))
                    throw new UnsupportedOperationException("Runtime does not support live parameter updates");
                if (platformContext != null) p.withPlatformContext(platformContext);
                if (locale != null) p.totalLocale(locale);
                p.applyParameters();
            }
            if (pluginLogger != null) c.usingPluginLogger(pluginLogger);
            if (prefix != null) c.totalPrefixProvider(prefix);
            c.applySettings();
            return owner;
        }
    }

    /**
     * 琳琅服务的运行参数设置项
     *
     * <p>语言参数原地更新，已有服务、命令注册和语言引用继续有效</p>
     */
    final class Parameters {

        private final Linlang owner;
        private final Parametric p;
        private Object platformContext;
        private String locale;

        /**
         * @hidden
         */
        Parameters(Linlang owner, Parametric p) {
            this.owner = owner;
            this.p = p;
        }

        /**
         * 设置平台上下文
         *
     * @param platformContext 平台上下文对象（如 Bukkit 的 JavaPlugin 实例）
     * @return 当前参数构建器
         */
        public Parameters platformContext(Object platformContext) {
            this.platformContext = java.util.Objects.requireNonNull(platformContext);
            return this;
        }

        /**
         * 设置琳琅的启动语言
         *
         * <b>琳琅服务的全局参数：语言</b>
         *
     * @param v 地区代码，遵循 <code>language_REGION</code> 格式，如 <code>zh_CN</code>
     * @return 当前参数构建器
         */
        public Parameters totalLocale(String v) {
            if (v == null || v.isBlank()) throw new IllegalArgumentException("locale");
            locale = v;
            return this;
        }

        /**
         * 应用运行参数设置
         *
     * <p>原地应用运行参数，不重建服务，也不重载无关配置。</p>
     *
     * @return 所属 Linlang 门面
         */
        public Linlang apply() {
            if (platformContext != null) p.withPlatformContext(platformContext);
            if (locale != null) p.totalLocale(locale);
            p.applyParameters();
            return owner;
        }
    }


    /**
     * @hidden
     */
    interface Configurable {

        Configurable totalPrefix(String prefix);

        Configurable totalPrefixProvider(Function<Object, String> provider);

        Configurable usingPluginLogger(boolean usePluginLogger);

        void reload();

        /**
         * 应用设置，不重新读取文件。
         */
        default void applySettings() {}
    }

    /**
     * @hidden
     */
    interface Parametric {

        Parametric withPlatformContext(Object platformContext);

        Parametric totalLocale(String locale);

        void restart();

        /**
         * 原地应用参数；旧运行时必须显式拒绝，不能退回破坏性重建。
         */
        default void applyParameters() {
            throw new UnsupportedOperationException("Runtime does not support live parameter updates");
        }
    }

    /**
     * 注册具名重载回调，同名注册会替换旧回调；传入 null 移除。
     *
     * <p>文件与语言成功更新后、GUI 定义刷新前调用，用于更新业务缓存。
     * 回调不得递归重载，也不应重新注册已有命令。异常会加入重载失败结果。</p>
     *
     * @param name 当前插件内唯一的回调名称
     * @param callback 重载回调，null 表示移除
     * @return 当前门面
     */
    default Linlang onReload(String name, Runnable callback) {
        throw new UnsupportedOperationException("Runtime does not support reload callbacks");
    }

    /**
     * 注册重建后的初始化回调，重复注册替换旧回调，null 表示移除。
     *
     * <p>回调应重新绑定文件、注册命令、安装消息传输器及 GUI Source 和 Hook。
     * 不得再次重载、重建或关闭门面。重建开始后的失败会关闭门面，不能保证回滚。</p>
     *
     * @param callback 新服务的初始化回调
     * @return 当前门面
     */
    default Linlang onRebuild(Runnable callback) {
        throw new UnsupportedOperationException("Runtime does not support rebuild callbacks");
    }

    /**
     * 重新载入琳琅服务
     */
    default void reload() {
        if (this instanceof Configurable c) c.reload();
    }

    /**
     * 重建文件、命令、消息及界面服务，然后执行重建回调。
     *
     * <p>必须先注册重建回调。旧服务和绑定对象不可继续使用，数据库与审计资源保持。</p>
     */
    default void restart() {
        if (this instanceof Parametric p) p.restart();
    }
}
