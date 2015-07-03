package com.script.manager;

import java.util.List;

import javax.annotation.Resource;

import com.helpinput.spring.annotation.Parent;
import com.script.dao.TeacherDao;
import com.script.entity.Teacher;

@Parent("transactionProxy")
public class TeacherManagerImpl implements TeacherManager {
	
   @Resource
	private TeacherDao teacherDao;
	
	public void setTeacherDao(TeacherDao teacherDao) {
		this.teacherDao = teacherDao;
	}

	@Override
	public Teacher getTeacher(String id) {
		return teacherDao.getTeacher(id);
	}

	@Override
	public List<Teacher> getAllTeacher() {
		return teacherDao.getAllTeacher();
	}

	@Override
	public void addTeacher(Teacher teacher) {
		teacherDao.addTeacher(teacher);
	}

	@Override
	public boolean delTeacher(String id) {
		return teacherDao.delTeacher(id);
	}

	@Override
	public boolean updateTeacher(Teacher teacher) {
		return teacherDao.updateTeacher(teacher);
	}
	
	public void test1() {
		
	}

}
