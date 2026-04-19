package com.huadian.service;

import com.huadian.bean.SysLogin;
import com.huadian.bean.SysUsers;
import com.huadian.dao.SysLoginMapper;
import com.huadian.dao.SysUsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TokenService {
    @Autowired
    SysLoginMapper sysLoginMapper;
    @Autowired
    SysUsersMapper sysUserMapper;
    public SysUsers checkIfTokenValid(String token,String uuid) {

        if(token != null
                &&!("".equals(token))
                &&!("undefined".equals(token))
        )
        {
            if(uuid != null
                    && !("".equals(uuid))
                    && !("undefined".equals(uuid))
            )
            {
                SysLogin login = sysLoginMapper.selectById(uuid);
                if(login != null){
                    SysUsers user = sysUserMapper.selectByPrimaryKey(login.getLoginId());
                    if( user != null){
                        //如果没有记录为没登录
                        if(login != null){
                            //判断token是否相同
                            if(token.equals(login.getToken())) {
                                //过期时间小于当前时间登陆过期new Date().getTime()
                                if(login.getExptime().getTime() > new Date().getTime()){
                                    //重新初始化登陆过期时间30*60*1000
                                    login.setExptime(new Date(new Date().getTime()+(60*60*1000)));
                                    login.setSource("PC");
                                    sysLoginMapper.updateExpTime(login);

                                    return user;
                                }
                                sysLoginMapper.deleteById(login.getId());

                                return null;
                            }
                        }
                    }
                }

            }
        }
        return null ;
    }


}
