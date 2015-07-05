package com.script.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.helpinput.annotation.Mapping;
import com.helpinput.annotation.MappingExclude;
import com.helpinput.core.LoggerBase;

/**
 * 动态拦截器 
 * dyanmic interceptor
 */
@Mapping({ "/teacher/*" })
@MappingExclude("/login")
public class TeacherIntercepptor implements HandlerInterceptor {
	static Logger logger = LoggerBase.logger;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String url = request.getRequestURL().toString();
		logger.info("teacherIntercepptor preHandle........xxxxxxxxxxxxxx...................." + url);
		return true;
	}
	
	//在业务处理器处理请求执行完成后,生成视图之前执行的动作   
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
							ModelAndView modelAndView) throws Exception {
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
	
	
}
