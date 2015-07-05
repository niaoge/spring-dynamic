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
 *@Date: 2015-7-6
 */
package com.helpinput.holder;

import java.util.LinkedList;
import java.util.List;

public class SafeHolder<T extends Object> {
	private List<T> holder = new LinkedList<>();
	
	public void register(T item) {
		synchronized (holder) {
			if (!holder.contains(item)) {
				holder.add(item);
			}
		}
	}
	
	public void remover(T item) {
		synchronized (holder) {
			holder.remove(item);
		}
	}
	
	public List<T> getList() {
		synchronized (holder) {
			return new LinkedList<>(holder);
		}
	}
	
}
