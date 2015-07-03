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
package com.helpinput.maps;

import java.util.Map;



public class OnceMap extends RequestValues {
	private static final long serialVersionUID = 1L;
	//private static Logger logger = LoggerBase.logger;
	
	private String ownKey;
	private Map<String, Object> parent;
	
	public OnceMap(String ownKey, Map<String, Object> parent) {
		this.ownKey = ownKey;
		this.parent = parent;
		parent.put(ownKey, this);
		
	}
	
	/* (non-Javadoc)
	 * @see com.helpinput.map.CaseInsensitiveHashMap#get(java.lang.Object)
	 */
	public Object get(Object key) {
		Object result = remove(key);
		if (this.size() == 0) {
			this.parent.remove(ownKey);
		}
		return result;
	}
	
}
