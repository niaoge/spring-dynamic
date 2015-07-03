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

import static com.helpinput.core.Consts.DOT;
import static com.helpinput.core.Consts.NODE_LINDE;
import static com.helpinput.core.Consts.NODE_ROOT;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;

import com.helpinput.maps.RequestValues;
import com.helpinput.maps.Values;

public class Utils extends LoggerBase {
	
	public static Class<?>[] EmptyClasses = {};
	public static Object[] EmptyObjects = {};
	
	public static class Temp {
		
		public String value = null;
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	public static boolean hasLength(Map<String, Object> map, String key, Temp temp) {
		if (temp == null)
			temp = new Temp();
		
		if (Utils.hasLength(map)) {
			Object value = map.get(key);
			if (value != null) {
				temp.value = value.toString();
				return Utils.hasLength(temp.value);
			}
		}
		return false;
	}
	
	public static boolean hasLength(String str, Temp temp) {
		if (temp == null)
			temp = new Temp();
		if (str != null) {
			temp.value = str.trim();
			return temp.value.length() > 0;
		}
		return false;
	}
	
	public static boolean hasLength(Object str, Temp temp) {
		if (temp == null)
			temp = new Temp();
		if (str != null) {
			temp.value = str.toString().trim();
			return temp.value.length() > 0;
		}
		return false;
	}
	
	public static boolean hasLength(Map<String, Object> map, Object key) {
		if (Utils.hasLength(map)) {
			Object value = map.get(key);
			return hasLength(value);
		}
		return false;
	}
	
	public static String deleteEnd(String source, String end) {
		if (hasLength(source) && hasLength(end) && source.endsWith(end)) {
			return source.substring(1, source.length() - end.length() + 1);
		}
		return source;
		
	}
	
	public static String beanNamFormFile(String fileName) {
		if (Utils.hasLength(fileName)) {
			int lastIndex = fileName.lastIndexOf('/');
			if (lastIndex > -1) {
				fileName = fileName.substring(lastIndex + 1);
			}
			lastIndex = fileName.lastIndexOf(".");
			if (lastIndex > -1) {
				fileName = fileName.substring(lastIndex + 1);
			}
			fileName = fileName.replace("/", "");
			fileName = Utils.beanName(fileName);
		}
		return fileName;
	}
	
	public static String beanName(String className) {
		if (hasLength(className)) {
			//$$EnhancerByCGLIB$$
			int idx = className.indexOf("$$");
			if (idx > -1)
				className = className.substring(0, idx);
			idx = className.lastIndexOf('.');
			if (idx > -1)
				className = className.substring(idx + 1, className.length());
			className = className.substring(0, 1).toLowerCase() + className.substring(1, className.length());
		}
		return className;
	}
	
	public static String beanName(final File file) {
		return beanNamFormFile(file.getName());
	}
	
	public static boolean hasLength(final String str) {
		return (str != null && str.length() > 0);
	}
	
	public static boolean hasLength(final Boolean value) {
		return (value != null && value);
	}
	
	public static boolean hasLength(final Object[] objs) {
		return (objs != null && objs.length > 0);
	}
	
	public static boolean hasLength(final Map<?, ?> map) {
		return (map != null && map.size() > 0);
	}
	
	public static boolean hasLength(final Collection<?> coll) {
		return (coll != null && coll.size() > 0);
	}
	
	//	public static boolean hasLength(final Long value) {
	//		return (value != null && value > 0L);
	//	}
	//	
	//	public static boolean hasLength(final Integer value) {
	//		return (value != null && value > 0);
	//	}
	
	public static boolean hasLength(final Number value) {
		return (value != null && value.floatValue() > 0);
	}
	
	public static boolean hasLength(final Object value) {
		if (value == null)
			return false;
		if (value instanceof String)
			return hasLength((String) value);
		if (value instanceof Boolean)
			return hasLength((Boolean) value);
		if (value instanceof Object[])
			return hasLength((Object[]) value);
		if (value instanceof Map)
			return hasLength((Map<?, ?>) value);
		if (value instanceof Collection)
			return hasLength((Collection<?>) value);
		if (value instanceof Number)
			return hasLength((Number) value);
		return hasLength(value.toString());
	}
	
