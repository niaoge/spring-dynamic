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
package com.helpinput.spring.registinerceptor;

import static com.helpinput.spring.Consts.application_scope;
import static com.helpinput.spring.Consts.singleton_scope;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public abstract class AbstractBeanRegistInterceptor implements BeanRegistInterceptor {
	protected String beanNameSuffix = "$$$$";
	
	@Override
	public boolean afterRemove(Class<?> clz, String beanName, String scope, DefaultListableBeanFactory dlbf) {
		if (getCondition(clz)) {
			String refBeanName = beanName + beanNameSuffix;
			if (application_scope.equals(scope))
				dlbf.destroyScopedBean(refBeanName);
			else if (singleton_scope.equals(scope))
				dlbf.destroySingleton(refBeanName);
			dlbf.removeBeanDefinition(refBeanName);
			return true;
		}
		return false;
	}
	
}
