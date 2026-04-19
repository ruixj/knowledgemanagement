package com.huadian.service;



import com.huadian.bean.SysUsers;

import java.util.Map;

public interface SysLoginService {
	
	Map Login( String loginName, String password);
	
	void exit(String loginId,String userId);

	public Map checkIfTokenValid(String token, String uuid) ;
}


