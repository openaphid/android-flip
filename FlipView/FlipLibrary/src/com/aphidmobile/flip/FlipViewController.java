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

import android.content.Context;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;
import com.aphidmobile.utils.AphidLog;
import junit.framework.Assert;

import java.util.LinkedList;

public class FlipViewController extends AdapterView<Adapter> {
	
	public static final int ORIENTATION_VERTICAL = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;

	private static final int MSG_SURFACE_CREATED = 1;
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == MSG_SURFACE_CREATED) {
				contentWidth = 0;
				contentHeight = 0;
				requestLayout();
				return true;
			}
			return false;
		}
	});

	private GLSurfaceView surfaceView;
	private FlipRenderer renderer;
	private FlipCards cards;

	private int contentWidth;
	private int contentHeight;
	
	private boolean orientationVertical;

	private boolean enableFlipAnimation = true;

	private boolean inFlipAnimation = false;

	//AdapterView Related
	private Adapter adapter;
	private DataSetObserver adapterDataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			View v = bufferedViews.get(bufferIndex);
			if (v != null) {
				for (int i = 0; i < adapter.getCount(); i++) {
					if (v.equals(adapter.getItem(i))) {
						adapterIndex = i;
						break;
					}
				}
			}
			reloadAllViews();
		}
	};

	private final LinkedList<View> bufferedViews = new LinkedList<View>();
	private final LinkedList<View> releasedViews = new LinkedList<View>(); //XXX: use a SparseArray to log the related view indices?
	private int bufferIndex = -1;
	private int adapterIndex = -1;
	private int sideBufferSize = 1;

	private float touchSlop;
	@SuppressWarnings("unused")
	private float maxVelocity;

	public FlipViewController(Context context) {
		this(context, true);
	}

	
	public FlipViewController(Context context, boolean orientationVertical) {
		super(context);
		ViewConfiguration configuration = ViewConfiguration.get(getContext());
		touchSlop = configuration.getScaledTouchSlop();
		maxVelocity = configuration.getScaledMaximumFlingVelocity();
		this.orientationVertical = orientationVertical;
		setupSurfaceView();
	}

	float getTouchSlop() {
		return touchSlop;
	}

	GLSurfaceView getSurfaceView() {
		return surfaceView;
	}

	FlipRenderer getRenderer() {
		return renderer;
	}

	int getContentWidth() {
		return contentWidth;
	}

	int getContentHeight() {
		return contentHeight;
	}

	public void setEnableFlipAnimation(boolean enable) {
		enableFlipAnimation = enable;
	}

	public void onResume() {
		surfaceView.onResume();
	}

	public void onPause() {
		surfaceView.onPause();
	}

	void reloadTexture() {
		handler.sendMessage(Message.obtain(handler, MSG_SURFACE_CREATED));
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Touch Event
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return cards.handleTouchEvent(event, false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return cards.handleTouchEvent(event, true);
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Orientation
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//XXX: adds a global layout listener?
	}

	//--------------------------------------------------------------------------------------------------------------------
	// AdapterView<Adapter>
	@Override
	public Adapter getAdapter() {
		return adapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		setAdapter(adapter, 0);
	}

	public void setAdapter(Adapter adapter, int initialPosition) {
		if (this.adapter != null)
			this.adapter.unregisterDataSetObserver(adapterDataObserver);

		this.adapter = adapter;

		if (this.adapter != null) {
			this.adapter.registerDataSetObserver(adapterDataObserver);
			if (this.adapter.getCount() > 0)
				setSelection(initialPosition);
		}
	}

	@Override
	public View getSelectedView() {
		return (bufferIndex < bufferedViews.size() && bufferIndex >= 0) ? bufferedViews.get(bufferIndex) : null;
	}

	@Override
	public void setSelection(int position) {
		if (adapter == null)
			return;

		Assert.assertTrue("Invalid selection position", position >= 0 && position < adapter.getCount());

		releaseViews();

		View selectedView = viewFromAdapter(position, true);
		bufferedViews.add(selectedView);

		for (int i = 1; i <= sideBufferSize; i++) {
			int previous = position - i;
			int next = position + i;

			if (previous >= 0)
				bufferedViews.addFirst(viewFromAdapter(previous, false));
			if (next < adapter.getCount())
				bufferedViews.addLast(viewFromAdapter(next, true));
		}

		bufferIndex = bufferedViews.indexOf(selectedView);
		adapterIndex = position;

		requestLayout();
		updateVisibleView(inFlipAnimation ? -1 : bufferIndex);
	}

	@Override
	public int getSelectedItemPosition() {
		return adapterIndex;
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Layout
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("onLayout: %d, %d, %d, %d; child %d", l, t, r, b, bufferedViews.size());

		for (View child : bufferedViews)
			child.layout(0, 0, r - l, b - t);

		if (changed || contentWidth == 0) {
			int w = r - l;
			int h = b - t;
			surfaceView.layout(0, 0, w, h);

			if (contentWidth != w || contentHeight != h) {
				contentWidth = w;
				contentHeight = h;
			}
		}

		if (enableFlipAnimation && bufferedViews.size() >= 1) {
			View frontView = bufferedViews.get(bufferIndex);
			View backView = null;
			if (bufferIndex < bufferedViews.size() - 1)
				backView = bufferedViews.get(bufferIndex + 1);
			renderer.updateTexture(adapterIndex, frontView, backView == null ? -1 : adapterIndex + 1, backView);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Logger.i( String.format("onMeasure: %d, %d, ; child %d", widthMeasureSpec, heightMeasureSpec, flipViews.size()));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		for (View child : bufferedViews)
			child.measure(widthMeasureSpec, heightMeasureSpec);

		surfaceView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Internals
	private void setupSurfaceView() {
		surfaceView = new GLSurfaceView(getContext());

		cards = new FlipCards(this, orientationVertical);
		renderer = new FlipRenderer(this, cards);

		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		surfaceView.setZOrderOnTop(true);
		surfaceView.setRenderer(renderer);
		surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		addViewInLayout(surfaceView, -1, new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT), false);
	}

	private void reloadAllViews() {
		//XXX: remove all old views and then add new views back
	}

	private void releaseViews() {
		for (View view : bufferedViews)
			releaseView(view);
		bufferedViews.clear();
	}

	private void releaseView(View view) {
		Assert.assertNotNull(view);
		detachViewFromParent(view);
		releasedViews.add(view);
	}

	private View viewFromAdapter(int position, boolean addToTop) {
		Assert.assertNotNull(adapter);

		View releasedView = releasedViews.isEmpty() ? null : releasedViews.removeFirst();
		View view = adapter.getView(position, releasedView, this);
		if (view != releasedView)
			releasedViews.add(releasedView);
		
		setupAdapterView(view, addToTop, view == releasedView);
		return view;
	}

	private void setupAdapterView(View view, boolean addToTop, boolean isReusedView) {
		LayoutParams params = view.getLayoutParams();
		if (params == null) {
			params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		}

		if (isReusedView)
			attachViewToParent(view, addToTop ? 0 : 1, params);
		else
			addViewInLayout(view, addToTop ? 0 : 1, params, true);		
	}

	private void updateVisibleView(int index) {
		if (AphidLog.ENABLE_DEBUG)
			AphidLog.i("Update visible views, index %d, buffered: %d", index, bufferedViews.size());
		
		for (int i = 0; i < bufferedViews.size(); i++)
			bufferedViews.get(i).setVisibility(index == i ? VISIBLE : INVISIBLE);
	}

	void postFlippedToView(final int indexInAdapter) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				flippedToView(indexInAdapter);
			}
		});
	}

	private void debugBufferedViews() {
		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("bufferedViews: " + bufferedViews + ", index: " + bufferIndex);
	}

	void flippedToView(int indexInAdapter) { //XXX: can be simplified and unified with setSelection
		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("flippedToView: %d", indexInAdapter);

		debugBufferedViews();

		if (indexInAdapter >= 0 && indexInAdapter < adapter.getCount()) {

			if (indexInAdapter == adapterIndex + 1) { //forward one page
				if (adapterIndex < adapter.getCount() - 1) {
					adapterIndex++;
					View old = bufferedViews.get(bufferIndex);
					if (bufferIndex > 0)
						releaseView(bufferedViews.removeFirst());
					if (adapterIndex + sideBufferSize < adapter.getCount())
						bufferedViews.addLast(viewFromAdapter(adapterIndex + sideBufferSize, true));
					bufferIndex = bufferedViews.indexOf(old) + 1;
					requestLayout();
					updateVisibleView(inFlipAnimation ? -1 : bufferIndex);
				}
			} else if (indexInAdapter == adapterIndex - 1) {
				if (adapterIndex > 0) {
					adapterIndex--;
					View old = bufferedViews.get(bufferIndex);
					if (bufferIndex < bufferedViews.size() - 1)
						releaseView(bufferedViews.removeLast());
					if (adapterIndex - sideBufferSize >= 0)
						bufferedViews.addFirst(viewFromAdapter(adapterIndex - sideBufferSize, false));
					bufferIndex = bufferedViews.indexOf(old) - 1;
					requestLayout();
					updateVisibleView(inFlipAnimation ? -1 : bufferIndex);
				}
			} else
				setSelection(indexInAdapter);
		} else
			Assert.assertTrue("Invalid indexInAdapter: " + indexInAdapter, false);
		debugBufferedViews();
	}

	void postHideFlipAnimation() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				hideFlipAnimation();
			}
		});
	}

	void showFlipAnimation() {
		if (!inFlipAnimation) {
			inFlipAnimation = true;

			cards.setVisible(true);
			surfaceView.requestRender();

			handler.postDelayed(new Runnable() { //use a delayed message to avoid flicker, the perfect solution would be sending a message from the GL thread 
				public void run() {
					if (inFlipAnimation)
						updateVisibleView(-1);
				}
			}, 100);
		}
	}

	private void hideFlipAnimation() {
		if (inFlipAnimation) {
			inFlipAnimation = false;

			updateVisibleView(bufferIndex);

			handler.post(new Runnable() {
				public void run() {
					if (!inFlipAnimation)
						cards.setVisible(false);
				}
			});
		}
	}
}