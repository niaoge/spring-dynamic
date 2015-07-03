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
 *@Date: 2015-6-10
 */
package com.helpinput.core;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 转换器 1:将JavaBean 转换成Map、JSONObject 2:将JSONObject 转换成Map
 * 
 */
public class MapConvertor {
	/**
	 * 将javaBean转换成Map
	 * 
	 * @param javaBean
	 *            javaBean
	 * @return Map对象
	 */
	public static Map<String, Object> toMap(Object javaBean) {
		Map<String, Object> result = new HashMap<String, Object>();
		Method[] methods = javaBean.getClass().getDeclaredMethods();
		
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("get")) {
					String field = method.getName();
					field = field.substring(field.indexOf("get") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					
					Object value = method.invoke(javaBean, (Object[]) null);
					result.put(field, null == value ? "" : value.toString());
				}
			}
			catch (Exception e) {
			}
		}
		
		return result;
	}
	
	/**
	 * 将json对象转换成Map
	 * 
	 * @param jsonObject
	 *            json对象
	 * @return Map对象
	 * @throws JSONException
	 */
	public static Map<String, Object> toMap(JSONObject jsonObject) throws JSONException {
		Map<String, Object> result = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = jsonObject.keys();
		String key = null;
		String value = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = jsonObject.getString(key);
			result.put(key, value);
		}
		return result;
	}
	
	/**
	 * 将javaBean转换成JSONObject
	 * 
	 * @param bean
	 *            javaBean
	 * @return json对象
	 */
	public static JSONObject toJSON(Object bean) {
		return new JSONObject(toMap(bean));
	}
	
	public static String methodNameGetFieldName(String methodName) {
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toLowerCase(methodName.charAt(3)));
		sb.append(methodName.substring(4));
		return sb.toString();
	}
	
	/**
	 * 将map转换成Javabean
	 * 
	 * @param javabean
	 *            javaBean
	 * @param values
	 *            map数据
	 */
	public static Object toJavaBean(Object javabean, Map<?, ?> data) {
		Method[] methods = javabean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("set")) {
					Class<?>[] parmaTypes = method.getParameterTypes();
					if (parmaTypes.length == 1) {
						String fieldName = methodNameGetFieldName(method.getName());
						Object value = Utils.convert(data.get(fieldName), parmaTypes[0]);
						method.invoke(javabean, value);
						//					String field = method.getName();
						//					field = field.substring(field.indexOf("set") + 3);
						//					field = field.toLowerCase().charAt(0) + field.substring(1);
						//					method.invoke(javabean, new Object[] { data.get(field) });
					}
				}
			}
			catch (Exception e) {
			}
		}
		return javabean;
	}
	
	public static Object toJavaBeanNotClean(Object javabean, Map<?, ?> data) {
		Method[] methods = javabean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("set")) {
					String fieldName = methodNameGetFieldName(method.getName());
					Object object = data.get(fieldName);
					if (object != null)
						method.invoke(javabean, object);
					//					String field = method.getName();
					//					field = field.substring(field.indexOf("set") + 3);
					//					field = field.toLowerCase().charAt(0) + field.substring(1);
					//					method.invoke(javabean, new Object[] { data.get(field) });
				}
			}
			catch (Exception e) {
			}
		}
		return javabean;
	}
	
	public static boolean checkHasFieldName(Object javabean, String fieldName) {
		Method[] methods = javabean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if (method.getName().startsWith("set")) {
					String scanedFieldName = methodNameGetFieldName(method.getName());
					if (scanedFieldName.equals(fieldName))
						return true;
				}
			}
			catch (Exception e) {
			}
		}
		return false;
	}
	
	/**
	 * 将javaBean转换成JSONObject
	 * 
	 * @param bean
	 *            javaBean
	 * @return json对象
	 * @throws ParseException
	 *             json解析异常
	 * @throws JSONException
	 */
	public static void toJavaBean(Object javabean, String data) throws ParseException, JSONException {
		JSONObject jsonObject = new JSONObject(data);
		Map<String, Object> datas = toMap(jsonObject);
		toJavaBean(javabean, datas);
	}
}
