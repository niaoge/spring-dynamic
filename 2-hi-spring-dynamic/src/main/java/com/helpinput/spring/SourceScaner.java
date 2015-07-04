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

import static com.helpinput.core.Utils.hasLength;
import groovy.lang.GroovyCodeSource;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.VoidType;
import com.helpinput.core.FileUtils;
import com.helpinput.core.LoggerBase;
import com.helpinput.core.PathUtil;
import com.helpinput.core.Utils;
import com.helpinput.spring.support.ClassLoaderHolder;

class SourceScaner {
	private static Logger logger = LoggerBase.logger;
	
	private static String java_lang_Object = "Object";
	private static String java_lang_Object_ = "java.lang.Object";
	
	private static volatile Boolean scanning = false;
	private static final PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	Set<String> commonsTypes = new HashSet<>(Arrays.asList("String", "Integer", "int", "Boolean", "boolean"));
	
	/** 1String nameSpace ,2String Action,3String ActonClass; */
	//private static final ConcurrentMap<String, ConcurrentMap<String, String>> strutsNameSpaces = new ConcurrentHashMap<String, ConcurrentMap<String, String>>();
	
	private static final Map<String, Map<String, BeanInfo>> scanedBeanInfos = new ConcurrentHashMap<>();
	
	private List<String> dirs = null;
	private ApplicationContext context = null;
	
	public SourceScaner(List<String> dirs, ApplicationContext applicationContext) {
		this.dirs = dirs;
		this.context = applicationContext;
	}
	
	private void closeWithWarning(Closeable c) {
		if (c != null) {
			try {
				c.close();
			}
			catch (IOException e) {
				System.err.println("Caught exception during close(): " + e);
			}
		}
	}
	
	class BraceInfo {
		
		int leftBraceCount = 0;
		boolean findDouble = false;
		
		@Override
		public String toString() {
			return "leftBraceCount:" + leftBraceCount + ", findDouble:" + findDouble;
		}
	}
	
	private void initBeanInfosAndFileReaders() {
		for (Map<String, BeanInfo> beanInfosInPath : scanedBeanInfos.values()) {
			for (BeanInfo beanInfo : beanInfosInPath.values()) {
				beanInfo.scaned = false;
				beanInfo.needParse = true;
				beanInfo.isNew = false;
			}
		}
		
		if (!Utils.hasLength(this.dirs))
			return;
		
		String rootPath = new File(Thread.currentThread().getContextClassLoader().getResource("").toString())
				.getParentFile().getParent().substring(5);
		
		rootPath = rootPath.replace('\\', '/');
		
		for (String path : dirs) {
			path = path.replace('\\', '/');
			
			if (path.startsWith("/"))
				path = rootPath + path;
			path = "file:" + path;
			
			Map<String, BeanInfo> beanInfosInPath = scanedBeanInfos.get(path);
			if (beanInfosInPath == null) {
				beanInfosInPath = new ConcurrentHashMap<>();
				scanedBeanInfos.put(path, beanInfosInPath);
			}
			
			Resource[] resources;
			try {
				resources = resourcePatternResolver.getResources(path);
			}
			catch (Exception e) {
				//如果扫描出错，则保留之前的扫描
				if (hasLength(beanInfosInPath)) {
					for (BeanInfo beanInfo : beanInfosInPath.values()) {
						parserBeanInfoError(beanInfo);
					}
				}
				logger.info("read \"" + path + "\" error！", e);
				continue;
			}
			
			for (Resource resource : resources) {
				String fileName = null;
				BeanInfo beanInfo = null;
				String relativePath = null;
				
				try {
					File resourceFile = resource.getFile();
					fileName = resourceFile.toURI().getPath();
					relativePath = PathUtil.getRelativePath(rootPath, fileName);
					long newModified = resourceFile.lastModified();
					
					beanInfo = beanInfosInPath.get(relativePath);
					
					if (beanInfo != null) {
						beanInfo.needParse = (newModified != beanInfo.lastModified);
						beanInfo.isUpdate = true;
					}
					else {
						beanInfo = new BeanInfo(fileName, relativePath, newModified);
						beanInfosInPath.put(relativePath, beanInfo);
						beanInfo.needParse = true;
					}
					
					beanInfo.scaned = true;
					beanInfo.lastModified = newModified;
					
					if (beanInfo.needParse) {
						BufferedReader reader = null;
						try {
							reader = FileUtils.getFileBufferedReader(fileName);
							CompilationUnit cu = JavaParser.parse(reader, true);
							beanInfo.cu = cu;
							beanInfo.packageName = ParserUtils.getPackageName(cu);
							ClassOrInterfaceDeclaration real = ParserUtils.getClassName(cu);
							if (real != null) {
								beanInfo.isInterface = real.isInterface();
								beanInfo.scanName = real.getName();
								beanInfo.needParse = beanInfo.needParse && (!beanInfo.isInterface);
							}
							beanInfo.importName = beanInfo.packageName + "." + beanInfo.scanName;
							beanInfo.referencWrapPt = Pattern.compile("^.*\\W+" + beanInfo.scanName + "\\W+.*$");
						}
						finally {
							closeWithWarning(reader);
						}
					}
				}
				catch (IOException | ParseException e) {
					e.printStackTrace();
					if (beanInfo != null) {
						parserBeanInfoError(beanInfo);
						continue;
					}
				}
			}
		}
	}
	
