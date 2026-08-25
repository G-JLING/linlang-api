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
     * 设置琳琅服务的个性化设置
     *
     * <p>例如琳琅服务消息前缀等。调用 {@link Settings#apply()} 将热应用修改</p>
     *
     * @return 个性化设置构建器
     * @throws IllegalStateException 当前运行时不支持动态设置时
     */
    default Settings settings() {
        if (!(this instanceof Configurable c))
            throw new IllegalStateException("This Linlang is not configurable.");
        return new Settings(this, c);
    }

    /**
     * 更改琳琅服务的系统参数
     *
     * <p>例如平台上下文、启动语言等。调用 {@link Parameters#apply()} 后将触发一次重建</p>
     *
     * @return 运行参数构建器
     * @throws IllegalStateException 当前运行时不支持参数重建时
     */
    default Parameters parameters() {
        if (!(this instanceof Parametric p))
            throw new IllegalStateException("This Linlang is not parameterizable.");
        return new Parameters(this, p);
    }

    /**
     * 琳琅服务的「个性化」设置项
     *
     * <p>您可以不进行个性化设置，不会影响琳琅工作。但可能会使得一些服务的表现不像您想得那样</p>
     */
    final class Settings {

        private final Linlang owner;
        private final Configurable c;

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
            c.usingPluginLogger(v);
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
            c.totalPrefix(prefix);
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
     * @param func 根据消息接收者计算前缀的函数
     * @return 当前设置构建器
         */
        public Settings dynamicTotalPrefix(Function<Object, String> func) {
            c.totalPrefixProvider(func);
            return this;
        }

        /**
         * 应用个性化设置
         *
     * <p>调用后将触发一次重载，和 {@link Linlang#reload()} 功能一致，使其链式调用的方式。</p>
     *
     * @return 所属 Linlang 门面
         */
        public Linlang apply() {
            c.reload();
            return owner;
        }
    }

    /**
     * 琳琅服务的「运行参数」设置项（需要重建琳琅）
     *
     * <p>这些设置的应用会导致整个琳琅服务实例被重新构建，一些正在进行的任务可能被中断</p>
     */
    final class Parameters {

        private final Linlang owner;
        private final Parametric p;

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
            p.withPlatformContext(platformContext);
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
            p.totalLocale(v);
            return this;
        }

        /**
         * 应用运行参数设置
         *
     * <p>调用后将触发一次重建，和 {@link Linlang#restart()} 功能一致，使其链式调用的方式。</p>
     *
     * @return 所属 Linlang 门面
         */
        public Linlang apply() {
            p.restart();
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
    }

    /**
     * @hidden
     */
    interface Parametric {

        Parametric withPlatformContext(Object platformContext);

        Parametric totalLocale(String locale);

        void restart();
    }

    /**
     * 重新载入琳琅服务
     */
    default void reload() {
        if (this instanceof Configurable c) c.reload();
    }

    /**
     * 重建琳琅服务
     *
     * <p>这将导致整个琳琅服务被重新构建，一些正在进行中的任务可能被中断。</p>
     */
    default void restart() {
        if (this instanceof Parametric p) p.restart();
    }
}
