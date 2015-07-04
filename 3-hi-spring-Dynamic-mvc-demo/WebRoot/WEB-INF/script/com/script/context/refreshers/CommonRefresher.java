package com.script.context.refreshers;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.helpinput.spring.ScanedType;

public class CommonRefresher extends  com.helpinput.spring.contex.refreshers.CommonRefresher{

	@Override
	public void refresh(ApplicationContext context, Map<Class<?>, ScanedType> refreshedClass) {
		// do some refrer here.... ,this is script , execute where update 
	}
	
}
