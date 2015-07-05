package com.script.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Scope;

import com.helpinput.annotation.Parent;
import com.helpinput.annotation.Prototype;

@Entity
@Table(name="T_Teacher")
@Prototype
public class Teacher implements Serializable {

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name = "system-uuid",strategy="uuid")
	@Column(length=32)
	String id;
	
	@Column(length=32)
	String teacherName;
	
	@Column(length=32)
	String age;
	
	Date birthday;
	
	@Override
	public String toString() {
		return this.getClass().toString()+"[id:"+id+",teacherName:"+teacherName+",age:"+age+",birthday:"+birthday+"]";
	}

}
