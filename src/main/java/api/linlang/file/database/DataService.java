package api.linlang.file.database;

import api.linlang.file.database.repo.Repository;

import java.util.Objects;

/**
 * Database service facade used by plugins and higher-level modules.
 * <p>
 * This API no longer uses ServiceLoader/SPI discovery. Create the concrete
 * implementation directly in your runtime module (e.g. core) and pass it
 * around via dependency injection.
 * <p>
 * Typical lifecycle:
 * <ol>
 *   <li>Construct implementation</li>
 *   <li>{@link #init(DbType, DbConfig)} to open connections</li>
 *   <li>Obtain repositories via {@link #repo(Class)}</li>
 *   <li>Optionally {@link #migrate()} schemas</li>
 *   <li>Call {@link #close()} on shutdown</li>
 * </ol>
 */
public interface DataService extends AutoCloseable {

    /**
     * Initialize connection pool / driver by database type and config.
     */
    void init(DbType type, DbConfig cfg);

    /**
     * Obtain a repository for the annotated entity type.
     * @param entityType entity class
     * @return repository bound to that entity
     */
    <T, ID> Repository<T, ID> repo(Class<T> entityType);

    /**
     * Perform pending schema migrations if supported by the implementation.
     * No-op if unsupported.
     */
    void migrate();

    /** Flush all pending changes to the underlying store. */
    void flushAll();

    /** Flush the repository of a specific entity type. */
    <T> void flushOf(Class<T> entityType);

    /**
     * Close the service. Default behaviour flushes all repositories.
     */
    @Override
    default void close() { flushAll(); }

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

    class DbConfig {
        private final String url;
        private final String user;
        private final String pass;
        private final int poolSize;

        public DbConfig(String url, String user, String pass, int poolSize){
            this.url = Objects.requireNonNull(url, "url");
            if (url.isBlank()) throw new IllegalArgumentException("url must not be blank");
            if (poolSize <= 0) throw new IllegalArgumentException("poolSize must be positive");
            this.user = user;
            this.pass = pass;
            this.poolSize = poolSize;
        }

        public String url(){ return url; }
        public String user(){ return user; }
        public String pass(){ return pass; }
        public int poolSize(){ return poolSize; }

        /** 便捷构造。 */
        public static DbConfig of(String url, String user, String pass, int poolSize){
            return new DbConfig(url, user, pass, poolSize);
        }
    }
}
