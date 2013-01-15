package com.thousandeyes.minitwitter.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.thousandeyes.minitwitter.dtos.entities.User;
import com.thousandeyes.minitwitter.repositories.DataService;


/**
 * @author Miguel
 *
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	 
	@Autowired
	DataService dserv;
	
	/*
	 * Called before the actual handler will be executed(non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public boolean preHandle(HttpServletRequest request, 
		HttpServletResponse response, Object handler)
	    throws Exception {

		logger.info("Authenticating user key");
		
		String key = request.getParameter("key");
		if(key==null) 
			throw new IllegalAccessException("Unauthorized access!");
		User user = this.dserv.getAuthenticatedUser(key);
		user.setKey(key);
		UserContextTLS.instance.set(user);

		return true;
	}
 
	/*
	 * Called after the handler is executed(non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	public void postHandle(
		HttpServletRequest request, HttpServletResponse response, 
		Object handler, ModelAndView modelAndView)
		throws Exception {

	}
}
	

