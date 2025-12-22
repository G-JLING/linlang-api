package api.linlang.file.file;

import java.util.List;

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
    default void reload() {};
}