package com.aphidmobile.flip;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.View;
import com.aphidmobile.utils.AphidLog;
import com.aphidmobile.utils.TextureUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.LinkedList;

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
public class FlipRenderer implements GLSurfaceView.Renderer {

	private FlipViewController flipViewController;

	private FlipCards cards;

	private boolean created = false;

	private final LinkedList<Texture> postDestroyTextures = new LinkedList<Texture>();

	public FlipRenderer(FlipViewController flipViewController, FlipCards cards) {
		this.flipViewController = flipViewController;
		this.cards = cards;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		created = true;

		cards.invalidateTexture();
		flipViewController.reloadTexture();

		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("onSurfaceCreated");
	}

	public static float[] light0Position = {0, 0, 100f, 0f};

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();

		float fovy = 20f;
		float eyeZ = height / 2f / (float) Math.tan(TextureUtils.d2r(fovy / 2));

		GLU.gluPerspective(gl, fovy, (float) width / (float) height, 0.5f, eyeZ + height / 2); //set zFar be larger than eyeZ to fix issue #5

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl,
			width / 2f, height / 2f, eyeZ,
			width / 2f, height / 2f, 0.0f,
			0.0f, 1.0f, 0.0f
		);

		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);

		float lightAmbient[] = new float[]{3.5f, 3.5f, 3.5f, 1f};
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);

		light0Position = new float[]{0, 0, eyeZ, 0f};
		gl.glLightfv(GL_LIGHT0, GL_POSITION, light0Position, 0);

		if (AphidLog.ENABLE_DEBUG)
			AphidLog.d("onSurfaceChanged: %d, %d", width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		synchronized (postDestroyTextures) {
			for (Texture texture : postDestroyTextures)
				texture.destroy(gl);
			postDestroyTextures.clear();
		}

		cards.draw(this, gl);
	}

	public void postDestroyTexture(Texture texture) {
		synchronized (postDestroyTextures) {
			postDestroyTextures.add(texture);
		}
	}

	public void updateTexture(int frontIndex, View frontView, int backIndex, View backView) {
		if (created) {
			cards.reloadTexture(frontIndex, frontView, backIndex, backView);
			flipViewController.getSurfaceView().requestRender();
		}
	}

	public static void checkError(GL10 gl) {
		if (AphidLog.ENABLE_DEBUG) {
			int error = gl.glGetError();
			if (error != 0)
				throw new RuntimeException(GLU.gluErrorString(error));
		}
	}
}
