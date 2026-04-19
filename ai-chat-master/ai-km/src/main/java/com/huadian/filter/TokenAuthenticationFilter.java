package com.huadian.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huadian.bean.SysUsers;
import com.huadian.context.UserContext;
import com.huadian.service.SysLoginService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);


    @Autowired
    private SysLoginService sysLoginService;

    // 不需要Token验证的路径
    private static final List<String> EXCLUDE_URLS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/swagger-ui",
            "/swagger-ui.html",
            "/swagger-resources",
            "/v3/api-docs",
            "/webjars",
            "/actuator/health"
    );

    public TokenAuthenticationFilter( ) {

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 从请求中提取token
            String token = extractToken(request);
            String ryid = request.getHeader("ryid");
            if (token == null) {
                logger.warn("Token missing for request: {}", request.getRequestURI());
                handleUnauthorized(response, "Token is required");
                return;
            }

            // 验证token
            Map sysUser = sysLoginService.checkIfTokenValid(token, ryid);
            if (sysUser == null) {
                logger.warn("Invalid token for request: {}", request.getRequestURI());
                handleUnauthorized(response, "Invalid or expired token");
                return;
            }
            UserContext.setUser(sysUser);

            logger.debug("Token validation successful for user: {}", sysUser.get("username"));
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage(), e);
            handleUnauthorized(response, "Token validation failed");
        }
        finally {
            UserContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        // 移除contextPath
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        // 检查是否在排除列表中
        String finalPath = path;
        return EXCLUDE_URLS.stream()
                .anyMatch(exclude -> {
                    boolean b = finalPath.startsWith(exclude);
                    return b;
                });
    }

    private String extractToken(HttpServletRequest request) {
        // 1. 从Authorization头获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

//        // 2. 从查询参数获取（可选）
//        String tokenParam = request.getParameter("token");
//        if (StringUtils.hasText(tokenParam)) {
//            return tokenParam;
//        }
//
//        // 3. 从Cookie获取（可选）
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("token".equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }

        return null;
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
       // errorResponse.put("path", ((HttpServletResponse) response).getRequestURI());

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}