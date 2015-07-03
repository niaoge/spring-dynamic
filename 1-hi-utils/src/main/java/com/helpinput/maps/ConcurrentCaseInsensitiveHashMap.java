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

import org.slf4j.Logger;

import com.helpinput.core.LoggerBase;



public class ConcurrentCaseInsensitiveHashMap<V> extends ConcurrentMap<V> {
	private static final long serialVersionUID = 1L;
	public static Logger logger=LoggerBase.logger;

	private final ConcurrentMap<String> caseInsensitiveKeys;



	/**
	 * Create a new LinkedCaseInsensitiveMap for the default Locale.
	 * @see java.lang.String#toLowerCase()
	 */
	public ConcurrentCaseInsensitiveHashMap() {
		super();
		this.caseInsensitiveKeys = new ConcurrentMap<String>();
	}


	/**
	 * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
	 * with the given initial capacity and stores lower-case keys according
	 * to the default Locale.
	 * @param initialCapacity the initial capacity
	 * @see java.lang.String#toLowerCase()
	 */
	public ConcurrentCaseInsensitiveHashMap(int initialCapacity) {
		super(initialCapacity);
		this.caseInsensitiveKeys = new ConcurrentMap<String>();
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
			return super.get(this.caseInsensitiveKeys.get(convertKey((String)key)));
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


	/**
	 * Convert the given key to a case-insensitive key.
	 * <p>The default implementation converts the key
	 * to lower-case according to this Map's Locale.
	 * @param key the user-specified key
	 * @return the key to use for storing
	 * @see java.lang.String#toLowerCase(java.util.Locale)
	 */
	protected String convertKey(String key) {
		return key.toLowerCase();
	}

}