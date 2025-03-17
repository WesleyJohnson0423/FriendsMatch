
package com.yanpeng.usercenterback.interceptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.yanpeng.usercenterback.common.ErrorCode;
import com.yanpeng.usercenterback.exception.BusinessException;
import com.yanpeng.usercenterback.model.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static com.yanpeng.usercenterback.constant.UserConstant.USER_LOGIN_STATE;


/**
 * <p>使用拦截器设置cookie的HttpOnly.</p>
 * <p>是为了防止XSS攻击,窃取cookie的内容.</p>
 * @author chenkangjing
 * @time 2017.5.24
 */
public class MyInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(MyInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        Object Currentuser = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) Currentuser;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN,"未登录");
        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {


    }


    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object arg2, Exception Exception)
            throws Exception {
    }
}
