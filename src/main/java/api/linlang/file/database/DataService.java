package api.linlang.file.database;

import api.linlang.file.database.repo.Repository;

import java.util.Objects;

/**
 * 数据库服务门面，负责连接初始化、实体仓库管理与结构迁移。
 *
 * <p>服务实例由 Linlang 运行时提供，不通过 {@link java.util.ServiceLoader} 发现。
 * 一个典型生命周期如下：</p>
 * <ol>
 *   <li>使用 {@link #init(DbType, DbConfig)} 初始化连接池；</li>
 *   <li>使用 {@link #repo(Class)} 获取实体仓库；</li>
 *   <li>按需使用 {@link #migrate()} 同步数据库结构；</li>
 *   <li>在插件关闭时调用 {@link #close()}。</li>
 * </ol>
 */
public interface DataService extends AutoCloseable {

    /**
     * 根据数据库类型与连接配置初始化服务。
     *
     * @param type 数据库类型
     * @param cfg 连接配置
     * @throws IllegalStateException 服务已经初始化或运行时无法建立连接时
     */
    void init(DbType type, DbConfig cfg);

    /**
     * 获取绑定到指定实体类型的仓库。
     *
     * <p>实体必须声明 {@code @Table}，并且只能包含一个 {@code @Id} 字段。
     * 自动生成的主键仅支持 {@code int} 与 {@code long}。实体需要提供无参数构造方法，
     * 该构造方法可以不是公开的。</p>
     *
     * @param entityType 实体类
     * @param <T> 实体类型
     * @param <ID> 主键类型
     * @return 对应实体的仓库
     */
    <T, ID> Repository<T, ID> repo(Class<T> entityType);

    /**
     * 执行实现支持的数据库结构迁移。
     *
     * <p>运行时采用非破坏性的增量迁移：创建缺失的数据表、列、主键与索引，
     * 并应用新声明的默认值和表注释。迁移不会自动删除列，也不会缩窄或改写已有列类型。</p>
     */
    void migrate();

    /**
     * 将所有仓库中尚未提交的修改写入底层数据库。
     */
    void flushAll();

    /**
     * 将指定实体仓库中尚未提交的修改写入底层数据库。
     *
     * @param entityType 实体类
     * @param <T> 实体类型
     */
    <T> void flushOf(Class<T> entityType);

    /**
     * 关闭数据库服务。
     *
     * <p>默认实现仅调用 {@link #flushAll()}；具体运行时可以覆盖此方法以释放连接池。</p>
     */
    @Override
    default void close() { flushAll(); }

    /**
     * 运行时内建支持的数据库类型。
     */
    enum DbType {
        /**
         * H2 数据库
         */
        H2,
        /**
         * MySQL 数据库
         */
        MYSQL
    }

    /**
     * 数据库连接配置。
     *
     * <p>密码允许为 {@code null}；URL 不得为空，连接池大小必须大于零。</p>
     */
    class DbConfig {
        private final String url;
        private final String user;
        private final String pass;
        private final int poolSize;

        /**
         * 创建数据库连接配置。
         *
         * @param url JDBC URL
         * @param user 数据库用户名，可为 {@code null}
         * @param pass 数据库密码，可为 {@code null}
         * @param poolSize 连接池最大连接数
         */
        public DbConfig(String url, String user, String pass, int poolSize){
            this.url = Objects.requireNonNull(url, "url");
            if (url.isBlank()) throw new IllegalArgumentException("url must not be blank");
            if (poolSize <= 0) throw new IllegalArgumentException("poolSize must be positive");
            this.user = user;
            this.pass = pass;
            this.poolSize = poolSize;
        }

        /**
         * @return JDBC URL
         */
        public String url(){ return url; }

        /**
         * @return 数据库用户名，可能为 {@code null}
         */
        public String user(){ return user; }

        /**
         * @return 数据库密码，可能为 {@code null}
         */
        public String pass(){ return pass; }

        /**
         * @return 连接池最大连接数
         */
        public int poolSize(){ return poolSize; }

        /**
         * 创建数据库连接配置。
         *
         * @param url JDBC URL
         * @param user 数据库用户名，可为 {@code null}
         * @param pass 数据库密码，可为 {@code null}
         * @param poolSize 连接池最大连接数
         * @return 新的连接配置
         */
        public static DbConfig of(String url, String user, String pass, int poolSize){
            return new DbConfig(url, user, pass, poolSize);
        }
    }
}
