package com.aphidmobile.flip;

import java.util.LinkedList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;
import com.aphidmobile.utils.AphidLog;

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

	private LinkedList<View> flipViews = new LinkedList<View>();

	private GLSurfaceView surfaceView;
	private FlipRenderer renderer;
	
	private int width;
	private int height;

	private boolean flipping = false;
	
	private Handler handler = new Handler(new Handler.Callback() {		
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == MSG_SURFACE_CREATED) {
				width = 0;
				height = 0;
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

	private LinkedList<View> bufferedViews = new LinkedList<View>();
	private int bufferIndex = -1;
	private int adapterIndex = -1;

	public FlipViewController(Context context) {
		super(context);
		setupSurfaceView();
	}

	private void setupSurfaceView() {
		surfaceView = new GLSurfaceView(getContext());
		
		renderer = new FlipRenderer(this);
		
		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		surfaceView.setZOrderOnTop(true);
		surfaceView.setRenderer(renderer);
		surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		
		addViewInLayout(surfaceView, -1, new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT), false);
	}

	public GLSurfaceView getSurfaceView() {
		return surfaceView;
	}

	public FlipRenderer getRenderer() {
		return renderer;
	}

	public void addFlipView(View v) {
		flipViews.add(v);
		addView(v);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		AphidLog.i("onLayout: %d, %d, %d, %d; child %d", l, t, r, b, flipViews.size());
		
		for (View child : flipViews)
			child.layout(0, 0, r - l, b - t);

		if (changed || width == 0) {
			int w = r - l;
			int h = b - t;
			surfaceView.layout(0, 0, w, h);
			
			if (width != w || height != h) {
				width = w;
				height = h;

				if (flipping && flipViews.size() >= 2) {
					View frontView = flipViews.get(flipViews.size() - 1);
					View backView = flipViews.get(flipViews.size() - 2);
					renderer.updateTexture(frontView, backView);
					frontView.setVisibility(View.INVISIBLE);
					backView.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Logger.i( String.format("onMeasure: %d, %d, ; child %d", widthMeasureSpec, heightMeasureSpec, flipViews.size()));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		for (View child : flipViews)
			child.measure(widthMeasureSpec, heightMeasureSpec);
	}

	public void startFlipping() {
		flipping = true;
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return renderer.getCards().handleTouchEvent(event);
	}	
	
	//--------------------------------------------------------------------------------------------------------------------
	// AdapterView<Adapter>
	@Override
	public Adapter getAdapter() {
		return adapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		this.adapter = adapter;
		//XXX unregister observer
		//XXX: Auto-generated implementation
	}

	@Override
	public View getSelectedView() {
		return (bufferIndex < bufferedViews.size() && bufferIndex >= 0) ? bufferedViews.get(bufferIndex) : null;
	}

	@Override
	public void setSelection(int i) {
		//XXX: Auto-generated implementation
	}

	@Override
	public int getSelectedItemPosition() {
		return adapterIndex; //XXX: super class returns mNextSelectedPosition, why?
	}

	private void resetFocus() {
		//XXX: Auto-generated method body
	}
}
