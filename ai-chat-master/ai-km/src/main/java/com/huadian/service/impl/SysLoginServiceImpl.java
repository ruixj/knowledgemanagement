package com.huadian.service.impl;



import com.huadian.bean.SysUsers;

import com.huadian.config.RedisConfig;


import com.huadian.dao.SysUsersMapper;
import com.huadian.service.SysLoginService;
import com.huadian.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class SysLoginServiceImpl implements SysLoginService {
	@Autowired
	SysUsersMapper sysUsersMapper;


	@Qualifier("redisTemplateObj")

	private final RedisTemplate<String, Object> redisTemplate;

	private final RedisConfig.RedisKeyPrefix redisKeyPrefix;

    public SysLoginServiceImpl(RedisTemplate<String, Object> redisTemplate, RedisConfig.RedisKeyPrefix redisKeyPrefix) {
        this.redisTemplate = redisTemplate;
        this.redisKeyPrefix = redisKeyPrefix;
    }

//	@Autowired
//	UtilMapper utilMapper;

	public Map Login( String loginName, String password) {
		try {
			SysUsers sysUser = sysUsersMapper.selectByLoginIdShowPwd(loginName);
			if( sysUser !=null ){
				if( sysUser.getPassword().equals(password) ){
					String token= JwtUtil.createToken(sysUser);

					//SysLogin sysLogin=new SysLogin();
					//sysLogin.setLoginId(sysUser.getId());
					//sysLogin.setToken(token);
					//sysLogin.setExptime(new Date(new Date().getTime()+(60*60*1000)));
					String uuid = UUID.randomUUID().toString();
					//sysLogin.setUuid(uuid);
					//sysLoginMapper.insert(sysLogin);
					String key = redisKeyPrefix.getLoginsKeyPrefix() + sysUser.getRyid(); //uuid;
					//Date date = new Date(new Date().getTime() + (60 * 60 * 1000));

					Map result = new HashMap();
					result.put("uuid",uuid);
					result.put("token",token);
					result.put("username",sysUser.getUserName());
					result.put("ryid",sysUser.getRyid());

					redisTemplate.opsForValue().set(key,result,  Duration.ofHours(10));

					//systemLogService.addLog("用户登录", "登陆系统", sysLoginUser.getId());
					return result;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/*
	 * 退出
	 * */
	@Override
	public void exit(String loginId,String userId) {
		String key = redisKeyPrefix.getLoginsKeyPrefix() + loginId;
		redisTemplate.delete(key);
		//sysLoginMapper.deleteById(uuid);
		//systemLogService.addLog("用户退出", "退出系统", userId);
	}

	public Map checkIfTokenValid(String token,String loginId) {
		String key = redisKeyPrefix.getLoginsKeyPrefix() + loginId;
		Map loginInfo = (Map) redisTemplate.opsForValue().get(key);
		if(token != null
				&&!("".equals(token))
				&&!("undefined".equals(token))
				&& loginInfo != null
		) {
			if(loginId != null
					&&!("".equals(loginId))
					&&!("undefined".equals(loginId))
			){

				String tokenInCache  = (String) loginInfo.get("token");
				if (token.equals(tokenInCache)){
					redisTemplate.opsForValue().set(key,loginInfo,  Duration.ofHours(10));
					return loginInfo;
				}
				else{
					redisTemplate.delete(key);
					return null;
				}
			}
		}
		return null ;
	}


	
}
