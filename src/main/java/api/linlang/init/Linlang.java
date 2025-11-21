package api.linlang.init;

import api.linlang.file.database.config.DbConfig;
import api.linlang.file.database.repo.Repository;
import api.linlang.file.database.types.DbType;
import api.linlang.file.service.Services;

import java.util.function.Function;

/**
 * 平台无关的运行期装配接口。
 * 由各平台的 runtime 在加载时提供实现并注册；第三方仅依赖本接口与 API 坐标。
 */
public interface Linlang extends AutoCloseable {

    /** 建议由实现返回运行时实现版本；用于兼容性日志与故障排查。 */
    String runtimeVersion();

    /** 当前 API 版本（实现可据此做兼容校验）。 */
    String API_VERSION = "1.0.4.5";

    /* ========================= 配置 / 装配 ========================= */

    /**
     * 注入平台上下文（例如：Bukkit 传入 JavaPlugin 实例）。
     * 平台无关，按需使用；可重复调用覆盖。
     */
    Linlang withPlatformContext(Object platformContext);

    /** 固定命令前缀（如 "§f[§dMyPlugin§f]"）。 */
    Linlang withCommandPrefix(String prefix);

    /**
     * 动态命令前缀提供者（根据平台上下文计算）。
     * provider 的入参为 {@code withPlatformContext} 传入的对象。
     */
    Linlang withCommandPrefixProvider(Function<Object, String> provider);

    /** 初始化语言（如 "zh_CN"）。 */
    Linlang withInitialLanguage(String locale);

    /** 是否使用“平台自身的日志记录器”（如 Bukkit 的 Plugin.getLogger()）。 */
    Linlang withPluginLogger(boolean usePluginLogger);

    /** 触发热重载（常用于语言/命令等变更后刷新）。 */
    Linlang reload();

    /* ========================= 数据 / 仓库 ========================= */

    /** 初始化数据源。 */
    void initDb(DbType type, DbConfig cfg);

    /** 获取实体仓库。ID 类型由实现内部推断或使用通用 Object。 */
    <T> Repository<T, ?> repo(Class<T> entityType);

    /* ========================= 服务门面 ========================= */

    /** 访问已装配的 LinFile/LinData/LinLang 等服务门面。 */
    Services services();

    /* ========================= 生命周期 ========================= */

    /** 释放资源（热重载监视器、调度器等）。实现可选择无操作。 */
    @Override
    default void close() {}
}