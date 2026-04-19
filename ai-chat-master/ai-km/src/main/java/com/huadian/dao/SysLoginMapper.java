package com.huadian.dao;


import com.huadian.bean.SysLogin;

public interface SysLoginMapper {
 
    SysLogin selectById(String uuid);
    
    int updateExpTime(SysLogin login);
    
    int deleteById(Integer id);
}