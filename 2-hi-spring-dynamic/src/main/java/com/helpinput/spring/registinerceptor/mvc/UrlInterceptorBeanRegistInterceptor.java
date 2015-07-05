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
 *@Date: 2015-7-7
 */
package com.helpinput.spring.registinerceptor.mvc;

import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

import com.helpinput.annotation.Mapping;
import com.helpinput.annotation.MappingExclude;
import com.helpinput.core.LoggerBase;
import com.helpinput.core.Utils;
import com.helpinput.spring.registinerceptor.BeanRegistInterceptor;

public class UrlInterceptorBeanRegistInterceptor implements BeanRegistInterceptor {
	static Logger logger = LoggerBase.logger;
	
	@Override
	public BeanDefinition beforeRegist(Class<?> clz, String beanName, String scope, DefaultListableBeanFactory dlbf,
										BeanDefinitionBuilder builder) {
		if (getCondition(clz)) {
			String refDefname = beanName + "$$$$";
			
			RootBeanDefinition refDef = new RootBeanDefinition();
			refDef.setBeanClass(clz);
			refDef.setScope(scope);
			dlbf.registerBeanDefinition(refDefname, refDef);
			
			RootBeanDefinition mappedInterceptorDef = new RootBeanDefinition(MappedInterceptor.class);
			mappedInterceptorDef.setScope(scope);
			
			ManagedList<String> includePatterns = null;
			ManagedList<String> excludePatterns = null;
			Object interceptorBean;
			
			Mapping mapAnn = clz.getAnnotation(Mapping.class);
			if (mapAnn != null) {
				String[] includes = mapAnn.value();
				if (Utils.hasLength(includes)) {
					includePatterns = new ManagedList<>(includes.length);
					for (String s : includes) 
						includePatterns.add(s);
				}
			}

			MappingExclude unMapAnn = clz.getAnnotation(MappingExclude.class);
			if (unMapAnn != null) {
				String[] excludes = unMapAnn.value();
				if (Utils.hasLength(excludes)) {
					excludePatterns = new ManagedList<>(excludes.length);
					for (String s : excludes) 
						excludePatterns.add(s);
				}
			}
			
			interceptorBean = new RuntimeBeanReference(refDefname);
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, includePatterns);
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(1, excludePatterns);
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(2, interceptorBean);
			return mappedInterceptorDef;
		}
		return null;
	}
	
	@Override
	public boolean afterRemove(Class<?> clz, String beanName, String scope, DefaultListableBeanFactory dlbf) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean getCondition(Class<?> clz) {
		return HandlerInterceptor.class.isAssignableFrom(clz);
	}
	
}
