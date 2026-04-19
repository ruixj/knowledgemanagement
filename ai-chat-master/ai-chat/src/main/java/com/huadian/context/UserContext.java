package com.huadian.context;

import com.huadian.bean.SysUsers;

public class UserContext {
    private static final ThreadLocal<SysUsers> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(SysUsers user) {
        USER_HOLDER.set(user);
    }

    public static SysUsers getUser() {
        return USER_HOLDER.get();
    }

    public static String getUserId() {
        SysUsers user = getUser();
        return user != null ? user.getRyid() : null;
    }

    public static String getUsername() {
        SysUsers user = getUser();
        return user != null ? user.getUserName() : null;
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
