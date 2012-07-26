/*
Copyright 2012 Aphid Mobile

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.aphidmobile.flip;

import android.view.MotionEvent;

import java.util.Arrays;

public class AphidTouchEvent {
	private static final int BASE_TOUCH_COUNT = 5;

	private static final Object pool_lock = new Object();
	private static AphidTouchEvent pooled_touch;
	private static int pool_size = 0;
	private static final int MAX_POOL_SIZE = 10;

	public int[] ids;
	public float[] screenXs;
	public float[] screenYs;
	public float[] clientXs;
	public float[] clientYs;

	public int actionIndex = 0;

	public int touchCount = 0;

	public int eventIdentifier;
	public long eventTime;
	public int eventPhase;

	AphidTouchEvent next;

	private AphidTouchEvent() {
		ids = new int[BASE_TOUCH_COUNT];
		screenXs = new float[BASE_TOUCH_COUNT];
		screenYs = new float[BASE_TOUCH_COUNT];
		clientXs = new float[BASE_TOUCH_COUNT];
		clientYs = new float[BASE_TOUCH_COUNT];

		clear();
	}

	public void clear() {
		Arrays.fill(ids, -1);
		Arrays.fill(screenXs, -1);
		Arrays.fill(screenYs, -1);
		Arrays.fill(clientXs, -1);
		Arrays.fill(clientYs, -1);
		actionIndex = 0;
		touchCount = -1;
		eventIdentifier = -1;
		eventTime = -1;
		eventPhase = -1;
		next = null;
	}

	private static AphidTouchEvent obtain() {
		AphidTouchEvent ret = null;

		synchronized (pool_lock) {
			ret = pooled_touch;
			if (pooled_touch != null) {
				pooled_touch = pooled_touch.next;
				pool_size--;
			}
		}

		if (ret == null)
			ret = new AphidTouchEvent();

		ret.next = null;

		return ret;
	}

	public static AphidTouchEvent createSingleTouchEvent(int[] viewLocation, MotionEvent event, int phase, int index) {
		AphidTouchEvent ret = obtain();
		ret.eventIdentifier = System.identityHashCode(event);
		ret.eventTime = event.getEventTime();
		ret.eventPhase = phase;
		ret.actionIndex = 0;
		ret.touchCount = 1;
		ret.ids[0] = event.getPointerId(index) + 1; //avoid zero id
		ret.clientXs[0] = event.getX(index);
		ret.clientYs[0] = event.getY(index);
		ret.screenXs[0] = ret.clientXs[0] + viewLocation[0];
		ret.screenYs[0] = ret.clientYs[0] + viewLocation[1];
		return ret;
	}

	public static AphidTouchEvent createMultiTouchEvent(int[] viewLocation, MotionEvent event, int phase) {
		AphidTouchEvent ret = obtain();

		ret.eventIdentifier = System.identityHashCode(event);
		ret.eventTime = event.getEventTime();
		ret.eventPhase = phase;
		ret.actionIndex = event.getActionIndex();

		ret.touchCount = event.getPointerCount();
		ret.ensureCapacity(ret.touchCount);

		for (int i = 0; i < ret.touchCount; i++) {
			ret.ids[i] = event.getPointerId(i) + 1; //avoid zero id
			ret.clientXs[i] = event.getX(i);
			ret.clientYs[i] = event.getY(i);
			ret.screenXs[i] = ret.clientXs[i] + viewLocation[0];
			ret.screenYs[i] = ret.clientYs[i] + viewLocation[1];
		}

		return ret;
	}

	public void recycle() {
		clear();
		synchronized (pool_lock) {
			if (pool_size < MAX_POOL_SIZE) {
				next = pooled_touch;
				pooled_touch = this;
				pool_size++;
			} else
				next = null;
		}
	}

	public void ensureCapacity(int touchCount) {
		if (touchCount > ids.length) {
			ids = new int[touchCount];
			screenXs = new float[touchCount];
			screenYs = new float[touchCount];
			clientXs = new float[touchCount];
			clientYs = new float[touchCount];
		}
	}

	public int getEventIdentifier() {
		return eventIdentifier;
	}

	public long getEventTime() {
		return eventTime;
	}

	public int getEventPhase() {
		return eventPhase;
	}

	public int[] getIds() {
		return ids;
	}

	public float[] getScreenXs() {
		return screenXs;
	}

	public float[] getScreenYs() {
		return screenYs;
	}

	public float[] getClientXs() {
		return clientXs;
	}

	public float[] getClientYs() {
		return clientYs;
	}

	public int getTouchCount() {
		return touchCount;
	}

	public int getActionIndex() {
		return actionIndex;
	}
}
