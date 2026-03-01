package api.linlang.runtime;

import api.linlang.command.LinCommand;
import api.linlang.file.LinFile;
import api.linlang.view.LinInteract;
import api.linlang.messenger.LinMessenger;

import java.util.function.Function;

/**
 * 琳琅主类
 *
 * <p>琳琅动态服务的唯一入口</p>
 */
public interface Linlang {

    /**
     * 运行时版本
     *
     * @hidden
     */
    String runtimeVersion();

    /*
      ########################################  服务门面  ########################################
     */

    /**
     * <p>琳琅的信息服务，可以方便地向任何位置（玩家或控制台）发送聊天框、动作栏、标题消息</p>
     * <p>对于控制台，消息统一降级至聊天框</p>
     *
     * @return 可用的 LinMessenger 实例
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
     * @return 可用的 LinInteract 实例
     */
    LinInteract linInteract();

    /*
      ########################################  服务门面  ########################################
     */

    /**
     * 安全地关闭琳琅服务
     *
     * <p>在软件卸载时调用</p>
     */
    default void close() {}

    /**
     * 设置琳琅服务的个性化设置
     *
     * <p>例如琳琅服务消息前缀等。调用 {@link Settings#apply()} 将热应用修改</p>
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
         * @param func 函数式接口
         */
        public Settings dynamicTotalPrefix(Function<Object, String> func) {
            c.totalPrefixProvider(func);
            return this;
        }

        /**
         * 应用个性化设置
         *
         * <p>调用后将触发一次重载，和 {@link Linlang#reload()} 功能一致，使其链式调用的方式。</p>
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
         */
        public Parameters totalLocale(String v) {
            p.totalLocale(v);
            return this;
        }

        /**
         * 应用运行参数设置
         *
         * <p>调用后将触发一次重建，和 {@link Linlang#restart()} 功能一致，使其链式调用的方式。</p>
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