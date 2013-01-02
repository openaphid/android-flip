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
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import com.aphidmobile.utils.AphidLog;
import com.openaphid.flip.R;

import junit.framework.Assert;

import java.util.LinkedList;

public class FlipViewController extends AdapterView<Adapter> {

	public static final int ORIENTATION_VERTICAL = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;
	
	private static final int MAX_RELEASED_VIEW_SIZE = 1;
	
	public static interface ViewFlipListener {
		void onViewFlipped(View view, int position);
	}	

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
	
	private ViewFlipListener onViewFlipListener;
	
	private Bitmap.Config animationBitmapFormat = Bitmap.Config.ARGB_8888;

	public FlipViewController(Context context) {
		this(context, true);
	}
	
	public FlipViewController(Context context, boolean orientationVertical) {
		super(context);
		init(context, orientationVertical);
	}
	
	/**
	 * Constructor required for XML inflation.
	 */
	public FlipViewController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, isOrientationVertical(context, attrs));
	}

	/**
	 * Constructor required for XML inflation.
	 */
	public FlipViewController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, isOrientationVertical(context, attrs));
	}
	
	public Bitmap.Config getAnimationBitmapFormat() {
		return animationBitmapFormat;
	}

	/**
	 * Set the bitmap config for the animation, default is ARGB_8888, which provides the best quality with large peak memory consumption.
	 * @param animationBitmapFormat ALPHA_8 is not supported and will throw exception when binding textures
	 */
	public void setAnimationBitmapFormat(Bitmap.Config animationBitmapFormat) {
		this.animationBitmapFormat = animationBitmapFormat;
	}
	
	/**
	 * Check the attributes for a declared orientation. (Defaults to true)
	 */
	private boolean isOrientationVertical(Context context, AttributeSet attrs) {
		boolean vertical = true;
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.FlipViewController,
				0, 0);
		try {
			vertical = a.getInteger(R.styleable.FlipViewController_orientation, 0) == 0;
		} finally {
			a.recycle();
		}
		return vertical;
	}

	

	private void init(Context context, boolean orientationVertical) {
		ViewConfiguration configuration = ViewConfiguration.get(getContext());
		touchSlop = configuration.getScaledTouchSlop();
		maxVelocity = configuration.getScaledMaximumFlingVelocity();
		this.orientationVertical = orientationVertical;
		setupSurfaceView();		
	}
	
	public ViewFlipListener getOnViewFlipListener() {
		return onViewFlipListener;
	}


	public void setOnViewFlipListener(ViewFlipListener onViewFlipListener) {
		this.onViewFlipListener = onViewFlipListener;
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

	/**
	 * Request the animator to update display if the pageView has been preloaded.
	 * 
	 * If the pageView is being used in the animation or its content has been buffered, the animator forcibly reloads it.
	 * 
	 * The reloading process is a bit heavy for an active page, so please don't invoke it too frequently for an active page. The cost is trivial for inactive pages.
	 * @param pageView
	 */
	public void refreshPage(View pageView) {
		if (cards.refreshPageView(pageView))
			requestLayout();
	}

	/**
	 * @see #refreshPage(android.view.View) 
	 * @param pageIndex
	 */
	public void refreshPage(int pageIndex) {
		if (cards.refreshPage(pageIndex))
			requestLayout();
	}

	/**
	 * Force the animator reload all preloaded pages
	 */
	public void refreshAllPages() {
		cards.refreshAllPages();
		requestLayout();
	}
	
	//--------------------------------------------------------------------------------------------------------------------
	// Locking - Allow to lock the touch input.
	private boolean locked = false;
	
	public void lock() {
		locked = true;
	}
	
	public void unlock() {
		locked = false;
	}

	//--------------------------------------------------------------------------------------------------------------------
	// Touch Event
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (locked) return false;
		return cards.handleTouchEvent(event, false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (locked) return false;
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
		
		cards.resetSelection(position, adapter.getCount());
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
		addReleasedView(view);
	}
	
	private void addReleasedView(View view) {
		Assert.assertNotNull(view);
		if (releasedViews.size() < MAX_RELEASED_VIEW_SIZE)
			releasedViews.add(view);
	}

	private View viewFromAdapter(int position, boolean addToTop) {
		Assert.assertNotNull(adapter);
		
		View releasedView = releasedViews.isEmpty() ? null : releasedViews.removeFirst();
		
		View view = adapter.getView(position, releasedView, this);
		if (releasedView != null && view != releasedView)
			addReleasedView(releasedView);
		
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
		/*
		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("Update visible views, index %d, buffered: %d, adapter %d", index, bufferedViews.size(), adapterIndex);
		*/
		
		for (int i = 0; i < bufferedViews.size(); i++)
			bufferedViews.get(i).setVisibility(index == i ? VISIBLE : INVISIBLE);
	}

	void postFlippedToView(final int indexInAdapter) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				flippedToView(indexInAdapter, true);
			}
		});
	}

	private void debugBufferedViews() {
		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("bufferedViews: %s; buffer index %d, adapter index %d", bufferedViews, bufferIndex, adapterIndex);
	}

	void flippedToView(final int indexInAdapter, boolean isPost) {
		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("flippedToView: %d, isPost %s", indexInAdapter, isPost);

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
			} else {
				AphidLog.e("Should not happen: indexInAdapter %d, adapterIndex %d", indexInAdapter, adapterIndex);
			}
		} else
			Assert.fail("Invalid indexInAdapter: " + indexInAdapter);
		//debugBufferedViews();
	}

	void postHideFlipAnimation() {
		if (inFlipAnimation) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					hideFlipAnimation();
				}
			});
		}
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
			
			if (onViewFlipListener != null)
				onViewFlipListener.onViewFlipped(bufferedViews.get(bufferIndex), adapterIndex);

			handler.post(new Runnable() {
				public void run() {
					if (!inFlipAnimation) {
						cards.setVisible(false);
						surfaceView.requestRender(); //ask OpenGL to clear its display
					}
				}
			});
		}
	}
}