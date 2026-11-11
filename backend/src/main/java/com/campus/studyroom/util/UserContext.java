package com.campus.studyroom.util;

import com.campus.studyroom.common.BusinessException;

/**
 * 当前请求用户上下文（基于 ThreadLocal）
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return user;
    }

    public static Long getUserId() {
        return get().getId();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(get().getRole());
    }

    public static void clear() {
        HOLDER.remove();
    }
}