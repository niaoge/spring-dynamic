/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *@Author: niaoge(Zhengsheng Xia)
 *@Email 78493244@qq.com
 *@Date: 2015-6-27
 */
package com.helpinput.spring.contex.refreshers;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.ClassUtils;

import com.helpinput.core.LoggerBase;
import com.helpinput.core.Utils;
import com.helpinput.spring.ScanedType;
public class SessiontRefresher implements ContextRefresher {
	static Logger logger = LoggerBase.logger;
	
	@SuppressWarnings("unchecked")
	ManagedList<Object> getManageList(DefaultListableBeanFactory dlbf, PropertyValue oldPropertyValue) {
		Set<String> oldClasses = null;
		
		if (oldPropertyValue != null) {
			Object value = oldPropertyValue.getValue();
			if (value != null && value instanceof ManagedList) {
				ManagedList<Object> real = (ManagedList<Object>) value;
				oldClasses = new HashSet<>(real.size() >>> 1);
				ClassLoader parentClassLoader = ClassUtils.getDefaultClassLoader();
				for (Object object : real) {
					TypedStringValue typedStringValue = (TypedStringValue) object;
					String className = typedStringValue.getValue();
					try {
						parentClassLoader.loadClass(className);
						oldClasses.add(className);
					}
					catch (ClassNotFoundException e) {
					}
				}
			}
		}
		
		
		int oldClassSize = (Utils.hasLength(oldClasses) ? oldClasses.size() : 0);
		Map<String, Object> beans = dlbf.getBeansWithAnnotation(Entity.class);
		HashSet<String> totalClasses = new HashSet<>(beans.size() + oldClassSize);
		if (oldClassSize > 0) {
			totalClasses.addAll(oldClasses);
		}
		
		for (Object entity : beans.values()) {
			String clzName = entity.getClass().getName();
			if (!totalClasses.contains(clzName)) {
				totalClasses.add(clzName);
			}
		}
		
		ManagedList<Object> list = new ManagedList<>(totalClasses.size());
		for (String clzName : totalClasses) {
			TypedStringValue typedStringValue = new TypedStringValue(clzName);
			list.add(typedStringValue);
		}
		return list;
	}
	
	@Override
	public void refresh(ApplicationContext context, Map<Class<?>, ScanedType> scanedClasses) {
		
		boolean needRefreshSessionFactory = false;
		for (Entry<Class<?>, ScanedType> entry : scanedClasses.entrySet()) {
			if (entry.getValue().getValue() > ScanedType.SAME.getValue()
					&& entry.getKey().getAnnotation(Entity.class) != null) {
				needRefreshSessionFactory = true;
				break;
			}
		}
		if (needRefreshSessionFactory) {
			DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) ((AbstractApplicationContext) context)
					.getBeanFactory();
			
			//testUserManager(dlbf);
			
			final String sessionFactory = "sessionFactory";
			final String annotatedClasses = "annotatedClasses";
			final String setSessionFactory = "setSessionFactory";
			
			BeanDefinition oldSessionFactoryDef = dlbf.getBeanDefinition(sessionFactory);
			
			if (oldSessionFactoryDef != null) {
				dlbf.removeBeanDefinition(sessionFactory);
				MutablePropertyValues propertyValues = oldSessionFactoryDef.getPropertyValues();
				PropertyValue oldPropertyValue = propertyValues.getPropertyValue(annotatedClasses);
				
				propertyValues.removePropertyValue(annotatedClasses);
				
				BeanDefinition newSessionFactoryDef = BeanDefinitionBuilder.rootBeanDefinition(
						oldSessionFactoryDef.getBeanClassName()).getBeanDefinition();
				
				List<PropertyValue> propertyValueList = newSessionFactoryDef.getPropertyValues().getPropertyValueList();
				
				propertyValueList.addAll(propertyValues.getPropertyValueList());
				propertyValueList.add(new PropertyValue(annotatedClasses, getManageList(dlbf, oldPropertyValue)));
				
				dlbf.registerBeanDefinition(sessionFactory, newSessionFactoryDef);
				
				SessionFactory sessionFactoryImpl = (SessionFactory) dlbf.getBean(sessionFactory);
				
				String[] beanNames = dlbf.getBeanDefinitionNames();
				for (String beanName : beanNames) {
					BeanDefinition beanDefinition = dlbf.getBeanDefinition(beanName);
					
					PropertyValues pValues = beanDefinition.getPropertyValues();
					if (pValues.getPropertyValue(sessionFactory) != null) {
						Object theBean = dlbf.getBean(beanName);
						Method method = Utils.findMethod(theBean, setSessionFactory, sessionFactoryImpl);
						if (method != null)
							Utils.InvokedMethod(theBean, method, sessionFactoryImpl);
					}
				}
			}
		}
	}
	
}
