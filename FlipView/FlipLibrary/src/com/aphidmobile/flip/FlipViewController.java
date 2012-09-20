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

public class FlipViewController extends AdapterView<Adapter> {

	private static final int MSG_SURFACE_CREATED = 1;

	private GLSurfaceView surfaceView;
	private FlipRenderer renderer;
	private FlipCards cards;

	private int width;
	private int height;

	private boolean enableFlipAnimation = true;
	
	private boolean inFlipAnimation = false;
	
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == MSG_SURFACE_CREATED) {
				width = 0;
				height = 0;
				AphidLog.d("requestLayout after got MSG_SURFACE_CREATED");
				requestLayout();
				return true;
			}
			return false;
		}
	});

	//AdapterView Related
	private Adapter adapter;
	private DataSetObserver adapterDataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			View v = getChildAt(bufferIndex);
			if (v != null) {
				for (int i = 0; i < adapter.getCount(); i++) {
					if (v.equals(adapter.getItem(i))) {
						adapterIndex = i;
						break;
					}
				}
			}
			resetFocus();
		}
	};

	private final LinkedList<View> bufferedViews = new LinkedList<View>();
	private int bufferIndex = -1;
	private int adapterIndex = -1;
	private int sideBufferSize = 1;

	private float touchSlop;
	private float maxVelocity;

	public FlipViewController(Context context) {
		super(context);
		ViewConfiguration configuration = ViewConfiguration.get(getContext());
		touchSlop = configuration.getScaledTouchSlop();
		maxVelocity = configuration.getScaledMaximumFlingVelocity();

		setupSurfaceView();
	}

	public float getTouchSlop() {
		return touchSlop;
	}

	private void setupSurfaceView() {
		surfaceView = new GLSurfaceView(getContext());

		cards = new FlipCards(this);
		renderer = new FlipRenderer(this, cards);

		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		surfaceView.setZOrderOnTop(true);
		surfaceView.setRenderer(renderer);
		surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		addViewInLayout(surfaceView, -1, new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT), false);
		
		requestLayout();
	}

	public GLSurfaceView getSurfaceView() {
		return surfaceView;
	}

	public FlipRenderer getRenderer() {
		return renderer;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		AphidLog.d("onLayout: %d, %d, %d, %d; child %d", l, t, r, b, bufferedViews.size());

		for (View child : bufferedViews) //todo: test visibility?
			child.layout(0, 0, r - l, b - t);

		if (changed || width == 0) {
			int w = r - l;
			int h = b - t;
			surfaceView.layout(0, 0, w, h);

			if (width != w || height != h) {
				width = w;
				height = h;
			}
		}

		if (enableFlipAnimation && bufferedViews.size() >= 1) { //XXX: check inFlipAnimation?
			View frontView = bufferedViews.get(bufferIndex);
			View backView = null;
			if (bufferIndex < bufferedViews.size() - 1)
				backView = bufferedViews.get(bufferIndex + 1);
			renderer.updateTexture(adapterIndex, frontView, backView == null ? -1 : adapterIndex + 1, backView);
		} else
			AphidLog.d("Ignore updateTexture");
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Logger.i( String.format("onMeasure: %d, %d, ; child %d", widthMeasureSpec, heightMeasureSpec, flipViews.size()));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		for (View child : bufferedViews) //todo: test visibility?
			child.measure(widthMeasureSpec, heightMeasureSpec);

		surfaceView.measure(widthMeasureSpec, heightMeasureSpec);
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

	public void reloadTexture() {
		handler.sendMessage(Message.obtain(handler, MSG_SURFACE_CREATED));
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Touch Event
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) { //XXX not correct
		boolean ret = cards.handleTouchEvent(event, false);
		return ret;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return cards.handleTouchEvent(event, true);
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Orientation
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);    //XXX: Auto-generated super call
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
		return adapterIndex; //XXX: super class returns mNextSelectedPosition, why?
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Internals

	private void resetFocus() {
		//XXX: Auto-generated method body
	}

	private void releaseViews() {
		for (View view : bufferedViews)
			releaseView(view);
		bufferedViews.clear();
	}

	private void releaseView(View view) {
		Assert.assertNotNull(view);
		detachViewFromParent(view);
		//XXX: add it to a released view buffer?
	}

	private View viewFromAdapter(int position, boolean addToTop) {
		Assert.assertNotNull(adapter);

		View view = adapter.getView(position, null, this); //XXX: replace null with a released view instance
		setupAdapterView(view, addToTop);
		return view;
	}

	private void setupAdapterView(View view, boolean addToTop) {
		LayoutParams params = view.getLayoutParams();
		if (params == null) {
			params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		}

		//XXX: 0 should be 1?
		addViewInLayout(view, addToTop ? -1 : 0, params, true); //XXX: different logic for released view?		
	}

	private void updateVisibleView(int index) {
		AphidLog.i("Update visible views, index %d, buffered: %d", index, bufferedViews.size());
		for (int i = 0; i < bufferedViews.size(); i++) {
			bufferedViews.get(i).setVisibility(index == i ? VISIBLE : INVISIBLE);
		}

		//invalidate(); //XXX: is this necessary?
	}

	public void postFlippedToView(final int indexInAdapter) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				flippedToView(indexInAdapter);
			}
		});
	}

	private void debugBufferedViews() {
		AphidLog.d("bufferedViews: " + bufferedViews + ", index: " + bufferIndex);
	}

	public void flippedToView(int indexInAdapter) { //XXX: can be simplified and unified with setSelection
		AphidLog.d("flippedToView: %d", indexInAdapter);

/*		if (indexInAdapter == adapterIndex && false)
			return;*/

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

	public void postHideFlipAnimation() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				hideFlipAnimation();
			}
		});
	}

	protected void showFlipAnimation() {
		if (!inFlipAnimation) {
			inFlipAnimation = true;
			cards.setVisible(true);
			surfaceView.requestRender();
			
			handler.postDelayed(new Runnable() { //use a delayed message to avoid flicker
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