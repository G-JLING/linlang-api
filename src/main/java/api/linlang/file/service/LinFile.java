package api.linlang.file.service;

import api.linlang.file.database.services.DataService;

/**
 *
 */
public interface LinFile {
    ConfigService config();
    LangService language();
    DataService database();

}