package com.huadian.dao;


import com.huadian.bean.SysUsers;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface SysUsersMapper {

    SysUsers selectByPrimaryKey(String id);
    SysUsers selectByLoginIdShowPwd(@Param("loginId") String loginId);
    SysUsers selectByRealNameAndPwd(@Param("realName") String realName, @Param("password") String password);
    int insertUser(Map<String, Object> params);

}