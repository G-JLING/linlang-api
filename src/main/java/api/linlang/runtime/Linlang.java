package api.linlang.runtime;

import api.linlang.audit.LinLog;
import api.linlang.command.LinCommand;
import api.linlang.file.database.config.DbConfig;
import api.linlang.file.database.repo.Repository;
import api.linlang.file.database.types.DbType;
import api.linlang.file.LinFile;
import api.linlang.messenger.LinMessenger;

import java.util.function.Function;

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
     * <p>琳琅的信息服务，可以方便地向任何位置（玩家或控制台）发送聊天框、动作栏、标题消息。</p>
     * <p>对于控制台，消息统一降级至聊天框。</p>
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
     * <p>琳琅的审计与日志服务</p>
     *
     * @return 可用的 LinLog 实例
     */
    LinLog linLog();

    /*
      ########################################  服务门面  ########################################
     */

    /**
     * 安全地关闭琳琅服务
     * <p>在软件卸载时调用</p>
     */
    default void close() {
    }

    /**
     * 设置琳琅服务的个性化设置
     * <p>使用后，需要调用 {@link #reload()} 使设置生效</p>
     */
    default Settings settings() {
        if (!(this instanceof Configurable c))
            throw new IllegalStateException("This Linlang is not configurable.");
        return new Settings(this, c);
    }

    final class Settings {

        private final Linlang owner;
        private final Configurable c;

        Settings(Linlang owner, Configurable c) {
            this.owner = owner;
            this.c = c;
        }

        /**
         * 琳琅审计与日志服务的日志方式
         *
         * <p>是否使得琳琅审计（LinLog）归于平台包装的日志通道。如 Bukkit 的 <code>plugin.getLogger()</code>
         *
         * @param v 布尔值
         */
        public Settings pluginLogger(boolean v) {
            c.withPluginLogger(v);
            return this;
        }

        /**
         * 琳琅的本地化
         *
         * @param v 地区代码，遵循 <code>language_REGION</code> 格式，如 <code>zh_CN</code>
         */
        public Settings initialLocale(String v) {
            c.withInitialLanguage(v);
            return this;
        }

        /**
         * 琳琅内建提示消息的前缀
         *
         * <p>设置琳琅内建提示消息的前缀，可便于消息识别。默认情况下，无前缀或提示为 [linlang]</p>
         *
         * <p>与 {@link #dynamicFixedPrefix(Function)} 选其一</p>
         *
         * @param prefix 前缀文本
         */
        public Settings commandPrefix(String prefix) {
            c.withCommandPrefix(prefix);
            return this;
        }

        /**
         * <p>设置琳琅内建提示消息的前缀，可便于消息识别。默认情况下，无前缀或提示为 [linlang]</p>
         * <b>动态前缀</b>
         * <p>与 <code>fixedPrefix()</code> 互斥</p>
         *
         * @param func 函数式接口
         */
        public Settings dynamicFixedPrefix(Function<Object, String> func) {
            c.withCommandPrefixProvider(func);
            return this;
        }

        /**
         * 应用设置
         * <p>在调用 {@link #settings()} 后需调用此方法以使设置生效</p>
         * <p>与 {@link #reload()} 任选其一，此方法主要用于提供链式调用体验</p>
         */
        public Linlang apply() {
            c.reload();
            return owner;
        }
    }

    interface Configurable {
        Configurable withPlatformContext(Object platformContext);

        Configurable withCommandPrefix(String prefix);

        Configurable withCommandPrefixProvider(Function<Object, String> provider);

        Configurable withInitialLanguage(String locale);

        Configurable withPluginLogger(boolean usePluginLogger);

        void reload();
    }

    /**
     * 重新载入琳琅服务
     * <p>在调用 {@link #settings()} 后需调用此方法以使设置生效</p>
     */
    default void reload() {
        if (this instanceof Configurable c) c.reload();
    }
}