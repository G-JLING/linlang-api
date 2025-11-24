package api.linlang.common;

import api.linlang.command.LinCommand;
import api.linlang.file.database.config.DbConfig;
import api.linlang.file.database.repo.Repository;
import api.linlang.file.database.types.DbType;
import api.linlang.file.service.LinFile;
import api.linlang.message.Messenger;

import java.util.function.Function;
public interface Linlang extends AutoCloseable {
    String runtimeVersion();

    // 数据 / 仓库
    void initDb(DbType type, DbConfig cfg);
    <T> Repository<T, ?> repo(Class<T> entityType);

    // 服务门面
    Messenger messenger();
    LinFile linFile();
    LinCommand linCommand();

    @Override
    default void close() {}


    // 配置
    interface Configurable {
        Configurable withPlatformContext(Object platformContext);
        Configurable withCommandPrefix(String prefix);
        Configurable withCommandPrefixProvider(Function<Object, String> provider);
        Configurable withInitialLanguage(String locale);
        Configurable withPluginLogger(boolean usePluginLogger);
        void        reload();
    }
}