package com.huadian.controller;

import com.huadian.exception.BaseException2;
import com.huadian.messages.ErrorConstant;
import com.huadian.service.SysLoginService;
import com.huadian.util.ErrorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    SysLoginService loginService;
    @PostMapping("/login")
    public ModelMap login(@RequestBody Map map) {
        try {
            String userName = (String) map.get("username");
            String password = (String) map.get("password");
            Map login = loginService.Login(userName, password);
            ModelMap ret = ErrorUtil.retErrMsg(ErrorConstant.SUCCESS, login);
            return ret;
        } catch (RuntimeException e) {
            throw new BaseException2(ErrorConstant.USER_OR_PWD_ERR);
        }
    }
}

