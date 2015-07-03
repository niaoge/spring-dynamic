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

import com.helpinput.core.Utils;

/**
 * Map,内装从数据库转换的数据,key:fieldName,object:values 有顺序，不区分大小写
 * 
 * @param <K>
 * */
public class ResponsValues extends CaseInsensitiveHashMap<Object> implements Values {
	private static final long serialVersionUID = 1L;

	public ResponsValues() {
		super();
	}
	
	public ResponsValues(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
	public Object set(Object key, Object value) {
		return Utils.put(this, key, value);
	}

	@Override
	public <T> T got(Object key, Object defaultValue) {
		return Utils.get(this,key, defaultValue);
	}

	@Override
	public <T> T got(Object key) {
		return Utils.get(this,key);
	}	
	
}
