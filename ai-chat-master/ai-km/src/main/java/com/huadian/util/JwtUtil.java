package com.huadian.util;

import com.huadian.bean.SysUsers;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET_KEY = "hudianzhinengssssssssssssssssssssssssss";

    public static String createToken(SysUsers sysUser) {
        Map<String,Object> header=new HashMap<String,Object>();
        header.put("type", "jwt");
        header.put("alg", "HS256");
        // 生成token
        String jwtToken = Jwts.builder()
                // 头部
                .setHeader(header)
                // 公共申明和私有申明

                .claim("ryid", sysUser.getRyid())
                .claim("loginDate", new Date())
                // 签证
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
        return jwtToken;
    }

    public static Claims getClaimByToken(String jwtToken) {
        Jws<Claims> claimsJws = Jwts.parser()
                // 设置私钥
                .setSigningKey(SECRET_KEY.getBytes())
                // 解析jwt字符串
                .parseClaimsJws(jwtToken);
        // 获取载荷信息
        Claims payload = claimsJws.getBody();
        return payload;
    }
}