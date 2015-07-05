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
package com.helpinput.spring.refresher.mvc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;

import com.helpinput.core.LoggerBase;
import com.helpinput.core.Utils;
import com.helpinput.spring.ScanedType;
import com.helpinput.spring.refresher.ContextRefresher;

public class MvcContextRefresher implements ContextRefresher {
	static Logger logger = LoggerBase.logger;
	
	@Override
	public void refresh(ApplicationContext context, Map<Class<?>, ScanedType> scanedClasses) {
		
		boolean needUpdateHandlerMapping = false;
		boolean needUpdateInterceptor = false;
		
		for (Entry<Class<?>, ScanedType> entry : scanedClasses.entrySet()) {
			if ((entry.getValue().getValue() > ScanedType.SAME.getValue())
					&& entry.getKey().getAnnotation(Controller.class) != null) {
				needUpdateHandlerMapping = true;
				break;
			}
		}
		
		for (Entry<Class<?>, ScanedType> entry : scanedClasses.entrySet()) {
			if ((entry.getValue().getValue() > ScanedType.SAME.getValue())
					&& HandlerInterceptor.class.isAssignableFrom(entry.getKey())) {
				needUpdateInterceptor = true;
				break;
			}
		}
		
		if (needUpdateInterceptor) {
			DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) ((AbstractApplicationContext) context)
					.getBeanFactory();
			Map<String, AbstractHandlerMapping> mappings = dlbf.getBeansOfType(AbstractHandlerMapping.class);
			
			if (Utils.hasLength(mappings)) {
				Field interceptorsField = Utils.findField(AbstractHandlerMapping.class, "interceptors");
				Field mappedInterceptorsFeild = Utils.findField(AbstractHandlerMapping.class, "mappedInterceptors");
				Method initApplicationContext = Utils
						.findMethod(AbstractHandlerMapping.class, "initApplicationContext");
				if (interceptorsField != null && mappedInterceptorsFeild != null && initApplicationContext != null) {
					for (AbstractHandlerMapping mapping : mappings.values()) {
						synchronized (mapping) {
							final List<Object> interceptors = Utils.getFieldValue(mapping, interceptorsField);
							if (Utils.hasLength(interceptors))
								interceptors.clear();
							final List<MappedInterceptor> mappedInterceptors = Utils.getFieldValue(mapping,
									mappedInterceptorsFeild);
							if (Utils.hasLength(mappedInterceptors))
								mappedInterceptors.clear();
							Utils.InvokedMethod(mapping, initApplicationContext);
						}
					}
				}
			}
			
		}
		
		if (needUpdateHandlerMapping) {
			final String mapName = "org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0";
			Object mappingHandler = context.getBean(mapName);
			if (mappingHandler != null) {
				Method method = Utils.findMethod(mappingHandler, "initHandlerMethods");
				Map<?, ?> handlerMethods = Utils.getFieldValue(mappingHandler, "handlerMethods");
				Map<?, ?> urlMap = Utils.getFieldValue(mappingHandler, "urlMap");
				if (method != null && handlerMethods != null && urlMap != null) {
					synchronized (mappingHandler) {
						handlerMethods.clear();
						urlMap.clear();
						Utils.InvokedMethod(mappingHandler, method);
					}
				}
			}
		}
	}
	
}
