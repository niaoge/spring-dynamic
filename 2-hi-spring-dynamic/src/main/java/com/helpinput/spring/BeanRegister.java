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

import static com.helpinput.spring.Consts.ApplicatoinScoped;
import static com.helpinput.spring.Consts.FlashScoped;
import static com.helpinput.spring.Consts.Prototype;
import static com.helpinput.spring.Consts.RequestScoped;
import static com.helpinput.spring.Consts.SessionScoped;
import static com.helpinput.spring.Consts.Singleton;
import static com.helpinput.spring.Consts.ViewScoped;
import static com.helpinput.spring.Consts.application_scope;
import static com.helpinput.spring.Consts.flash_scope;
import static com.helpinput.spring.Consts.prototype_scope;
import static com.helpinput.spring.Consts.relativePath;
import static com.helpinput.spring.Consts.request_scope;
import static com.helpinput.spring.Consts.session_scope;
import static com.helpinput.spring.Consts.singleton_scope;
import static com.helpinput.spring.Consts.view_scope;

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;

import com.helpinput.core.Utils;
import com.helpinput.propertyeditors.PropertyEditorRegister;
import com.helpinput.spring.annotation.Parent;
import com.helpinput.spring.contex.refreshers.ContextRefresher;

class BeanRegister {
	static Logger logger = LoggerFactory.getLogger(BeanRegister.class);
	
	enum Info {
		Registed, Updated, Removed;
	}
	
	private static void logRegist(Info info, String relativePath, String beanName, String scope) {
		System.out.println(info.name() + " bean [" + beanName + "] of [scope: " + scope + "] in file [" + relativePath
				+ "]");
	}
	
	static String findScopeStr(Class<?> clz) {
		String result = null;
		if (clz != null && clz != Object.class) {
			Annotation[] annotations = clz.getDeclaredAnnotations();
			for (int i = annotations.length - 1; i >= 0; i--) {
				Annotation annotation = annotations[i];
				String simpleName = annotation.annotationType().getSimpleName();
				
				if (RequestScoped.equals(simpleName))
					return request_scope;
				else if (FlashScoped.equals(simpleName))
					return flash_scope;
				else if (ViewScoped.equals(simpleName))
					return view_scope;
				else if (SessionScoped.equals(simpleName))
					return session_scope;
				else if (ApplicatoinScoped.equals(simpleName))
					return application_scope;
				else if (Singleton.equals(simpleName))
					return singleton_scope;
				else if (Prototype.equals(simpleName))
					return prototype_scope;
				
				else if (annotation instanceof Scope) {
					Scope scopeAnn = (Scope) annotation;
					if (Utils.hasLength(scopeAnn.value()))
						return scopeAnn.value();
				}
			}
			result = findScopeStr(clz.getSuperclass());
		}
		if (Utils.hasLength(result))
			return result;
		return singleton_scope;
	}
	
	/** 用更快的aop实现 */
	/*
	 * private static boolean checkInterceptor(Class<?> clz) { if (clz == null)
	 * return false; Method[] methods = clz.getDeclaredMethods(); for (Method
	 * method : methods) { //如果class内有方法用Handled 或 PostValidate 标注，将生成子类，以便拦截 if
	 * (method.getAnnotation(Handled.class) != null ||
	 * method.getAnnotation(PostValidate.class) != null) { return true; } }
	 * return checkInterceptor(clz.getSuperclass()); }
	 */
	
	public static BeanDefinitionBuilder createBuilder(Class<?> clz, Class<?> InterceptorClass) {
		/** 用更快的aop实现 */
		/*
		 * if (InterceptorClass != null) { if (checkInterceptor(clz)) {
		 * BeanDefinitionBuilder builder =
		 * BeanDefinitionBuilder.rootBeanDefinition(InterceptorClass,
		 * "getInstance"); builder.addConstructorArgValue(clz); return builder;
		 * } }
		 */
		return BeanDefinitionBuilder.rootBeanDefinition(clz);
	}
	
