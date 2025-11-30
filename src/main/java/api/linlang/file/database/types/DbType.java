package api.linlang.file.database.types;

import api.linlang.file.database.config.DbConfig;

/**
 * 数据库类型
 *
 * <p>使用 {@link api.linlang.file.database.services.DataService#init(DbType, DbConfig)}</p> 来初始化一个新的数据库时，指定改数据库类型
 */
public enum DbType {
    /**
     * H2 数据库
     */
    H2,
    /**
     * MySQL 数据库
     */
    MYSQL
}
