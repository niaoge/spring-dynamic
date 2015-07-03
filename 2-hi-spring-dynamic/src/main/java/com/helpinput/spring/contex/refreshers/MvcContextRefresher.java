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
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import com.helpinput.core.LoggerBase;
import com.helpinput.core.Utils;
import com.helpinput.spring.ScanedType;

public class MvcContextRefresher implements ContextRefresher {
	static Logger logger = LoggerBase.logger;
	
	@Override
	public void refresh(ApplicationContext context, Map<Class<?>, ScanedType> scanedClasses) {
		
		boolean needUpdateHandlerMapping = false;
		
		for (Entry<Class<?>, ScanedType> entry : scanedClasses.entrySet()) {
			if ((entry.getValue().getValue() > ScanedType.SAME.getValue())
					&& entry.getKey().getAnnotation(Controller.class) != null) {
				needUpdateHandlerMapping = true;
				break;
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
