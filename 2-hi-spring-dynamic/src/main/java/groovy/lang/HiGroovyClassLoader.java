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
package groovy.lang;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.slf4j.Logger;

import com.helpinput.core.LoggerBase;

public class HiGroovyClassLoader extends GroovyClassLoader {
	static Logger logger = LoggerBase.logger;
	private Map<String, byte[]> classBytes = new ConcurrentHashMap<>();
	public int compileCount = 0;
	
	public Class parseClass(GroovyCodeSource codeSource, boolean shouldCacheSource) throws CompilationFailedException {
		synchronized (sourceCache) {
			Class answer = sourceCache.get(codeSource.getName());
			if (answer != null)
				return answer;
			answer = doParseClass(codeSource);
			if (shouldCacheSource)
				sourceCache.put(codeSource.getName(), answer);
			return answer;
		}
	}
	
	private void validate(GroovyCodeSource codeSource) {
		if (codeSource.getFile() == null) {
			if (codeSource.getScriptText() == null) {
				throw new IllegalArgumentException("Script text to compile cannot be null!");
			}
		}
	}
	
	private void definePackage(String className) {
		int i = className.lastIndexOf('.');
		if (i != -1) {
			String pkgName = className.substring(0, i);
			java.lang.Package pkg = getPackage(pkgName);
			if (pkg == null) {
				definePackage(pkgName, null, null, null, null, null, null, null);
			}
		}
	}
	
	public static class MyClassCollector extends ClassCollector {
		private byte[] code = null;
		
		protected MyClassCollector(InnerLoader cl, CompilationUnit unit, SourceUnit su) {
			super(cl, unit, su);
		}
		
		public GroovyClassLoader getDefiningClassLoader() {
			return super.getDefiningClassLoader();
		}
		
		protected Class createClass(byte[] code, ClassNode classNode) {
			this.code = code;
			return super.createClass(code, classNode);
		}
	}
	
	protected MyClassCollector createMyCollector(CompilationUnit unit, SourceUnit su) {
		InnerLoader loader = AccessController.doPrivileged(new PrivilegedAction<InnerLoader>() {
			public InnerLoader run() {
				return new InnerLoader(HiGroovyClassLoader.this);
			}
		});
		return new MyClassCollector(loader, unit, su);
	}
	
	private String resolveName(String name) {
		if (name == null) {
			return name;
		}
		name = name.replace('.', '/');
		return name;
	}
	
	private Class doParseClass(GroovyCodeSource codeSource) {
		validate(codeSource);
		Class answer; // Was neither already loaded nor compiling, so compile and add to cache.
		CompilerConfiguration config = CompilerConfiguration.DEFAULT;
		
		CompilationUnit unit = createCompilationUnit(config, codeSource.getCodeSource());
		
		SourceUnit su = null;
		if (codeSource.getFile() == null) {
			su = unit.addSource(codeSource.getName(), codeSource.getScriptText());
		}
		else {
			su = unit.addSource(codeSource.getFile());
		}
		
		MyClassCollector collector = createMyCollector(unit, su);
		
		unit.setClassgenCallback(collector);
		int goalPhase = Phases.CLASS_GENERATION;
		if (config != null && config.getTargetDirectory() != null)
			goalPhase = Phases.OUTPUT;
		unit.compile(goalPhase);
		@SuppressWarnings("unchecked")
		List<Class<?>> classList = (List<Class<?>>) collector.getLoadedClasses();
		answer = classList.get(0);
		String mainClass = su.getAST().getMainClassName();
		for (Object o : collector.getLoadedClasses()) {
			Class clazz = (Class) o;
			String clazzName = clazz.getName();
			definePackage(clazzName);
			setClassCacheEntry(clazz);
			if (clazzName.equals(mainClass))
				answer = clazz;
		}
		synchronized (classBytes) {
			this.classBytes.put(resolveName(answer.getName()) + ".class", collector.code);
		}
		return answer;
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		synchronized (classBytes) {
			if (classBytes.containsKey(name)) {
				return new ByteArrayInputStream(classBytes.get(name));
			}
		}
		return super.getResourceAsStream(name);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.loadClass(name);
	}
	
	@Override
	public void clearCache() {
		super.clearCache();
		synchronized (classBytes) {
			classBytes.clear();
		}
	}
	
	public void clearNodeUsedCache(Set<String> usedclassNames, Set<String> usedPaths) {
		synchronized (classBytes) {
			//importName:com.tgb.web.UserController
			for (String path : classBytes.keySet()) {
				if (!usedPaths.contains(path)) {
					classBytes.remove(path);
				}
				
			}
		}
		
		synchronized (classCache) {
			for (String className : classCache.keySet()) {
				//com/tgb/web/UserController.class
				if (!usedclassNames.contains(className)) {
					logger.info("classCache:....................." + className);
					classCache.remove(className);
				}
				
			}
		}
		
	}
}
