package com.huadian.dao;


import java.util.List;
import java.util.Map;

public interface SysKMMapper {

    int createKM(Map<String,Object> params);

    List<Map<String,Object>> loadKMByPage(Map map);

    long loadKMByPageCount(Map map);
}