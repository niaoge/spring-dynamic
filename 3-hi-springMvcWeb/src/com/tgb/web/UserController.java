/**
 * thanks  langgufu  for static demo source(http://langgufu.iteye.com/blog/2088355) 
 */
package com.tgb.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.helpinput.core.LoggerBase;
import com.tgb.entity.User;
import com.tgb.manager.UserManager;

@Controller
@RequestMapping("/user")
public class UserController {
	static Logger logger = LoggerBase.logger;
	
	@Resource(name = "userManager")
	private UserManager userManager;
	
	@RequestMapping("/getAllUser")
	public String getAllUser(HttpServletRequest request) {
		request.setAttribute("userList", userManager.getAllUser());
		return "/index";
	}
	
	@RequestMapping("/getUser")
	public String getUser(String id, HttpServletRequest request) {
		
		request.setAttribute("user", userManager.getUser(id));
		
		return "/editUser";
	}
	
	@RequestMapping("/toAddUser")
	public String toAddUser() {
		return "/addUser";
	}
	
	@RequestMapping("/addUser")
	public String addUser(User user, HttpServletRequest request) {
		logger.info(userManager.getClass().toString() + "........〖1〗........" + userManager);
		userManager.addUser(user);
		
		return "redirect:/user/getAllUser";
	}
	
	@RequestMapping("/delUser")
	public void delUser(String id, HttpServletResponse response) {
		
		String result = "{\"result\":\"error\"}";
		
		if (userManager.delUser(id)) {
			result = "{\"result\":\"success\"}";
		}
		
		response.setContentType("application/json");
		
		try {
			PrintWriter out = response.getWriter();
			out.write(result);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/updateUser")
	public String updateUser(User user, HttpServletRequest request) {
		if (userManager.updateUser(user)) {
			user = userManager.getUser(user.getId());
			request.setAttribute("user", user);
			return "redirect:/user/getAllUser";
		}
		else {
			return "/error";
		}
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) throws ServletException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
