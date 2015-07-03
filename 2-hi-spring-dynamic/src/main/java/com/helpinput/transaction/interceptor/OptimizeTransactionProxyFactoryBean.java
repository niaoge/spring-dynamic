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
package com.helpinput.transaction.interceptor;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springframework.util.ClassUtils;

@SuppressWarnings("serial")
public class OptimizeTransactionProxyFactoryBean extends TransactionProxyFactoryBean {
	private Object target;
	
	private Class<?>[] proxyInterfaces;
	
	private Object[] preInterceptors;
	
	private Object[] postInterceptors;
	
	/** Default is global AdvisorAdapterRegistry */
	private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
	
	
	private Object proxy;
	
	@Override
	public void setTarget(Object target) {
		this.target = target;
	}
	
	@Override
	public void setProxyInterfaces(Class<?>[] proxyInterfaces) {
		this.proxyInterfaces = proxyInterfaces;
	}
	
	@Override
	public void setPreInterceptors(Object[] preInterceptors) {
		this.preInterceptors = preInterceptors;
	}
	
	@Override
	public void setPostInterceptors(Object[] postInterceptors) {
		this.postInterceptors = postInterceptors;
	}
	
	@Override
	public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
		this.advisorAdapterRegistry = advisorAdapterRegistry;
	}
	
	public void afterPropertiesSet() {
		if (this.target == null) {
			throw new IllegalArgumentException("Property 'target' is required");
		}
		if (this.target instanceof String) {
			throw new IllegalArgumentException("'target' needs to be a bean reference, not a bean name as value");
		}
		
		
		ClassLoader proxyClassLoader=target.getClass().getClassLoader();
		
		ProxyFactory proxyFactory = new ProxyFactory();
		
		if (this.preInterceptors != null) {
			for (Object interceptor : this.preInterceptors) {
				proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
			}
		}
		
		// Add the main interceptor (typically an Advisor).
		proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(createMainInterceptor()));
		
		if (this.postInterceptors != null) {
			for (Object interceptor : this.postInterceptors) {
				proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
			}
		}
		
		proxyFactory.copyFrom(this);
		
		TargetSource targetSource = createTargetSource(this.target);
		proxyFactory.setTargetSource(targetSource);
		
		if (this.proxyInterfaces != null) {
			proxyFactory.setInterfaces(this.proxyInterfaces);
		}
		else if (!isProxyTargetClass()) {
			// Rely on AOP infrastructure to tell us what interfaces to proxy.
			proxyFactory.setInterfaces(ClassUtils.getAllInterfacesForClass(targetSource.getTargetClass(),
					proxyClassLoader));
		}
		
		/**
		 * use this option to let proxyFactory user cglib to create proxy,otherwise in dynamic script ,this is no dynamic interface
		 * 使用这个选荋 将使用cglib 产生代理类，否则在动动的java中，没有动态的接口
		 */
		proxyFactory.setOptimize(true);
		this.proxy = proxyFactory.getProxy(proxyClassLoader);
	}
	
	public Object getObject() {
		if (this.proxy == null) {
			throw new FactoryBeanNotInitializedException();
		}
		return this.proxy;
	}
	
	public Class<?> getObjectType() {
		if (this.proxy != null) {
			return this.proxy.getClass();
		}
		if (this.proxyInterfaces != null && this.proxyInterfaces.length == 1) {
			return this.proxyInterfaces[0];
		}
		if (this.target instanceof TargetSource) {
			return ((TargetSource) this.target).getTargetClass();
		}
		if (this.target != null) {
			return this.target.getClass();
		}
		return null;
	}
	
}