	//	public static boolean hasLength(Object value) {
	//		boolean result = (value != null);
	//		if (result) {
	//			if (value instanceof String)
	//				result = hasLength((String) value);
	//			else if (value instanceof Boolean)
	//				result = hasLength((Boolean) value);
	//			else if (value instanceof Object[])
	//				result = hasLength((Object[]) value);
	//			else if (value instanceof Map<?, ?>)
	//				result = hasLength((Map<?, ?>) value);
	//			else if (value instanceof Collection<?>)
	//				result = hasLength((Collection<?>) value);
	//			else if (value instanceof Long)
	//				result = hasLength((Long) value);
	//			else if (value instanceof Integer)
	//				result = hasLength((Integer) value);
	//		}
	//		return result;
	//	}
	
	public static HashSet<String> stringToHash(String source, String split) {
		HashSet<String> hashset = new HashSet<String>();
		String[] fieldNames = source.split(split);
		for (String str : fieldNames) {
			hashset.add(str.trim());
		}
		return hashset;
	}
	
	public static <T> T newInstance(Class<T> classType, Object... args) {
		if (!hasLength(args)) {
			try {
				return classType.newInstance();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
		Constructor<?> cst = findConstructor(classType, args);
		if (cst != null) {
			try {
				@SuppressWarnings("unchecked")
				T instance = (T) cst.newInstance(args);
				return instance;
			}
			catch (SecurityException e) {
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Object caller, String className, Object... args) {
		try {
			ClassLoader loader = (caller instanceof ClassLoader) ? (ClassLoader) caller : caller.getClass()
					.getClassLoader();
			return (T) newInstance(loader.loadClass(className), args);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Java文件操作 获取文件扩展名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf(DOT);
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}
	
	/**
	 * Java文件操作 获取不带扩展名的文件名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf(DOT);
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
	
	public static String treeName(String fileName, int node) {
		StringBuffer nodeSb = new StringBuffer();
		if (node > 0) {
			nodeSb.append(NODE_ROOT);
			for (int i = 0; i < node; i++) {
				nodeSb.append(NODE_LINDE);
			}
		}
		nodeSb.append(fileName);
		return nodeSb.toString();
	}
	
	public static Long parseLong(String s, Long min) {
		if (hasLength(s)) {
			Long value = Long.parseLong(s);
			if (min != null)
				value = value < min ? min : value;
			return value;
		}
		return min;
	}
	
	public static Integer parseInt(String s, Integer min) {
		if (hasLength(s)) {
			int value = Integer.parseInt(s);
			if (min != null)
				value = value < min ? min : value;
			return value;
		}
		return min;
	}
	
	public static Map<String, Object> map(String[] keys, Object[] values, Map<String, Object> dest) {
		if (Utils.hasLength(keys) && Utils.hasLength(values) && (keys.length == values.length)) {
			for (int i = 0; i < values.length; i++)
				dest.put(keys[i], values[i]);
		}
		return dest;
	}
	
	public static Values createValues() {
		return new RequestValues();
	}
	
	public static Map<String, Object> map(String[] keys, Object[] values) {
		Map<String, Object> map = createValues();
		return map(keys, values, map);
	}
	
	public static Map<String, Object> map(String key, Object[] values) {
		return map(strings(key), values);
	}
	
	public static Map<String, Object> map(String[] keys, Map<String, Object> source, Map<String, Object> dest) {
		if (Utils.hasLength(keys) && Utils.hasLength(source)) {
			for (String key : keys) {
				dest.put(key, source.get(key));
			}
		}
		return dest;
	}
	
	public static Map<String, Object> map(String[] keys, Map<String, Object> source) {
		RequestValues map = new RequestValues();
		return map(keys, source, map);
	}
	
	public static Map<String, Object> map(Map<String, Object> source, Map<String, Object> dest) {
		if (dest == null)
			dest = createValues();
		if (hasLength(source))
			dest.putAll(source);
		return dest;
	}
	
	public static Values map(Map<String, Object> source) {
		Values dest = createValues();
		map(source, dest);
		return dest;
	}
	
	public static Values map() {
		return createValues();
	}
	
	public static String[] strings(String... strs) {
		return strs;
	}
	
	public static Object[] objects(Object... objs) {
		return objs;
	}
	
	public static Class<?>[] classes(Class<?>... clzes) {
		return clzes;
	}
	
	public static Object[] objects(String[] keys, Map<String, Object> map) {
		Object[] dest = new Object[keys.length];
		for (int i = 0; i < dest.length; i++) {
			String key = keys[i];
			dest[i] = map.get(key);
		}
		return dest;
	}
	
	public static Map<String, Object> map(String key, Map<String, Object> values) {
		return map(strings(key), values);
	}
	
	public static Map<String, Object> map(String key, Object value) {
		return map(strings(key), objects(value));
	}
	
	public static Map<String, Object> map(String key, Object value, Map<String, Object> dest) {
		return map(strings(key), objects(value), dest);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T mapValue(Map<String, Object> map, String key, Class<T> type) {
		Object result = map.get(key);
		if (type == String.class) {
			if (result != null) {
				result = ((String) result).trim();
			}
			else
				result = "";
		}
		return (T) result;
	}
	
	public static Class<?> primitiveWrap(Class<?> clz) {
		//int, double, float, long, short, boolean, byte, char， void.
		Class<?> newClass = null;
		if (clz == boolean.class)
			newClass = Boolean.class;
		else if (clz == int.class)
			newClass = Integer.class;
		else if (clz == double.class)
			newClass = Double.class;
		else if (clz == float.class)
			newClass = Float.class;
		else if (clz == long.class)
			newClass = Long.class;
		else if (clz == short.class)
			newClass = Short.class;
		else if (clz == byte.class)
			newClass = Byte.class;
		else if (clz == char.class)
			newClass = Character.class;
		return newClass;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Member> T setAccess(final Member theMember) {
		if (theMember instanceof Method) {
			Method real = (Method) theMember;
			if (!real.isAccessible())
				real.setAccessible(true);
		}
		else if (theMember instanceof Constructor) {
			@SuppressWarnings("rawtypes")
			Constructor real = (Constructor) theMember;
			if (!real.isAccessible())
				real.setAccessible(true);
		}
		else if (theMember instanceof Field) {
			Field real = (Field) theMember;
			if (!real.isAccessible())
				real.setAccessible(true);
		}
		return (T) theMember;
	}
	
	@SuppressWarnings("rawtypes")
	public static Member findMember(Member[] methods, Class<?> targetClass, String methodName, Class<?>[] argsClasses) {
		for (Member member : methods) {
			if (!hasLength(methodName) || member.getName().equals(methodName)) {
				
				if (!hasLength(argsClasses))
					return  setAccess(member);
				
				Class<?>[] paramTypes;
				
				if (member instanceof Method)
					paramTypes = ((Method) member).getParameterTypes();
				else
					paramTypes = ((Constructor) member).getParameterTypes();
				
				if (!hasLength(paramTypes) && !hasLength(argsClasses))
					return setAccess(member);
				
				
				if (argsClasses.length <= paramTypes.length) {
					for (int i = 0; i < argsClasses.length; i++) {
						if (argsClasses[i] == null)
							continue;
						
						if (paramTypes[i].isPrimitive()) {
							if (!primitiveWrap(paramTypes[i]).isAssignableFrom(argsClasses[i]))
								break;
						}
						else if (!paramTypes[i].isAssignableFrom(argsClasses[i]))
							break;
						
						if (i == argsClasses.length - 1) {
							return setAccess(member);
						}
					}
				}
			}
		}
		Class<?> superClass;
		if ((superClass = targetClass.getSuperclass()) != null) {
			return findMember(superClass.getDeclaredMethods(), superClass, methodName, argsClasses);
		}
		return null;
	}
	
	public static Method findMethod(Object target, String methodName, Object... args) {
		Class<?> targetClass = getClass(target);
		Class<?>[] argsClasses = getArgClasses(args);
		Method[] methods = targetClass.getDeclaredMethods();
		return (Method) findMember(methods, targetClass, methodName, argsClasses);
	}
	
	public static Class<?>[] getArgClasses(Object... args) {
		Class<?>[] argsClasses;
		if (Utils.hasLength(args)) {
			argsClasses = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					argsClasses[i] = getClass(args[i]);
				}
			}
		}
		else
			argsClasses = EmptyClasses;
		return argsClasses;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Constructor<?> findConstructor(Object target, Object... args) {
		Class<?> targetClass = getClass(target);
		Class<?>[] agrClasses = getArgClasses(args);
		Constructor<?>[] constructors = targetClass.getDeclaredConstructors();
		return (Constructor<T>) findMember(constructors, targetClass, null, agrClasses);
	}
	
	public static <T> T listGet0(List<T> list) {
		if (hasLength(list))
			return list.get(0);
		return null;
	}
	
	public static Class<?> getClass(Object target) {
		return (target instanceof Class ? (Class<?>) target : target.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T InvokedMethod(Object target, Method method, Object... args) {
		if (method != null)
			try {
				return (T) method.invoke(target, args);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	public static <T> T InvokedMethod(Object target, String methodName, Object... args) {
		Method method = findMethod(target, methodName, args);
		return InvokedMethod(target, method, args);
	}
	
	public static Field findField(Object target, String fieldName) {
		Class<?> targetClass = getClass(target);
		Field theField;
		try {
			theField = targetClass.getDeclaredField(fieldName);
			return setAccess(theField);
		}
		catch (NoSuchFieldException e) {
			if (targetClass.getSuperclass() != null)
				return findField(targetClass.getSuperclass(), fieldName);
			else
				return null;
		}
	}
	
	public static Field findField(Object obj, String fieldName, Class<?> type) {
		if (obj == null)
			return null;
		Field theField = findField(obj, fieldName);
		if (isFieldType(theField, type))
			return theField;
		return null;
	}
	
	public static void object2Buffer(Object o, Class<?> targetClass, StringBuffer sb) {
		Field[] fields = targetClass.getDeclaredFields();
		for (Field field : fields) {
			Object value = getFieldValue(o, field);
			if (value != null) {
				String fieldName = field.getName();
				if (!fieldName.equals("descriptors")) {
					sb.append(field.getName() + "=" + value);
					sb.append(" , ");
					sb.append(" \n");
				}
			}
		}
		
		Class<?> supClass = targetClass.getSuperclass();
		if (supClass != null)
			object2Buffer(o, supClass, sb);
	}
	
	public static void printObject(Object o) {
		StringBuffer sb = new StringBuffer();
		Class<?> targetClass = null;
		if (o != null)
			targetClass = o.getClass();
		else {
			logger.info("" + null);
			return;
		}
		sb.append("class:" + o.getClass() + "@" + o.hashCode() + ";\n");
		object2Buffer(o, targetClass, sb);
		logger.info(sb.toString());
	}
	
	public static <T> T getFieldValue(Object target, String fieldName) {
		Field theField = findField(target, fieldName);
		return getFieldValue(target, theField);
	}
	
	/*
	 * public static <T> T getMethodAnnotation(Class<?> clz,Class<T> ann){
	 * clz.get }
	 */
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object target, Field theField) {
		if (theField != null) {
			setAccess(theField);
			try {
				return (T) theField.get(target);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void setFieldValue(Object target, Field theField, Object value) {
		if (theField != null) {
			setAccess(theField);
			try {
				Object newValue = ConvertUtils.convert(value, theField.getType());
				theField.set(target, newValue);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info(MessageFormat.format("convert error:{0},{1},{2}", target, theField, value));
			}
		}
	}
	
	public static void setFieldValue(Object target, String fieldName, Object value) {
		Field theField = findField(target, fieldName);
		if (theField != null) {
			setFieldValue(target, theField, value);
		}
	}
	
	public static String objectsToString(Object... args) {
		StringBuffer sb = new StringBuffer();
		for (Object object : args) {
			sb.append(object).append(", ");
		}
		return sb.toString();
	}
	
	public static String objectsClass(Object... args) {
		StringBuffer sb = new StringBuffer();
		for (Object object : args) {
			if (object == null)
				sb.append("null, ");
			else
				sb.append(object.getClass().getSimpleName() + ", ");
		}
		return sb.toString();
	}
	
	@SafeVarargs
	public static <T> Set<T> arrayToLinkSet(T... a) {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
		return arrayToLinkSet(result, a);
	}
	
	@SafeVarargs
	public static <T> Set<T> arrayToLinkSet(Set<T> set, T... a) {
		if (a != null) {
			for (T t : a) {
				set.add(t);
			}
		}
		return set;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Object o, Class<T> clz) {
		if (o == null)
			return null;
		if (clz.isInstance(o))
			return (T) o;
		return null;
	}
	
	public static boolean equals(final Object sourceV, final Object filterV) {
		if (sourceV == null) {
			return filterV == null;
		}
		
		if (filterV == null)
			return false;
		
		if ((sourceV instanceof Number)) {
			if (!(filterV instanceof Number))
				return false;
			
			Number v1 = (Number) sourceV;
			Number v2 = (Number) filterV;
			return v1.floatValue() == v2.floatValue();
		}
		return sourceV.equals(filterV);
	}
	
	public static boolean isFieldType(Field field, Class<?> clz) {
		if (field != null) {
			Class<?> type = field.getType();
			if (type.isAssignableFrom(clz)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, Class<? extends T> targetType) {
		return (T) ConvertUtils.convert(value, targetType);
	}
	
	public static void assertTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(final Map<?, ?> map, Object key) {
		return (T) map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(final Map<?, ?> map, Object key, Object defaultValue) {
		Object o = map.get(key);
		if (o == null)
			o = defaultValue;
		return (T) o;
	}
	
	/**
	 * 只有在脚本中才能使用，
	 */
	public static Object put(final Map<String, Object> map, Object key, Object value) {
		if (key != null) {
			Utils.assertTrue((key instanceof String), "这个方法在脚本中由编译前预先转换成String,如果没有转换，请手工转换");
		}
		return map.put((String) key, value);
	}
	
	public static int replaceWholeWord(final StringBuilder sb, final int begin, final String source,
										final String oldStr, final String newStr) {
		int lastb = begin;
		int olen = oldStr.length();
		int sLen = source.length();
		
		int idx = source.indexOf(oldStr, lastb);
		
		if (idx > -1) {
			boolean needAppend = false;
			sb.append(source, lastb, idx);
			lastb = idx + olen;
			
			if ((idx == 0 || !Character.isJavaIdentifierPart(source.charAt(idx - 1)))) {
				if (idx + olen >= sLen || !Character.isJavaIdentifierPart(source.charAt(idx + olen))) {
					needAppend = true;
					sb.append(newStr);
				}
			}
			
			if (!needAppend)
				sb.append(source, idx, idx + olen);
			lastb = replaceWholeWord(sb, lastb, source, oldStr, newStr);
		}
		else
			sb.append(source, lastb, sLen);
		
		return lastb;
	}
	
	public static void replaceWholeWord(final StringBuilder sb, final int begin, final String source,
										final String oldStr, final String newStr, char... quoters) {
		boolean replaced = false;
		if (quoters.length > 0) {
			int lastb = begin;
			int quoterIdx = source.indexOf(quoters[0]); //7
			if (quoterIdx > -1) {
				replaceWholeWord(sb, lastb, source.substring(lastb, quoterIdx), oldStr, newStr);
				char rightQuoter = quoters.length > 1 ? quoters[1] : quoters[0];
				
				int lastQuoterIdx = source.indexOf(rightQuoter, quoterIdx + 1);
				
				while ((lastQuoterIdx > 0 && source.charAt(lastQuoterIdx - 1) == '\\')) {
					lastQuoterIdx = source.indexOf(rightQuoter, lastQuoterIdx + 1);
				}
				int end;
				
				if (lastQuoterIdx < 0) {
					end = source.length();
				}
				else
					end = lastQuoterIdx + 1;
				
				sb.append(source, quoterIdx, end);
				
				if (end < source.length()) {
					replaceWholeWord(sb, begin, source.substring(end), oldStr, newStr, quoters);
				}
				replaced = true;
			}
		}
		if (!replaced)
			replaceWholeWord(sb, begin, source, oldStr, newStr);
	}
}
