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
 *@Date: 2015-6-30
 */
package com.helpinput.spring;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.helpinput.core.Utils;
import org.slf4j.Logger;
import com.helpinput.core.LoggerBase;

public class ParserUtils {
	static Logger logger = LoggerBase.logger;
	
	public static String getPackageName(CompilationUnit cu) {
		return cu.getPackage().getName().toString();
	}
	
	public static ClassOrInterfaceDeclaration getClassName(CompilationUnit cu) {
		List<TypeDeclaration> types = cu.getTypes();
		if (Utils.hasLength(types)) {
			TypeDeclaration real = types.get(0);
			if (real instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration rl = (ClassOrInterfaceDeclaration) real;
				return rl;
			}
		}
		return null;
	}
	
	public static BodyDeclaration getBody(CompilationUnit cu) {
		List<Node> nodes = cu.getChildrenNodes();
		for (Node node : nodes) {
			if (node instanceof BodyDeclaration) {
				return (BodyDeclaration) node;
			}
		}
		return null;
	}
	
	public static List<FieldDeclaration> getFields(CompilationUnit cu) {
		List<FieldDeclaration> result = new ArrayList<>();
		BodyDeclaration body = getBody(cu);
		if (body != null) {
			List<Node> nodes = body.getChildrenNodes();
			for (Node node : nodes) {
				if (node instanceof FieldDeclaration) {
					result.add((FieldDeclaration) node);
				}
				
			}
		}
		return result;
	}
	
}
