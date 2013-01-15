package com.thousandeyes.minitwitter.aop;

import com.thousandeyes.minitwitter.dtos.entities.User;

/**
 * @author Miguel
 *
 */
public class UserContextTLS {
	public static final ThreadLocal<User> instance = new ThreadLocal<User>();
	public static Boolean isAdmin() {
		return instance.get().getKey().equals("admin");
	}

}