	public static BeanDefinitionWrap registerBean(ApplicationContext context, DefaultListableBeanFactory dlbf,
													BeanDefinitionBuilder builder, Class<?> clz, String beanName,
													String scope, BeanInfo beanInfo) {
		if (!Utils.hasLength(beanName)) {
			beanName = Commons.getBeanName(clz);
			if (!Utils.hasLength(beanName))
				beanName = Utils.beanName(beanInfo.scanName);
		}
		
		if (!Utils.hasLength(beanName))
			return null;
		
		//原有的bean如果有注册信息，将bean清除
		if (beanName.endsWith("Impl")) {
			beanName = beanName.substring(0, beanName.length() - 4);
		}
		
		if (!Utils.hasLength(beanName))
			return null;
		
		BeanDefinition bd = null;
		if (!Utils.hasLength(scope))
			scope = findScopeStr(clz);
		//logger.info(beanName + "........〖1〗........" + scope);
		
		Parent ann = clz.getAnnotation(Parent.class);
		if (ann != null) {
			String parentName = ann.value();
			String property = ann.property();
			if (Utils.hasLength(parentName) && Utils.hasLength(property)) {
				
				GenericBeanDefinition parent = (GenericBeanDefinition) dlbf.getBeanDefinition(parentName);
				if (parent != null) {
					String baseBeanName = beanName + "$$$$";
					removeBean(dlbf, null, baseBeanName);
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
				}
			}
		}
		
		removeBean(dlbf, beanInfo, null);
		if (bd == null)
			bd = builder.getBeanDefinition();
		
		if (beanInfo != null)
			bd.setAttribute(relativePath, beanInfo.relativePath);
		bd.setScope(scope);
		
		dlbf.registerBeanDefinition(beanName, bd);
		
		boolean isUpdate = beanInfo != null ? beanInfo.isUpdate : false;
		String relativePath = beanInfo != null ? beanInfo.relativePath : "";
		
		BeanDefinitionWrap beanDefinitionWrap = new BeanDefinitionWrap(beanName, bd);
		
		if (ContextRefresher.class.isAssignableFrom(clz)) {
			RefresherHolder.registerRefresher(beanName);
		}
		
		logRegist(isUpdate ? Info.Updated : Info.Registed, relativePath, beanName, scope);
		return beanDefinitionWrap;
	}
	
	protected static boolean removeBean(DefaultListableBeanFactory dlbf, BeanInfo beanInfo, String beanName) {
		BeanDefinition bd = null;
		if (!Utils.hasLength(beanName))
			if (beanInfo != null)
				beanName = beanInfo.beanName;
		if (!Utils.hasLength(beanName))
			return false;
		
		try {
			bd = dlbf.getBeanDefinition(beanName);
			if (bd != null) {
				//如果是替换的bean则不移除 relativePath相同, 但是脚本已经不存在了
				//					BeanRegister.runOnUnregisger(beanName, dlbf);
				String scope = bd.getScope();
				/**
				 * bean注册后，应将application,singleton中的bean清除，session、view、 flash、
				 * request中的bean自行清除,被其它
				 * session,view,flash,request中的bean引用的application中的bean虽然在
				 * application,singleton中的bean在上下文中已无引用，
				 * 但是如果在其它scope的bean中被引用bean中，直到该引用的bean清除时而清除，
				 */
				if (application_scope.equals(scope))
					dlbf.destroyScopedBean(beanName);
				else if (singleton_scope.equals(scope))
					dlbf.destroySingleton(beanName);
				
				dlbf.removeBeanDefinition(beanName);
				if (beanInfo != null && beanInfo.beanClass != null
						&& PropertyEditor.class.isAssignableFrom(beanInfo.beanClass)) {
					RefresherHolder.removeRefresher(beanName);
				}
				logRegist(Info.Removed, (String) bd.getAttribute(relativePath), beanInfo.beanName, scope);
				return true;
			}
		}
		catch (NoSuchBeanDefinitionException e) {
			//如果找不到 BeanDefinition,则不需要再保留相信的beanInfo,所以
			return true;
		}
		return false;
	}
	
	public static BeanDefinitionWrap registerBean(ApplicationContext context, DefaultListableBeanFactory dlbf,
													Class<?> clz, Class<?> InterceptorClass, String beanName,
													String scope, BeanInfo beanInfo) {
		BeanDefinitionBuilder builder = createBuilder(clz, InterceptorClass);
		return registerBean(context, dlbf, builder, clz, beanName, scope, beanInfo);
	}
	
	public static void refreshContext(ApplicationContext context, Map<Class<?>, ScanedType> refreshedClass) {
		ContextRefresher contextRefresher = null;
		
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) ((AbstractApplicationContext) context)
				.getBeanFactory();
		
		for (Entry<Class<?>, ScanedType> entry : refreshedClass.entrySet()) {
			if (entry.getValue().getValue() > ScanedType.SAME.getValue() && PropertyEditor.class.isAssignableFrom(entry.getKey())) {
				@SuppressWarnings("unchecked")
				Class<? extends PropertyEditor> propertyEditorType = (Class<? extends PropertyEditor>) entry.getKey();
				PropertyEditorRegister.registerProtertyEditor(dlbf, propertyEditorType);
			}
		}
		
		synchronized (RefresherHolder.refresherNames) {
			if (Utils.hasLength(RefresherHolder.refresherNames)) {
				for (String contextRefresherName : RefresherHolder.refresherNames) {
					Object bean = null;
					try {
						bean = context.getBean(contextRefresherName);
					}
					catch (BeansException e) {
						logger.info("bean of [" + contextRefresherName + "] not found!");
					}
					if (bean != null && bean instanceof ContextRefresher) {
						contextRefresher = (ContextRefresher) bean;
						contextRefresher.refresh(context, refreshedClass);
					}
				}
			}
		}
	}
	
}
