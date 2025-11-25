package api.linlang.file.service;

import api.linlang.file.database.services.DataService;

/**
 *  琳琅文件服务，包含了文件、语言和数据库。
 */
public interface LinFile {
    ConfigService config();
    LangService language();
    DataService database();

}