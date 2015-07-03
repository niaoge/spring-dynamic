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

import java.util.regex.Pattern;

import com.github.javaparser.ast.CompilationUnit;


// 上一次更新的脚本，前一个string 是path,后一个路径是beanName,用于释放不用的bean;
class BeanInfo {
	
	
	String fileName=null;
	String scanName=null;
	String importName=null;
	
	String beanName=null;
	String actionName=null;
	String relativePath=null;
	String packageName=null;
	Long lastModified=0L;
	CompilationUnit cu=null;
	Pattern referencWrapPt=null;
	Class<?> beanClass =null;
	
	boolean needParse=true;
	boolean isUpdate=false;
	boolean scaned=false;
	boolean isNew=false;
	boolean isInterface =false;
	
	BeanInfo(String fileName, String relativePath, Long lastModified) {
		this.fileName = fileName;
		this.relativePath = relativePath;
		this.lastModified = lastModified;
	}
}