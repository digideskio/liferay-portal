/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.messageboards.util.comparator;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portlet.messageboards.model.MBThread;

import java.util.Date;

/**
 * <a href="ThreadLastPostDateComparator.java.html"><b><i>View Source</i></b>
 * </a>
 *
 * @author Brian Wing Shun Chan
 */
public class ThreadLastPostDateComparator extends OrderByComparator {

	public static String ORDER_BY_ASC = "lastPostDate ASC, threadId ASC";

	public static String ORDER_BY_DESC = "lastPostDate DESC, threadId DESC";

	public static String[] ORDER_BY_FIELDS = {"lastPostDate", "threadId"};

	public ThreadLastPostDateComparator() {
		this(false);
	}

	public ThreadLastPostDateComparator(boolean asc) {
		_asc = asc;
	}

	public int compare(Object obj1, Object obj2) {
		MBThread thread1 = (MBThread)obj1;
		MBThread thread2 = (MBThread)obj2;

		Date lastPostDate1 = thread1.getLastPostDate();
		Date lastPostDate2 = thread2.getLastPostDate();

		boolean ignoreMilliseconds = false;

		DB db = DBFactoryUtil.getDB();

		if (!db.isSupportsDateMilliseconds()) {
			ignoreMilliseconds = true;
		}

		int value = DateUtil.compareTo(
			lastPostDate1, lastPostDate2, ignoreMilliseconds);

		if (value == 0) {
			if (thread1.getThreadId() < thread2.getThreadId()) {
				value = -1;
			}
			else if (thread1.getThreadId() > thread2.getThreadId()) {
				value = 1;
			}
		}

		if (_asc) {
			return value;
		}
		else {
			return -value;
		}
	}

	public String getOrderBy() {
		if (_asc) {
			return ORDER_BY_ASC;
		}
		else {
			return ORDER_BY_DESC;
		}
	}

	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	public boolean isAscending() {
		return _asc;
	}

	private boolean _asc;

}