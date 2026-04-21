package com.huadian.dao;


import java.util.List;
import java.util.Map;

public interface SysKMMapper {

    int createKM(Map<String,Object> params);

    List<Map<String,Object>> loadKMByPage(Map map);

    long loadKMByPageCount(Map map);

    int saveFile(Map<String,Object> params);

    List<Map<String,Object>> loadFilesByKB(Map<String,Object> params);

    long loadFilesByKBCount(Map<String,Object> params);

    int deleteFileById(Map<String,Object> params);

    int parseFile(Map<String,Object> params);

    int updateParseResult(Map<String,Object> params);

    int toggleFileEnabled(Map<String,Object> params);
}