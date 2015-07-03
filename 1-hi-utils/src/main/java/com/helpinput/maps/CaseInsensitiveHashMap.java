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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class CaseInsensitiveHashMap<V> extends LinkedHashMap<String, V> {
	
	private static final long serialVersionUID = 1L;


	private final Map<String, String> caseInsensitiveKeys;

	private final Locale locale;



	public CaseInsensitiveHashMap() {
		this(null);
	}


	public CaseInsensitiveHashMap(Locale locale) {
		super();
		this.caseInsensitiveKeys = new HashMap<String, String>();
		this.locale = (locale != null ? locale : Locale.getDefault());
	}


	public CaseInsensitiveHashMap(int initialCapacity) {
		this(initialCapacity, null);
	}

	public CaseInsensitiveHashMap(int initialCapacity, Locale locale) {
		super(initialCapacity);
		this.caseInsensitiveKeys = new HashMap<String, String>(initialCapacity);
		this.locale = (locale != null ? locale : Locale.getDefault());
	}



	@Override
	public V put(String key, V value) {
		String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
		if (oldKey != null && !oldKey.equals(key)) {
			super.remove(oldKey);
		}
		return super.put(key, value);
	}


	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		if (map.isEmpty()) {
			return;
		}
		for (Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}


	@Override
	public boolean containsKey(Object key) {
		return (key instanceof String && this.caseInsensitiveKeys.containsKey(convertKey((String) key)));
	}


	@Override
	public V get(Object key) {
		if (key instanceof String) {
			return super.get(this.caseInsensitiveKeys.get(convertKey((String) key)));
		}
		else {
			return null;
		}
	}


	@Override
	public V remove(Object key) {
		if (key instanceof String ) {
			return super.remove(this.caseInsensitiveKeys.remove(convertKey((String) key)));
		}
		else {
			return null;
		}
	}


	@Override
	public void clear() {
		this.caseInsensitiveKeys.clear();
		super.clear();
	}



	protected String convertKey(String key) {
		return key.toLowerCase(this.locale);
	}

}