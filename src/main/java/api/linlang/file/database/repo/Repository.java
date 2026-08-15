package api.linlang.file.database.repo;

import api.linlang.file.database.dto.Page;
import api.linlang.file.database.dto.QuerySpec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 面向单一实体类型的通用数据仓库。
 *
 * <p>实体字段与数据库列的映射由数据库注解声明。查询、流与分页结果的具体一致性
 * 由运行时实现保证。</p>
 *
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public interface Repository<T, ID> extends AutoCloseable {

    /**
     * 新增或更新实体。
     *
     * @param e 待保存实体
     * @return 保存后的实体
     */
    T save(T e);

    /**
     * 根据主键删除实体。
     *
     * @param id 主键
     */
    void deleteById(ID id);

    /**
     * 根据主键查找实体。
     *
     * @param id 主键
     * @return 实体；不存在时为空
     */
    Optional<T> findById(ID id);

    /**
     * 查询全部实体。
     *
     * @return 查询结果
     */
    List<T> findAll();

    /**
     * 按查询条件返回分页结果。
     *
     * @param spec 查询条件
     * @return 分页结果
     */
    Page<T> query(QuerySpec spec);

    /**
     * @return 当前实体表中的记录数
     */
    long count();

    /**
     * 判断指定主键是否存在。
     *
     * @param id 主键
     * @return 存在时为 {@code true}
     */
    boolean existsById(ID id);

    /**
     * 删除全部实体。
     */
    void deleteAll();

    /**
     * 批量保存实体。
     *
     * @param entities 待保存实体集合
     */
    void saveAll(java.util.Collection<T> entities);

    /**
     * 以流形式遍历全部实体。
     *
     * <p>调用方应关闭返回的流，以便实现释放数据库资源。</p>
     *
     * @return 实体流
     */
    java.util.stream.Stream<T> streamAll();

    /**
     * 查询指定列等于给定值的第一条实体。
     *
     * @param column 实体字段名或实现支持的列名
     * @param value 比较值
     * @return 首条匹配实体；不存在时为空
     */
    default Optional<T> findOneWhere(String column, Object value) {
        Objects.requireNonNull(column, "column");
        return findAllWhere(column + " = ?", value).stream().findFirst();
    }

    /**
     * 将当前仓库中尚未提交的修改写入数据库。
     */
    default void flush() { }

    /**
     * 关闭仓库并释放实现持有的资源。
     */
    @Override
    default void close() {  }

    /**
     * 按条件表达式查询实体。
     *
     * <p>默认实现仅支持单个“字段 = ?”条件，并在内存中筛选 {@link #findAll()} 的结果；
     * 数据库运行时应覆盖此方法以执行原生参数化查询。无法识别条件时，默认实现返回全部实体。</p>
     *
     * @param where 条件表达式
     * @param params 参数值
     * @return 匹配实体列表
     */
    default List<T> findAllWhere(String where, Object... params) {
        if (where != null && where.contains("=") && params != null && params.length == 1) {
            String field = where.split("=")[0].trim();
            Object expected = params[0];
            List<T> all = findAll();
            List<T> out = new ArrayList<>();
            for (T e : all) {
                try {
                    Field f = e.getClass().getDeclaredField(field);
                    f.setAccessible(true);
                    Object val = f.get(e);
                    if (Objects.equals(val, expected)) out.add(e);
                } catch (Exception ignore) {}
            }
            return out;
        }
        return findAll();
    }
}
