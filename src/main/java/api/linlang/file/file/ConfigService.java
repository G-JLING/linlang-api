package api.linlang.file.file;

import api.linlang.file.file.migrator.Migrator;

import java.util.Collection;

/**
 * 配置文件服务
 */
public interface ConfigService {

    /**
     * 绑定配置文件对象
     * <p>将一个配置文件对象绑定至配置文件服务，并根据传参决定是否生成配置文件</p>
     *
     * @param config 配置文件对象类
     * @param emit   是否为此配置生成配置文件
     */
    <T> T bind(Class<T> config, boolean emit);

    /**
     * 绑定配置文件对象
     * <p>将一个配置文件对象绑定至配置文件服务，且生成配置文件</p>
     *
     * @param config 配置对象类
     */
    <T> T bind(Class<T> config);

    /**
     * 注册一个配置迁移器。
     *
     * <p>迁移器应在绑定对应配置类之前注册。迁移过程必须能从
     * {@link Migrator#from()} 连续推进到配置类声明的目标版本。</p>
     *
     * @param migrator 配置迁移器
     * @return 当前配置服务
     */
    ConfigService registerMigrator(Migrator migrator);

    /**
     * 批量注册配置迁移器。
     *
     * @param migrators 配置迁移器集合
     * @return 当前配置服务
     */
    default ConfigService registerMigrators(Collection<? extends Migrator> migrators) {
        if (migrators != null) {
            for (Migrator migrator : migrators) {
                registerMigrator(migrator);
            }
        }
        return this;
    }

    /**
     * @hidden
     */
    <T> void save(Class<T> type, T config);

    /** 保存所有已绑定的配置文件对象生成的配置文件中的修改
     *
     * <p>在软件卸载或重载前调用该方法以保存修改</p>
     */
    void saveAll();

    /**
     * 重新读取磁盘上所有绑定的文件并应用
     * <p>更新会被应用于最后一次赋值 {@link #bind(Class)} 的字段</p>
     *
     * <p>若文件被代码更改过，应先调用 {@link #saveAll()} 方法使更改落盘</p>
     */
    default void reload() {}
}