	private void parserBeanInfoError(BeanInfo beanInfo) {
		/** 出错的beanInfo应该保留上次的正确的扫描 */
		if (beanInfo != null) {
			beanInfo.scaned = true;
			beanInfo.needParse = false;
			beanInfo.isNew = false;
		}
	}
	
	private void replaceList(LinkedList<Node> list, Node oldNode, Node newNode) {
		int idx = list.indexOf(oldNode);
		list.remove(idx);
		list.add(idx, newNode);
	}
	
	private String replaceType(String typeString, Node type, Map<String, String> scanedNames, Set<String> scanedImputs,
								Map<String, Pattern> scanedRefeWrap) {
		//!!!!!String ,Object ,Integer
		if (!commonsTypes.contains(typeString)) {
			String result;
			if (hasLength(result = hasScanedTypeName(typeString, scanedNames, scanedImputs))) {
				Utils.InvokedMethod(type, "setType", new ClassOrInterfaceType(java_lang_Object));
				return result;
			}
			else {
				String theScanName = containsInPattern(scanedRefeWrap, typeString);
				if (Utils.hasLength(theScanName)) {
					StringBuilder sb = new StringBuilder(100);
					Utils.replaceWholeWord(sb, 0, typeString, theScanName, java_lang_Object);
					Utils.InvokedMethod(type, "setType", new ClassOrInterfaceType(sb.toString()));
				}
			}
		}
		return null;
	}
	
	private String hasScanedTypeName(String typeString, Map<String, String> scanedNames, Set<String> scanedImputs) {
		String resullt = scanedNames.get(typeString);
		
		//(User)
		if (hasLength(resullt))
			return resullt;
		
		//(com.tgb.entity.User)
		if (scanedImputs.contains(typeString))
			return typeString;
		
		return null;
	}
	
