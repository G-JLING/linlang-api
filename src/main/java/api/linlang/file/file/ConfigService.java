package api.linlang.file.file;

import api.linlang.file.file.migrator.Migrator;

import java.util.Collection;

/**
 * 将带有配置注解的 Java 对象绑定到 YAML 或 JSON 文件。
 *
 * <p>同一服务重复绑定同一配置类时，运行时返回并刷新其活动对象。调用方可以直接修改
 * 活动对象的公开字段，并通过 {@link #saveAll()} 写回磁盘。</p>
 */
public interface ConfigService {

    /**
     * 解析独立的配置文本并连接当前插件的语言服务。
     *
     * @param source 字面字符串、单行语言引用或引用映射
     * @return 按需解析的配置文本
     */
    default api.linlang.file.file.config.ConfigText text(Object source) {
        throw new UnsupportedOperationException("Config text is not supported by this runtime");
    }

    /**
     * 解析独立的配置文本列表并连接当前插件的语言服务。
     *
     * @param source 字符串列表、单行语言引用或引用映射
     * @return 按需解析的配置文本列表
     */
    default api.linlang.file.file.config.ConfigList textList(Object source) {
        throw new UnsupportedOperationException("Config lists are not supported by this runtime");
    }

    /**
     * 绑定配置类，并根据参数决定是否允许生成和写回文件。
     *
     * <p>{@code emit} 为 {@code false} 时仍会读取磁盘并填充对象，但不会创建、补齐或保存文件。</p>
     *
     * @param config 配置类
     * @param emit 是否允许生成和写回文件
     * @param <T> 配置对象类型
     * @return 由服务管理的活动配置对象
     * @throws api.linlang.file.file.config.ConfigLoadException 文件加载或校验失败
     */
    <T> T bind(Class<T> config, boolean emit);

    /**
     * 绑定配置类，并允许生成和写回文件。
     *
     * @param config 配置对象类
     * @param <T> 配置对象类型
     * @return 由服务管理的活动配置对象
     * @throws api.linlang.file.file.config.ConfigLoadException 文件加载或校验失败
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
     * 将指定配置对象保存到其注解声明的文件。
     *
     * @param type 配置类
     * @param config 待保存对象
     * @param <T> 配置对象类型
     * @hidden
     */
    <T> void save(Class<T> type, T config);

    /**
     * 保存所有已绑定且允许写回的配置对象。
     *
     * <p>应在插件关闭或主动重载前调用，以保留内存中的修改。</p>
     */
    void saveAll();

    /**
     * 重新读取所有已绑定配置的磁盘文件，并原地更新活动对象。
     *
     * <p>尚未保存的内存修改会被有效的磁盘内容覆盖。失败文件不改变活动对象，
     * 其余文件仍会继续加载；这不是跨文件的原子事务。</p>
     *
     * @throws api.linlang.file.file.config.ConfigLoadException 至少一个文件加载失败
     */
    default void reload() {}
}
