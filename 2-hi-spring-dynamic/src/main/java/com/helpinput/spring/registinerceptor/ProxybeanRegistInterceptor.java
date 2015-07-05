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
 *@Date: 2015-7-6
 */
package com.helpinput.spring.registinerceptor;

import java.util.List;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.helpinput.annotation.Parent;
import com.helpinput.core.Utils;
import com.helpinput.spring.BeanRegister;

public class ProxybeanRegistInterceptor extends AbstractBeanRegistInterceptor {
	
	@Override
	public BeanDefinition beforeRegist(Class<?> clz, String beanName, String scope, DefaultListableBeanFactory dlbf,
										BeanDefinitionBuilder builder) {
		BeanDefinition bd = null;
		
		if (getCondition(clz)){
			Parent ann=clz.getAnnotation(Parent.class);
			String parentName = ann.value();
			String property = ann.property();
			if (Utils.hasLength(parentName) && Utils.hasLength(property)) {
				
				BeanDefinition parent = (GenericBeanDefinition) dlbf.getBeanDefinition(parentName);
				if (parent != null) {
					String baseBeanName = beanName + "$$$$";
					BeanRegister.removeBean(dlbf, null, baseBeanName);
					BeanDefinition basebd = builder.getBeanDefinition();
					basebd.setScope(scope);
					dlbf.registerBeanDefinition(baseBeanName, basebd);
					
					bd = new GenericBeanDefinition();
					
					bd.setParentName(parentName);
					List<PropertyValue> propertyValueList = bd.getPropertyValues().getPropertyValueList();
					RuntimeBeanReference reference = new RuntimeBeanReference(baseBeanName);
					PropertyValue pValue = new PropertyValue(property, reference);
					propertyValueList.add(pValue);
					
					//dlbf.getBean(baseBeanName);
					return bd;
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean  getCondition(Class<?> clz) {
		return clz.getAnnotation(Parent.class)!=null;
	}
	
}
