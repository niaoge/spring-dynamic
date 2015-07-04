/**
 * thanks  langgufu  for static demo source(http://langgufu.iteye.com/blog/2088355) 
 */
package com.tgb.manager;

import java.util.List;

import com.tgb.entity.User;

public interface UserManager {

	public User getUser(String id);
	
	public List<User> getAllUser();
	
	public void addUser(User user);
	
	public boolean delUser(String id);
	
	public boolean updateUser(User user);
}
