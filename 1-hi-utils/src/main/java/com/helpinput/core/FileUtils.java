/* Copyright 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. */

/** @Author: niaoge(Zhengsheng Xia)
 * @Email 78493244@qq.com
 * @Date: 2015-6-10 */
package com.helpinput.core;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileUtils {
	
	/** 利用第三方开源包cpdetector获取文件编码格式
	 * 
	 * @param path
	 *            要判断文件编码格式的源文件的路径
	 * @author huanglei
	 * @version 2012-7-12 14:05 */
	public static String getFileEncode(File f) {
		/*
		 * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
		 * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
		 * JChardetFacade、ASCIIDetector、UnicodeDetector。
		 * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
		 * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
		 * cpDetector是基于统计学原理的，不保证完全正确。
		 */
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		/*
		 * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
		 * 指示是否显示探测过程的详细信息，为false不显示。
		 */
		detector.add(new ParsingDetector(false));
		/*
		 * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
		 * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
		 * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
		 */
		detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
		// ASCIIDetector用于ASCII编码测定
		detector.add(ASCIIDetector.getInstance());
		// UnicodeDetector用于Unicode家族编码的测定
		detector.add(UnicodeDetector.getInstance());
		java.nio.charset.Charset charset = null;
		try {
			charset = detector.detectCodepage(f.toURI().toURL());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null)
			return charset.name();
		else
			return null;
	}
	
	public static String getFileEncode(String path) {
		return getFileEncode(new File(path));
	}
	
	public static BufferedReader getFileBufferedReader(String path) throws UnsupportedEncodingException,
																	FileNotFoundException {
		String charsetName = getFileEncode(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charsetName));
		return reader;
	}
	
	public static void closeWithWarning(Closeable c) {
		if (c != null) {
			try {
				c.close();
			}
			catch (IOException e) {
				//LOG.warning("Caught exception during close(): " + e);
			}
		}
	}
	
	public static String getReaderText(BufferedReader reader) throws IOException {
		StringBuilder answer = new StringBuilder();
		// reading the content of the file within a char buffer
		// allow to keep the correct line endings
		char[] charBuffer = new char[8192];
		int nbCharRead /* = 0 */;
		try {
			reader.read(charBuffer);
			
			while ((nbCharRead = reader.read(charBuffer)) != -1) {
				// appends buffer
				answer.append(charBuffer, 0, nbCharRead);
			}
		}
		finally {
			closeWithWarning(reader);
			reader = null;
		}
		return answer.toString();
	}
	
	public static String getfileText(String path) throws IOException {
		BufferedReader reader = getFileBufferedReader(path);
		return getReaderText(reader);
	}
	
	public static String getFileExtension(File file) {
		String fileName = file.getName();
		int lastIdx = fileName.lastIndexOf(".");
		if (lastIdx != -1 && lastIdx != 0) {
			return fileName.substring(lastIdx + 1);
		}
		else {
			return "";
		}
	}	
	
}
