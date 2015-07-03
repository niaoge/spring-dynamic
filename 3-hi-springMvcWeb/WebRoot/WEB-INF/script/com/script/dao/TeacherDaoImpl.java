package com.script.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;

import com.helpinput.core.LoggerBase;
import com.script.entity.Teacher;
public class TeacherDaoImpl implements TeacherDao {
	static Logger logger = LoggerBase.logger;

	@Resource
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Teacher getTeacher(String id) {
		
		String hql = "from Teacher t where t.id=?";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setString(0, id);
		
		return (Teacher)query.uniqueResult();
	}

	@Override
	public List<Teacher> getAllTeacher() {
		
		
		String hql = "from Teacher";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		
		return query.list();
	}

	@Override
	public void addTeacher(Teacher teacher) {
		sessionFactory.getCurrentSession().save(teacher);
	}

	@Override
	public boolean delTeacher(String id) {
		
		String hql = "delete Teacher t where t.id = ?";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setString(0, id);
		
		return (query.executeUpdate() > 0);
	}

	@Override
	public boolean updateTeacher(Teacher teacher) {
		System.out.println(teacher.toString());
		String hql = "update Teacher t set t.teacherName = ?,t.age=? ,t.birthday=? where t.id = ?";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setString(0, teacher.getTeacherName());
		query.setString(1, teacher.getAge());
		query.setDate(2, teacher.getBirthday());
		query.setString(3, teacher.getId());
		
		return (query.executeUpdate() > 0);
	}

}
