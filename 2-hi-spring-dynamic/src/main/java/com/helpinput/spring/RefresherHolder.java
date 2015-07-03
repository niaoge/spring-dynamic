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
 *@Date: 2015-7-3
 */
package com.helpinput.spring;

import java.util.LinkedList;
import java.util.List;

import com.helpinput.core.Utils;

public class RefresherHolder {
	protected static List<String> refresherNames = new LinkedList<>();
	
	public static void registerRefresher(String refresherName) {
		synchronized (refresherNames) {
			if (Utils.hasLength(refresherName) && !refresherNames.contains(refresherName)) {
				refresherNames.add(refresherName);
			}
		}
	}
	
	public static void removeRefresher(String refresherName) {
		synchronized (refresherNames) {
			if (Utils.hasLength(refresherName)) {
				refresherNames.remove(refresherName);
			}
		}
	}
	
}
