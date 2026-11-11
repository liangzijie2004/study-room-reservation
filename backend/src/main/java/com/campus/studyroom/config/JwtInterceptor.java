package com.campus.studyroom.config;

import com.campus.studyroom.annotation.AdminOnly;
import com.campus.studyroom.common.BusinessException;
import com.campus.studyroom.common.Constants;
import com.campus.studyroom.util.JwtUtil;
import com.campus.studyroom.util.LoginUser;
import com.campus.studyroom.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录鉴权拦截器：解析 JWT，写入 UserContext，并处理 @AdminOnly 权限
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行非 Controller 方法（如静态资源、错误页）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        LoginUser loginUser = jwtUtil.parse(auth.substring(7));

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        boolean adminOnly = handlerMethod.getMethodAnnotation(AdminOnly.class) != null
                || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), AdminOnly.class);
        if (adminOnly && !Constants.ROLE_ADMIN.equals(loginUser.getRole())) {
            throw new BusinessException(403, "无权限访问该接口");
        }
        UserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}