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

package com.helpinput.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.helpinput.annotation.TargetType;
import com.helpinput.spring.support.ClassLoaderHolder;
import org.slf4j.Logger;
import com.helpinput.core.LoggerBase;

@TargetType(Class.class)
public class GLClassEditor extends PropertyEditorSupport {
	static Logger logger = LoggerBase.logger;
	
	private final ClassLoader classLoader;
	
	public GLClassEditor() {
		this(null);
	}
	
	public GLClassEditor(ClassLoader classLoader) {
		this.classLoader = (classLoader != null ? classLoader : GLClassEditor.class.getClassLoader());
	}
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			Class<?> result;
			try {
				result = ClassUtils.resolveClassName(text.trim(), this.classLoader);
			}
			catch (IllegalArgumentException e) {
				//for sessionFactory annotatedClasses
				result = ClassUtils.resolveClassName(text.trim(), ClassLoaderHolder.gcl);
				try {
					throw new Exception();
				}
				catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			setValue(result);
		}
		else {
			setValue(null);
		}
	}
	
	@Override
	public String getAsText() {
		Class<?> clazz = (Class<?>) getValue();
		if (clazz != null) {
			return ClassUtils.getQualifiedName(clazz);
		}
		else {
			return "";
		}
	}
	
}
