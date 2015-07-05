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
 *@Date: 2015-6-20
 */
package com.helpinput.spring.servlet.mvc;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.helpinput.core.LoggerBase;
import com.helpinput.holder.ContextHolder;
import com.helpinput.spring.registinerceptor.mvc.UrlInterceptorBeanRegistInterceptor;

@SuppressWarnings("serial")
public class EnhanceDispachServlet extends DispatcherServlet {
	static Logger logger = LoggerBase.logger;
	
	@Override
	protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
		ContextHolder.beanRegistIntercpterHolder.register(new UrlInterceptorBeanRegistInterceptor());

		Class<?> contextClass = getContextClass();
		if (logger.isDebugEnabled()) {
			logger.debug("Servlet with name '" + getServletName()
					+ "' will try to create custom WebApplicationContext context of class '" + contextClass.getName()
					+ "'" + ", using parent context [" + parent + "]");
		}
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Fatal initialization error in servlet with name '"
					+ getServletName() + "': custom WebApplicationContext class [" + contextClass.getName()
					+ "] is not of type ConfigurableWebApplicationContext");
		}
		ConfigurableWebApplicationContext wac;
		if (parent instanceof ConfigurableWebApplicationContext)
			wac = (ConfigurableWebApplicationContext) parent;
		else {
			wac = (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
			wac.setEnvironment(getEnvironment());
			wac.setParent(parent);
			wac.setConfigLocation(getContextConfigLocation());
			configureAndRefreshWebApplicationContext(wac);
		}
		return wac;
	}
	
}
