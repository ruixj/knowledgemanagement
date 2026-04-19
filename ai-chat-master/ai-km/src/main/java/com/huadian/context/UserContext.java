package com.huadian.context;

import com.huadian.bean.SysUsers;

import java.util.Map;

public class UserContext {
    private static final ThreadLocal<Map> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(Map user) {
        USER_HOLDER.set(user);
    }

    public static Map getUser() {
        return USER_HOLDER.get();
    }

    public static String getUserId() {
        Map user = getUser();
        return user != null ? user.get("ryid").toString() : null;
    }

    public static String getUsername() {
        Map user = getUser();
        return user != null ? user.get("userName").toString() : null;
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
