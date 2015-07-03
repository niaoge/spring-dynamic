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

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMap<V>  extends ConcurrentHashMap<String, V>{
	private static final long serialVersionUID = 1L;
	
	public ConcurrentMap() {
		super();
	}	
	
	public ConcurrentMap(int initialCapacity) {
		super(initialCapacity);
	}	
	

	@Override
	public V get(Object key) {
		if (key==null)
			return null;
		return super.get(key);
	}
	
}
