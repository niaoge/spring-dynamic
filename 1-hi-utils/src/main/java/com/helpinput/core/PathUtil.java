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

import java.util.ArrayList ;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import static com.helpinput.core.LoggerBase.logger;


/**
 * 类 名: PathUtils 描 述: 对于路径问题处理的工具类 作 者: Vv 创 建： 2012-11-29 版 本：
 * 历 史: (版本) 作者 时间 注释
 */
public class PathUtil {
	public static final String PATH_OLD_SPLIT = "\\\\";
	public static final String PATH_OTHER_SPLIT = "//";
	public static final String PATH_SPLIT = "/";
	public static final String UPPER_PATH = "..";
	public static final String PATH_HTML = "http://";
	
	/**
	 * 
	 * 描 述：追加目录 作 者：Vv 历 史: (版本) 作者 时间 注释
	 * 
	 * @param parentPath
	 *            上级目录 如：D:/AA
	 * @param subPath
	 *            子目录 如：BB
	 * @return 以目录名结尾 如： D:/AA/BB
	 */
	public static String appendPath(String parentPath, String subPath) {
		
		if (StringUtils.isNotBlank(parentPath) && !parentPath.endsWith(PATH_SPLIT)) {
			parentPath += PATH_SPLIT;
		}
		if (StringUtils.isNotBlank(subPath)) {
			parentPath = parentPath + subPath;
		}
		
		return processPath(parentPath);
	}
	
	/**
	 * 
	 * 描 述：根据当前路径获取目标路径的相对路径（传入参数为绝对路径） 算法： 1.将路径分割为文件名的数组
	 * 2.循环当前目录（currentPath）的文件名数组，分别与目标路径（targetPath）的文件数组里的同一位置（索引值i相同）比较
	 * 2a.如果同一索引值对应的文件夹名相同，则表示存在相对路径，循环继续（continue），直到一索引值对应的文件夹名不相同时，跳出循环
	 * 2b.如果两个路径的起始部分不一致，则表示不存在相对路径，返回目标路径 3.根据2中循环结束时的索引值，获取相对路径文件名的集合并组成String
	 * 作 者：Vv 历 史: (版本) 作者 时间 注释
	 * 
	 * @param currentPath
	 * @param targetPath
	 * @return 存在相对路径，则返回相对路径；否则，返回目标路径
	 * @throws ProcessPathException
	 */
	public static String getRelativePath(String currentPath, String targetPath) {
		if (StringUtils.isBlank(currentPath) || StringUtils.isBlank(targetPath)) {
			logger.error("The currentPath or targetPath parameter is required and can't be null or blank.");
			return currentPath;
		}
		currentPath = processPath(currentPath);
		targetPath = processPath(targetPath);
		
		String[] cpNames = currentPath.split(PATH_SPLIT);
		String[] tpNames = targetPath.split(PATH_SPLIT);
		
		List<String> rpNames = new ArrayList<String>();
		
		/**
		 * 2.循环当前目录（currentPath）的文件名数组，分别与目标路径（targetPath）的文件数组里的同一位置（索引值i相同）比较
		 * 2a.如果同一索引值对应的文件夹名相同，则表示存在相对路径，循环继续（continue），直到一索引值对应的文件夹名不相同时，跳出循环
		 * 2b.如果两个路径的起始部分不一致，则表示不存在相对路径，返回目标路径
		 */
		int i = 0;
		for (i = 0; i < cpNames.length; i++) {
			if (i > tpNames.length - 1)
				break;
			
			if (cpNames[i].equals(tpNames[i])) {
				continue;
			}
			else if (i == 0) {// 如果currentPath与targetPath的起始目录不相同，则说明不存在相对路径
				break;
			}
			else {
				rpNames.add(UPPER_PATH);
				break;
			}
		}
		
		/**
		 * 3.根据2中循环结束时的索引值 "i"，获取相对路径文件名的集合并组成String
		 */
		for (int j = i; j < tpNames.length; j++) {
			rpNames.add(tpNames[j]);
		}
		
		String relativePath = "";
		for (String rp : rpNames) {
			relativePath = relativePath + rp + PATH_SPLIT;
		}
		
		return processPath(relativePath);
	}
	
	/**
	 * 描 述：处理路径问题 如：传入 ///localhost//uuzz/admin/main.action; 返回
	 * /localhost/uuzz/admin/main.action 作 者：Vv 历 史: (版本) 作者 时间 注释
	 * 
	 * @param path
	 * @return
	 */
	public static String processPath(String path) {
		path = path.replaceAll(PATH_OLD_SPLIT, PATH_SPLIT);
		if (path.endsWith(PATH_SPLIT)) {
			path = path.substring(0, path.length() - 1);
		}
		
		boolean isHttp = false;
		if (path.startsWith(PATH_HTML)) {
			isHttp = true;
			path = path.substring(PATH_HTML.length());
		}
		
		// 循环处理，将"//"替换为"/"
		while (path.contains(PATH_OTHER_SPLIT)) {
			path = path.replaceAll(PATH_OTHER_SPLIT, PATH_SPLIT);
		}
		
		if (isHttp) {
			path = PATH_HTML + path;
		}
		
		return path;
	}
}
