package api.linlang.file;

import api.linlang.file.database.services.DataService;
import api.linlang.file.file.service.ConfigService;
import api.linlang.file.file.service.LangService;

/**
 *  琳琅文件服务，包含了文件、语言和数据库。
 */
public interface LinFile {

    /**
     * 获取文件服务
     * @return 可用的文件服务
     */
    ConfigService config();

    /**
     * 获取语言服务
     * @return 可用的语言服务
     */
    LangService language();

    /**
     * 获取数据库服务
     * @return 可用的数据库服务
     */
    DataService database();

}