	private void visit(Node node, List<ArrayInitializerExpr> arrayInitializerExprs, Map<String, String> scanedNames,
						Set<String> scanedImputs, Map<String, Pattern> scanedRefeWrap, final boolean inArgs,
						final String... fileName) {
		if (node == null) {
			return;
		}
		
		if (node instanceof CastExpr) {
			CastExpr real = (CastExpr) node;
			//(User)u -->(Object)u
			replaceType((real).getType().toString(), node, scanedNames, scanedImputs, scanedRefeWrap);
		}
		else if (node instanceof ImportDeclaration) {
			ImportDeclaration real = (ImportDeclaration) node;
			String importName = real.getName().toStringWithoutComments();
			if (scanedImputs.contains(importName)) {
				//import com.tgb.entity.User; -->>import java.lang.Object; not delete line
				real.setName(new NameExpr(java_lang_Object_));
			}
		}
		else if (node instanceof FieldDeclaration) {
			FieldDeclaration real = (FieldDeclaration) node;
			if (scanedNames.containsKey(real.getType().toString())) {
				//private UserDao userDao; -->> private java.lang.Object userDao;
				real.setType(new ClassOrInterfaceType(java_lang_Object));
			}
		}
		else if (node instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr real = (VariableDeclarationExpr) node;
			
			// User u = userDao.getUser(id); -->> Object u = userDao.getUser(id);
			replaceType(real.getType().toString(), real, scanedNames, scanedImputs, scanedRefeWrap);
			
			visitChildren(node, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, fileName);
		}
		else if (node instanceof Parameter) {
			Parameter real = (Parameter) node;
			String typeName = real.getType().toString();
			//public  void delUser(List<User>)  --->public  void delUser(List<Object>) 
			String classWholeName = replaceType(typeName, real, scanedNames, scanedImputs, scanedRefeWrap);
			
			if (Utils.hasLength(classWholeName)) {
				List<AnnotationExpr> annoList = real.getAnnotations();
				if (annoList == null) {
					annoList = new LinkedList<>();
					real.setAnnotations(annoList);
				}
				
				//public String addUser(User user, HttpServletRequest request) { -->  
				//public String addUser(@com.helpinput.spring.Dynamic("com.tgb.entity.User") Object user, HttpServletRequest request) {
				SingleMemberAnnotationExpr singleExpr = new SingleMemberAnnotationExpr(new NameExpr(
						"com.helpinput.spring.annotation.Dynamic"), new StringLiteralExpr(classWholeName));
				annoList.add(singleExpr);
			}
		}
		//		else if (node instanceof ExpressionStmt) {
		//			ExpressionStmt real = (ExpressionStmt) node;
		//			visitChildren(node, scanedNames, scanedImputs, scanedRefeWrap);
		//		}
		//		
		else if (node instanceof ReferenceType) {
			ReferenceType real = (ReferenceType) node;
			String typeString = real.getType().toString();
			//public User getUser(String id)  --> public Object getUser(String id) {
			replaceType(typeString, real, scanedNames, scanedImputs, scanedRefeWrap);
		}
		else if (node instanceof ObjectCreationExpr) {
			ObjectCreationExpr real = (ObjectCreationExpr) node;
			String typeNameTmp = real.getType().toString();
			String className = hasScanedTypeName(typeNameTmp, scanedNames, scanedImputs);
			
			
			if (hasLength(className)) {
				Expression expression = new FieldAccessExpr(new NameExpr("com.helpinput.core"), "Utils");
				List<Expression> args = new LinkedList<>();
				args.add(new ThisExpr());
				args.add(new StringLiteralExpr(className));
				
				List<Expression> oldArgs = real.getArgs();
				if (Utils.hasLength(oldArgs)) {
					for (Expression expr : oldArgs) {
						args.add(expr);
					}
				}
				
				MethodCallExpr forReplaceExpr = new MethodCallExpr(expression, "newInstance", args);
				
				Node parent = node.getParentNode();
				
				LinkedList<Node> parentList = (LinkedList<Node>) parent.getChildrenNodes();
				boolean isElise = false;
				
				if (parent instanceof ConditionalExpr) {
					ConditionalExpr realParent = (ConditionalExpr) parent;
					isElise = realParent.getElseExpr() == node;
				}
				
				replaceList(parentList, node, forReplaceExpr);
				forReplaceExpr.setParentNode(parent);
				
				if (parent instanceof VariableDeclarator) {
					((VariableDeclarator) parent).setInit(forReplaceExpr);
					visitChildren(forReplaceExpr, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap,
							fileName);
					return;
				}
				else if (parent instanceof MethodCallExpr) {
					MethodCallExpr realParent = ((MethodCallExpr) parent);
					if (!inArgs)
						//u =new User(...) -->>com.helpinput.core.Utils.newInstance(this,"com.tgb.entity.User",...)
						realParent.setScope(forReplaceExpr);
					else {
						@SuppressWarnings({ "rawtypes", "unchecked" })
						LinkedList<Node> parentArgs = (LinkedList) realParent.getArgs();
						
						//list.add(new User(...)) -->>list.add(com.helpinput.core.Utils.newInstance(this,"com.tgb.entity.User",...)) 
						replaceList(parentArgs, node, forReplaceExpr);
					}
					visit(forReplaceExpr, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, false,
							fileName);
					return;
				}
				else if (parent instanceof ExpressionStmt) {
					ExpressionStmt realParent = (ExpressionStmt) parent;
					realParent.setExpression(forReplaceExpr);
				}
				else if (parent instanceof ConditionalExpr) {
					ConditionalExpr realParent = (ConditionalExpr) parent;
					if (isElise)
						realParent.setElseExpr(forReplaceExpr);
					else
						realParent.setThenExpr(forReplaceExpr);
				}
				else if (parent instanceof ArrayInitializerExpr) {
					ArrayInitializerExpr realParent = (ArrayInitializerExpr) parent;
					@SuppressWarnings({ "rawtypes", "unchecked" })
					LinkedList<Node> parentArgs = (LinkedList) realParent.getValues();
					//{"Jimmy", new  User(), "Gougou", "Doggy"} 
					//-->"Jimmy", com.helpinput.core.Utils.newInstance(this,"com.tgb.entity.User",...), "Gougou", "Doggy"
					replaceList(parentArgs, node, forReplaceExpr);
				}
				
				visitChildren(forReplaceExpr, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap,
						fileName);
				return;
			}
			visitChildren(node, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, fileName);
		}
		else if (node instanceof MethodCallExpr) {
			MethodCallExpr real = (MethodCallExpr) node;
			List<Expression> args = real.getArgs();
			
			if (hasLength(args)) {
				for (int i = 0; i < args.size(); i++) {
					Expression expression = args.get(i);
					visit(expression, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, true, fileName);
				}
			}
			visitChildren(node, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, fileName);
		}
		
		else if (node instanceof ArrayInitializerExpr) {
			arrayInitializerExprs.add((ArrayInitializerExpr) node);
			visitChildren(node, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, fileName);
		}
		else if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration real = (ClassOrInterfaceDeclaration) node;
			List<ClassOrInterfaceType> implts = real.getImplements();
			if (hasLength(implts)) {
				for (int i = implts.size() - 1; i >= 0; i--) {
					ClassOrInterfaceType implt = implts.get(i);
					if (hasLength(hasScanedTypeName(implt.getName(), scanedNames, scanedImputs))) {
						implts.remove(i);
					}
				}
			}
			visitChildren(node, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, fileName);
		}
		else if (node instanceof QualifiedNameExpr) {
		}
		else if (node instanceof MarkerAnnotationExpr) {
		}
		else if (node instanceof VariableDeclaratorId) {
		}
		else if (node instanceof StringLiteralExpr) {
		}
		else if (node instanceof LineComment) {
		}
		else if (node instanceof BlockComment) {
		}
		else if (node instanceof NameExpr) {
		}
		else if (node instanceof ThisExpr) {
		}
		else if (node instanceof PrimitiveType) {
		}
		else if (node instanceof VoidType) {
		}
		else if (node instanceof FieldAccessExpr) {
		}
		
