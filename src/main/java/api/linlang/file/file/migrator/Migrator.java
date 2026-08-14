package api.linlang.file.file.migrator;

public interface Migrator {

    int from();

    int to();

    /**
     * 判断迁移器是否适用于指定配置类型
     */
    default boolean supports(Class<?> configType) {
        return true;
    }

    /**
     * 执行文档级迁移
     */
    void migrate(MutableDocument doc);
}
