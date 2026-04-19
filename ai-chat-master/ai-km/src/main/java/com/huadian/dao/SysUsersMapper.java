package com.huadian.dao;


import com.huadian.bean.SysUsers;
import org.apache.ibatis.annotations.Param;

public interface SysUsersMapper {

    SysUsers selectByPrimaryKey(String id);
    SysUsers selectByLoginIdShowPwd(@Param("loginId") String loginId);


}