		else {
			visitChildren(node, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, fileName);
		}
	}
	
	private void visitChildren(Node node, List<ArrayInitializerExpr> arrayInitializerExprs,
								Map<String, String> scanedNames, Set<String> scanedImputs,
								Map<String, Pattern> scanedRefeWrap, final String... fileName) {
//		int level = 0;
//		Node parent = node.getParentNode();
//		while (parent != null) {
//			parent = parent.getParentNode();
//			level++;
//		}
//		if (level > 0 && fileName.length > 0 && fileName[0].endsWith("TeacherManager.java")) {
//			if (node instanceof ClassOrInterfaceDeclaration) {
//				ClassOrInterfaceDeclaration real = (ClassOrInterfaceDeclaration) node;
//			}
//		}
		
		List<Node> children = node.getChildrenNodes();
		if (Utils.hasLength(children)) {
			//不能使用 for (Node node2 : children) 
			for (int i = 0; i < children.size(); i++) {
				Node child = children.get(i);
				visit(child, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, false, fileName);
			}
		}
	}
	
	private String readFile(BeanInfo beanInfo, Map<String, String> scanedNames, Set<String> scanedImputs,
							Map<String, Pattern> scanedRefeWrap, final String... fileName) {
		//logger.info(beanInfo.cu.toString());
		List<ArrayInitializerExpr> arrayInitializerExprs = new LinkedList<>();
		visit(beanInfo.cu, arrayInitializerExprs, scanedNames, scanedImputs, scanedRefeWrap, false, fileName);
		String source = beanInfo.cu.toString();
		for (ArrayInitializerExpr arrayInitializerExpr : arrayInitializerExprs) {
			String arryExpress = arrayInitializerExpr.toString();
			
			StringBuilder sb = new StringBuilder(arryExpress.length());
			
			int idx = arryExpress.indexOf('{');
			int lastIdx = arryExpress.lastIndexOf('}');
			sb.append(arryExpress, 0, idx).append('[').append(arryExpress, idx + 1, lastIdx).append(']')
					.append(arryExpress, lastIdx + 1, arryExpress.length());
			source = source.replace(arryExpress, sb.toString());
		}
		return source;
		
	}
	
	private String containsInPattern(Map<String, Pattern> scanedRefeWrap, String input) {
		for (Entry<String, Pattern> entry : scanedRefeWrap.entrySet()) {
			if (entry.getValue().matcher(input).find()) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	private void scanAndCreateBeans() {
		if (scanning == true)
			return;
		
		scanning = true;
		try {
			initBeanInfosAndFileReaders();
			boolean changed = false;
			
			for (Map<String, BeanInfo> map : scanedBeanInfos.values()) {
				for (BeanInfo subInfo : map.values()) {
					if (subInfo.needParse || !subInfo.scaned) {
						changed = true;
						break;
					}
				}
				if (changed)
					break;
			}
			
			if (!changed)
				return;
			
			int size = 0;
			for (Map<String, BeanInfo> beans : scanedBeanInfos.values()) {
				size += beans.size();
			}
			
			Map<String, String> scanedNames = new HashMap<>(size);
			Set<String> scanedImputs = new HashSet<>(size);
			Map<String, Pattern> scanedRefeWrap = new HashMap<>(size);
			
			for (Map<String, BeanInfo> map : scanedBeanInfos.values()) {
				for (BeanInfo beanInfo : map.values()) {
					if (beanInfo.scaned) {
						scanedNames.put(beanInfo.scanName, beanInfo.importName);
						scanedImputs.add(beanInfo.importName);
						scanedRefeWrap.put(beanInfo.scanName, beanInfo.referencWrapPt);
					}
				}
			}
			
			DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) ((AbstractApplicationContext) context)
					.getBeanFactory();
			
			synchronized (dlbf) {
				for (Map<String, BeanInfo> beanInfosInPath : scanedBeanInfos.values()) {
					for (BeanInfo beanInfo : beanInfosInPath.values()) {
						File file = new File(beanInfo.fileName);
						if (beanInfo.needParse && file.exists()) {
							Class<?> clz = null;
							
							String scriptText;
							try {
								scriptText = readFile(beanInfo, scanedNames, scanedImputs, scanedRefeWrap,
										beanInfo.fileName);
							}
							catch (Exception e) {
								parserBeanInfoError(beanInfo);
								logger.info(beanInfo.fileName, e);
								continue;
							}
							
							//logger.info(scriptText);
							
							GroovyCodeSource groovyCodeSource = new GroovyCodeSource(scriptText, beanInfo.relativePath,
									"script/groovy");
							groovyCodeSource.setCachable(false);
							try {
								clz = ClassLoaderHolder.gcl.parseClass(groovyCodeSource);
								if (clz == null) {
									//如果parser出错，则保留之前的扫描
									parserBeanInfoError(beanInfo);
									continue;
								}
								//System.out.println(scriptText);
							}
							catch (Exception e) {
								//如果扫描出错，则保留之前的扫描
								parserBeanInfoError(beanInfo);
								System.err.println(scriptText);
								e.printStackTrace();
								continue;
							}
							
							scriptText = null;
							String beanName = null;
							
							BeanDefinitionBuilder builder = BeanRegister.createBuilder(clz, Interceptor.class);
							
							beanName = BeanRegister.registerBean(context, dlbf, builder, clz, null, null, beanInfo)
									.getBeanName();
							
							changed = true;
							beanInfo.cu = null;
							
							if (hasLength(beanName)) {
								beanInfo.beanName = beanName;
								beanInfo.beanClass = clz;
								beanInfo.scaned = true;
								beanInfo.needParse = false;
								beanInfo.isUpdate = false;
								beanInfo.isNew = true;
							}
						}
					}
				}
				List<Class<?>> removedClasses = removeNotScanedBean(dlbf);
				changed = changed || hasLength(removedClasses);
				
				if (changed) {
					ClassLoaderHolder.gcl.compileCount++;
					boolean needClearClassLoader = (ClassLoaderHolder.gcl.compileCount % 100) == 0;
					
					Map<Class<?>, ScanedType> scanedClasses = new HashMap<>(size + removedClasses.size());
					for (Class<?> deletedClass : removedClasses)
						scanedClasses.put(deletedClass, ScanedType.DELETED);
					
					Set<String> usedclassNames = needClearClassLoader ? new HashSet<String>(size) : null;
					Set<String> usedPaths = needClearClassLoader ? new HashSet<String>(size) : null;
					
					for (Map<String, BeanInfo> beans : scanedBeanInfos.values()) {
						for (BeanInfo beanInfo : beans.values()) {
							if (needClearClassLoader) {
								usedclassNames.add(beanInfo.importName);
								usedPaths.add(beanInfo.importName.replace('.', '/') + ".class");
							}
							if (beanInfo.beanClass != null)
								scanedClasses
										.put(beanInfo.beanClass, beanInfo.isNew ? ScanedType.NEW : ScanedType.SAME);
						}
					}
					
					BeanRegister.refreshContext(this.context, scanedClasses);
					
					if (needClearClassLoader)
						ClassLoaderHolder.gcl.clearNodeUsedCache(usedclassNames, usedPaths);
					
					dlbf.preInstantiateSingletons();
				}
			}
		}
		finally {
			scanning = false;
		}
	}
	
	private List<Class<?>> removeNotScanedBean(DefaultListableBeanFactory dlbf) {
		List<Class<?>> removedClasses = new LinkedList<Class<?>>();
		// 将没有扫描到bean删除
		Iterator<Entry<String, Map<String, BeanInfo>>> it = scanedBeanInfos.entrySet().iterator();
		while (it.hasNext()) {
			Map<String, BeanInfo> beanInfoInPath = it.next().getValue();
			if (beanInfoInPath != null) {
				Iterator<Entry<String, BeanInfo>> it2 = beanInfoInPath.entrySet().iterator();
				while (it2.hasNext()) {
					BeanInfo beanInfo = it2.next().getValue();
					if (beanInfo != null) {
						if (!beanInfo.scaned) {
							if (hasLength(beanInfo.relativePath)) {
								
								if (beanInfo.beanClass != null)
									removedClasses.add(beanInfo.beanClass);
								
								boolean removeBeanName = false;
								if (hasLength(beanInfo.beanName)) {
									removeBeanName = BeanRegister.removeBean(dlbf, beanInfo, null);
								}
								
								boolean removeClassName = false;
								if (hasLength(beanInfo.actionName)) {
									removeClassName = BeanRegister.removeBean(dlbf, beanInfo, null);
								}
								//将自身清除，以保持最新的状态
								if (removeBeanName || removeClassName) {
									it2.remove();
								}
							}
						}
					}
				}
				if (!hasLength(beanInfoInPath)) {
					it.remove();
				}
			}
		}
		return removedClasses;
	}
	
	public void scanSource() {
		scanAndCreateBeans();
	}
	
	public void stop() {
		
	}
	
}
