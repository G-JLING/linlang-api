package api.linlang.runtime;

import api.linlang.command.LinCommand;
import api.linlang.file.database.config.DbConfig;
import api.linlang.file.database.repo.Repository;
import api.linlang.file.database.types.DbType;
import api.linlang.file.LinFile;
import api.linlang.messenger.LinMessenger;

import java.util.function.Function;

public interface Linlang {
    String runtimeVersion();

    // 数据 / 仓库
    void initDb(DbType type, DbConfig cfg);
    <T> Repository<T, ?> repo(Class<T> entityType);

    /*
      ########################################  服务门面  ########################################
     */

    /**
     * <p>琳琅的信息服务，可以方便地向任何位置（玩家或控制台）发送聊天框、动作栏、标题消息。</p>
     * <p>对于控制台，消息统一降级至聊天框。</p>
     *
     * @return 可用的 messenger 实例
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
     * @return 可用的 linCommand 实例
     */
    LinCommand linCommand();

    /*
      ########################################  服务门面  ########################################
     */

    default void close() {}

    default Settings settings() {
        if (!(this instanceof Configurable c))
            throw new IllegalStateException("This Linlang is not configurable.");
        return new Settings(this, c);
    }

    final class Settings {
        private final Linlang owner;
        private final Configurable c;
        Settings(Linlang owner, Configurable c){ this.owner=owner; this.c=c; }

        public Settings pluginLogger(boolean v){ c.withPluginLogger(v); return this; }
        public Settings initialLocale(String v){ c.withInitialLanguage(v); return this; }
        public Settings commandPrefix(String v){ c.withCommandPrefix(v); return this; }
        public Settings commandPrefixProvider(Function<Object,String> f){
            c.withCommandPrefixProvider(f); return this;
        }

        /** 应用所有设置并重建相关组件（命令路由、语言绑定等）。 */
        public Linlang apply(){ c.reload(); return owner; }
    }

    interface Configurable {
        Configurable withPlatformContext(Object platformContext);
        Configurable withCommandPrefix(String prefix);
        Configurable withCommandPrefixProvider(Function<Object, String> provider);
        Configurable withInitialLanguage(String locale);
        Configurable withPluginLogger(boolean usePluginLogger);
        void reload();
    }

    default void reload() {
        if (this instanceof Configurable c) c.reload();
    }
}