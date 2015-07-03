package com.script.web;

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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.helpinput.core.LoggerBase;
import com.script.entity.Teacher;
import com.script.manager.TeacherManager;
import com.script.propertyEditors.DatePropertyEditor;

@Controller
@RequestMapping("/teacher")
@Scope("request")
public class TeacherController {
	static Logger logger = LoggerBase.logger;
	
	@Resource
	TeacherManager teacherManager;
	
	@RequestMapping("/getAllTeacher")
	public String getAllTeacher(HttpServletRequest request) {
		
		request.setAttribute("teacherList", teacherManager.getAllTeacher());
		
		return "/teacherList";
	}
	
	@RequestMapping("/getTeacher")
	public String getTeacher(String id, HttpServletRequest request) {
		
		request.setAttribute("teacher", teacherManager.getTeacher(id));
		
		return "/editTeacher";
	}
	
	@RequestMapping("/toAddTeacher")
	public String toAddTeacher() {
		return "/addTeacher";
	}
	
	@RequestMapping("/addTeacher")
	public String addTeacher(Teacher teacher, HttpServletRequest request) {
		teacherManager.addTeacher(teacher);
		return "redirect:/teacher/getAllTeacher";
	}
	
	@RequestMapping("/delTeacher")
	public void delTeacher(String id, HttpServletResponse response) {
		
		String result = "{\"result\":\"error\"}";
		
		if (teacherManager.delTeacher(id)) {
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
	
	@RequestMapping("/updateTeacher")
	public String updateTeacher(Teacher teacher, HttpServletRequest request) {
		if (teacherManager.updateTeacher(teacher)) {
			teacher = teacherManager.getTeacher(teacher.getId());
			request.setAttribute("teacher", teacher);
			return "redirect:/teacher/getAllTeacher";
		}
		else {
			return "/error";
		}
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) throws ServletException {
		
		//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//		dateFormat.setLenient(false);
		//		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		binder.registerCustomEditor(Date.class, new DatePropertyEditor());
	}
}
