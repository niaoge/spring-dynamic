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
 *@Date: 2015-7-3
 */
package com.helpinput.propertyeditors;

import java.beans.PropertyEditor;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.helpinput.core.Utils;
import com.helpinput.spring.Commons;
import com.helpinput.spring.annotation.Properties;
import com.helpinput.spring.annotation.Property;
import com.helpinput.spring.annotation.TargetType;
import org.slf4j.Logger;
import com.helpinput.core.LoggerBase;

public class PropertyEditorRegister {
	static Logger logger = LoggerBase.logger;
	
	private static boolean addProperty(Class<? extends PropertyEditor> propertyEditorType, Property propertyAnn,
										Map<Method, Object> setMethodAndValues) {
		
		final String methodName = Commons.getSetterName(propertyAnn.name());
		
		if (!Utils.hasLength(methodName))
			return false;
		
		Method method = Utils.findMethod(propertyEditorType, methodName);
		
		if (method == null || !method.getReturnType().equals(Void.TYPE))
			return false;
		
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		if (parameterTypes == null || parameterTypes.length != 1)
			return false;
		
		try {
			Class<?> parameterType = parameterTypes[0];
			Object realValue = ConvertUtils.convert(propertyAnn.value(), parameterType);
			setMethodAndValues.put(method, realValue);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Class<?> getTargetType(Class<? extends PropertyEditor> propertyEditorType, Class<?>... targetType) {
		Class<?> tmpTargetType = null;
		if (!Utils.hasLength(targetType)) {
			TargetType targetTypeAnn = propertyEditorType.getAnnotation(TargetType.class);
			if (targetTypeAnn == null)
				return null;
			tmpTargetType = targetTypeAnn.value();
		}
		else
			tmpTargetType = targetType[0];
		
		return tmpTargetType;
	}
	
	public static PropertyEditor newProtertyEditor(Class<? extends PropertyEditor> propertyEditorType,
													Class<?>... targetType) {
		
		final Class<?> theTargetType = getTargetType(propertyEditorType, targetType);
		if (theTargetType == null)
			return null;
		
		Map<Method, Object> setMethodAndValues = null;
		
		Properties propertiesAnn = propertyEditorType.getAnnotation(Properties.class);
		if (propertiesAnn != null) {
			Property[] properties = propertiesAnn.value();
			if (Utils.hasLength(properties)) {
				setMethodAndValues = new HashMap<>(properties.length + 1);
				for (Property property : properties) {
					if (!addProperty(propertyEditorType, property, setMethodAndValues))
						return null;
				}
			}
		}
		
		Property propertyAnn = propertyEditorType.getAnnotation(Property.class);
		if (propertyAnn != null) {
			if (setMethodAndValues == null)
				setMethodAndValues = new HashMap<>(1);
			if (!addProperty(propertyEditorType, propertyAnn, setMethodAndValues))
				return null;
		}
		
		PropertyEditor propertyEditor;
		try {
			propertyEditor = propertyEditorType.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		
		if (Utils.hasLength(setMethodAndValues)) {
			for (Entry<Method, Object> entry : setMethodAndValues.entrySet()) {
				try {
					Utils.InvokedMethod(propertyEditor, entry.getKey(), entry.getValue());
				}
				catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return propertyEditor;
	}
	
	public static void registerProtertyEditor(DefaultListableBeanFactory dlbf,
												Class<? extends PropertyEditor> propertyEditorType,
												Class<?>... targetType) {
		final Class<?> theTargetType = getTargetType(propertyEditorType, targetType);
		if (theTargetType == null)
			return;
		
		final PropertyEditor propertyEditor = newProtertyEditor(propertyEditorType, theTargetType);
		if (propertyEditor == null)
			return;
		
		PropertyEditorRegistrar myPropertyEditorRegistrar = new PropertyEditorRegistrar() {
			public void registerCustomEditors(PropertyEditorRegistry registry) {
				registry.registerCustomEditor(theTargetType, propertyEditor);
			}
		};
		
		dlbf.addPropertyEditorRegistrar(myPropertyEditorRegistrar);
	}
}
