package com.aphidmobile.flip;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import com.aphidmobile.utils.AphidLog;
import junit.framework.Assert;

import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.opengles.GL10.*;

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
public class Texture {
	private FlipRenderer renderer;
	
	private int[] id = {0};

	private int width, height;
	private int contentWidth, contentHeight;

	private boolean destroyed = false;

	private Texture() {
	}

	public static Texture createTexture(Bitmap bitmap, FlipRenderer renderer, GL10 gl) {
		Texture t = new Texture();
		t.renderer = renderer;

		Assert.assertTrue("bitmap should not be null or recycled", bitmap != null && !bitmap.isRecycled());

		int w = Integer.highestOneBit(bitmap.getWidth() - 1) << 1;
		int h = Integer.highestOneBit(bitmap.getHeight() - 1) << 1;

		t.contentWidth = bitmap.getWidth();
		t.contentHeight = bitmap.getHeight();
		t.width = w;
		t.height = h;

		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("createTexture: %d, %d; POT: %d, %d", bitmap.getWidth(), bitmap.getHeight(), w, h);


		gl.glGenTextures(1, t.id, 0);
		gl.glBindTexture(GL_TEXTURE_2D, t.id[0]);
		gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		/*
		 gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		 gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		 */
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		GLUtils.texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bitmap);

		return t;
	}
	
	public void postDestroy() {
		renderer.postDestroyTexture(this);
	}

	public void destroy(GL10 gl) {
		if (id[0] != 0) {
			gl.glDeleteTextures(1, id, 0);
			if (AphidLog.ENABLE_DEBUG)
				AphidLog.d("Destroy texture: %d", id[0]);
		}

		id[0] = 0;
		destroyed = true;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public int[] getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getContentWidth() {
		return contentWidth;
	}

	public int getContentHeight() {
		return contentHeight;
	}
}
