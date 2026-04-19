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
			SysUsers sysUser = sysUsersMapper.selectByRealNameAndPwd(loginName, password);
			if( sysUser !=null ){
				if( true ){   // password already verified in SQL query
					String token= JwtUtil.createToken(sysUser);

					String uuid = UUID.randomUUID().toString();
					String key = redisKeyPrefix.getLoginsKeyPrefix() + sysUser.getRyid();

					Map result = new HashMap();
					result.put("uuid",uuid);
					result.put("token",token);
					result.put("username",sysUser.getUserName());
					result.put("ryid",sysUser.getRyid());

					redisTemplate.opsForValue().set(key,result,  Duration.ofHours(10));

					return result;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void register(String username, String password) {
		Map<String, Object> params = new HashMap<>();
		params.put("ryid", UUID.randomUUID().toString());
		params.put("real_name", username);
		params.put("password", password);
		sysUsersMapper.insertUser(params);
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
