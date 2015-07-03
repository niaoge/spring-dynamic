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
 *@Date: 2015-6-16
 */
package com.helpinput.spring;

import javax.inject.Named;

import org.slf4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import com.helpinput.core.LoggerBase;
import com.helpinput.core.Utils;

public class Commons {
	static Logger logger = LoggerBase.logger;
	
	public static DefaultListableBeanFactory getDefaultListableBeanFactory(ApplicationContext context) {
		if (context instanceof AbstractApplicationContext) {
			return (DefaultListableBeanFactory) ((AbstractApplicationContext) context).getBeanFactory();
		}
		return (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
	}
	
	public static String getBeanName(Class<?> beanClass) {
		String beanName = null;
		Named nameAnn = beanClass.getAnnotation(Named.class);
		//todo 如果没有named标注，则不加入bean;
		if (nameAnn != null) {
			if (Utils.hasLength(nameAnn.value()))
				beanName = nameAnn.value();
		}
		else {
			Component componentAnn = beanClass.getAnnotation(Component.class);
			if (componentAnn != null) {
				if (Utils.hasLength(componentAnn.value()))
					beanName = componentAnn.value();
			}
		}
		
		if (!Utils.hasLength(beanName)) {
			beanName = Utils.beanName(beanClass.getSimpleName());
		}
		return beanName;
	}
	
	public static String getSetterName(String name) {
		if (Utils.hasLength(name)) {
			return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return null;
	}
	
}
