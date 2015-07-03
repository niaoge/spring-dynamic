/**
*@Author: 夏政生 qq:78493244
*@Date: 2014-4-22
*@Copyright: 2014 www.inputhelp.com Inc. All rights reserved.
*/
package com.helpinput.spring;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.helpinput.core.LoggerBase;

/**
 * <pre>
 * cglib生成的拦截器
 * </pre>
 * 
 * <pre>
 * author: auwa, date:2013-12-4
 * </pre>
 */

public class Interceptor extends LoggerBase implements MethodInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <pre>
	 * cglib生成的子类实例
	 * </pre>
	 * 
	 * @param supperClass
	 * @return
	 */
	public static Object getInstance(Class<?> supperClass) {
		Enhancer enhancer = new Enhancer();
		//enhancer.setClassLoader(Thread.currentThread().getContextClassLoader());
		enhancer.setSuperclass(supperClass);
		enhancer.setCallback(new Interceptor());
		enhancer.setUseCache(false);
		Object dest = enhancer.create();
		//logger.info(dest.toString());
		return dest;
	}
	
	@Override
	public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return null;
//		
//		boolean proceeded = false;
//		Object result = null;
//		Set<String> interceptors = null;
//		PostValidate postValidateAnn = method.getAnnotation(PostValidate.class);
//		if (postValidateAnn != null) {
//			interceptors = new LinkedHashSet<String>();
//			String postValidatorName = postValidateAnn.value();
//			if (!Utils.hasLength(postValidatorName))
//				postValidatorName = WebSettings.getValidator();
//			
//			if (Utils.hasLength(postValidatorName)) {
//				interceptors.add(postValidatorName);
//			}
//		}
//		
//		Handled handleAnnotation = method.getAnnotation(Handled.class);
//		if (handleAnnotation != null) {
//			if (interceptors == null)
//				interceptors = new LinkedHashSet<String>();
//			String[] handlerNames = handleAnnotation.value();
//			
//			if (!Utils.hasLength(handlerNames)) {
//				//经过cglib生成的子类，其父类才是
//				String className = SpringUtils.getBeanName(target.getClass());
//				handlerNames = new String[] { className + "_" + method.getName() };
//			}
//			
//			interceptors = Utils.arrayToLinkSet(interceptors, handlerNames);
//		}
//		
//		if (Utils.hasLength(interceptors)) {
//			JoinPoint_Cglib joinPoint_Cglib = new JoinPoint_Cglib(target, method, args, proxy);
//			for (String handlerName : interceptors) {
//				if (!Utils.hasLength(handlerName))
//					continue;
//				if (!joinPoint_Cglib.doNext)
//					break;
//				Object bean = SpringUtils.bean(handlerName);
//				if (bean != null && bean instanceof Handler) {
//					Handler handler = (Handler) bean;
//					result = handler.proceed(joinPoint_Cglib);
//					proceeded = true;
//				}
//			}
//			if (joinPoint_Cglib.doNext && !joinPoint_Cglib.invoked) {
//				return proxy.invokeSuper(target, args);
//			}
//		}
//		
//		if (!proceeded)
//			result = proxy.invokeSuper(target, args);
//		return result;
	}
}
