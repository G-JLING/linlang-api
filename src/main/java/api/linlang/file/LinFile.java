package api.linlang.file;

import api.linlang.file.database.DataService;
import api.linlang.file.file.ConfigService;
import api.linlang.file.file.LangService;

/**
 * Linlang 文件服务入口，统一提供配置、语言与数据库服务。
 */
public interface LinFile {

    /**
     * 获取配置文件服务。
     *
     * @return 当前插件的配置文件服务
     */
    ConfigService config();

    /**
     * 获取语言文件服务。
     *
     * @return 当前插件的语言文件服务
     */
    LangService language();

    /**
     * 获取数据库服务。
     *
     * @return 当前插件的数据库服务
     */
    DataService database();

}
