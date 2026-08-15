package api.linlang.file.file.migrator;

/**
 * 配置文档版本迁移器。
 *
 * <p>迁移器在配置对象填充之前操作树形文档。应在首次绑定目标配置类之前注册，
 * 并确保迁移过程可重复判断且只处理声明的版本区间。</p>
 */
public interface Migrator {

    /**
     * @return 迁移前版本
     */
    int from();

    /**
     * @return 迁移后版本，必须大于 {@link #from()}
     */
    int to();

    /**
     * 判断迁移器是否适用于指定配置类型。
     *
     * @param configType 配置类
     * @return 适用时为 {@code true}
     */
    default boolean supports(Class<?> configType) {
        return true;
    }

    /**
     * 执行文档级迁移。
     *
     * @param doc 当前版本的可变配置文档
     */
    void migrate(MutableDocument doc);
}
