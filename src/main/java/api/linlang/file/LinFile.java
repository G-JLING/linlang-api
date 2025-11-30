package api.linlang.file;

import api.linlang.file.database.services.DataService;
import api.linlang.file.file.service.ConfigService;
import api.linlang.file.file.service.LangService;

/**
 *  琳琅文件服务，包含了文件、语言和数据库。
 */
public interface LinFile {
    ConfigService config();
    LangService language();
    DataService database();

}