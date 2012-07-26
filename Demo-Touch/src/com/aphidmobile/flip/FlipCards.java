package com.aphidmobile.flip;

import static com.aphidmobile.flip.FlipRenderer.*;

import android.graphics.Bitmap;
import android.view.View;

import javax.microedition.khronos.opengles.GL10;

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

public class FlipCards {
	private static final int MAX_ANGLE = 180;
	private static final float SPEED = 1.5f;

	private Texture frontTexture;
	private Bitmap frontBitmap;

	private Texture backTexture;
	private Bitmap backBitmap;

	private Card frontTopCard;
	private Card frontBottomCard;

	private Card backTopCard;
	private Card backBottomCard;

	private float angle = 0f;
	private boolean forward = true;
	private boolean animating = true;

	public FlipCards() {
		frontTopCard = new Card();
		frontBottomCard = new Card();

		backTopCard = new Card();
		backBottomCard = new Card();

		//frontBottomCard.setAnimating(true);

		//backTopCard.setAnimating(true);

		backTopCard.setAxis(Card.AXIS_BOTTOM);
	}

	public void reloadTexture(View frontView, View backView) {
		frontBitmap = GrabIt.takeScreenshot(frontView);
		backBitmap = GrabIt.takeScreenshot(backView);
	}

	public void draw(GL10 gl) {
		applyTexture(gl);

		if (frontTexture == null)
			return;

		if (animating) {
			if (angle >= MAX_ANGLE)
				forward = false;
			if (angle <= 1)
				forward = true;

			if (forward)
				angle += SPEED;
			else
				angle -= SPEED;
		}

		if (angle < 90) {
			frontTopCard.draw(gl);
			backBottomCard.draw(gl);
			frontBottomCard.setAngle(angle);
			frontBottomCard.draw(gl);
		} else {
			frontTopCard.draw(gl);
			backTopCard.setAngle(180 - angle);
			backTopCard.draw(gl);
			backBottomCard.draw(gl);
		}
	}

	private void applyTexture(GL10 gl) {
		if (frontBitmap != null) {
			if (frontTexture != null)
				frontTexture.destroy(gl);

			frontTexture = Texture.createTexture(frontBitmap, gl);

			frontTopCard.setTexture(frontTexture);
			frontBottomCard.setTexture(frontTexture);

			frontTopCard.setCardVertices(new float[]{
				0f, frontBitmap.getHeight(), 0f,                     //top left
				0f, frontBitmap.getHeight() / 2.0f, 0f,              //bottom left
				frontBitmap.getWidth(), frontBitmap.getHeight() / 2f, 0f, //bottom right
				frontBitmap.getWidth(), frontBitmap.getHeight(), 0f       //top right
			});

			frontTopCard.setTextureCoordinates(new float[]{
				0f, 0f,
				0f, frontBitmap.getHeight() / 2f / (float) frontTexture.getHeight(),
				frontBitmap.getWidth() / (float) frontTexture.getWidth(), frontBitmap.getHeight() / 2f / (float) frontTexture.getHeight(),
				frontBitmap.getWidth() / (float) frontTexture.getWidth(), 0f
			});

			frontBottomCard.setCardVertices(new float[]{
				0f, frontBitmap.getHeight() / 2f, 0f,                //top left
				0f, 0f, 0f,                                      //bottom left
				frontBitmap.getWidth(), 0f, 0f,                      //bottom right
				frontBitmap.getWidth(), frontBitmap.getHeight() / 2f, 0f  //top right
			});

			frontBottomCard.setTextureCoordinates(new float[]{
				0f, frontBitmap.getHeight() / 2f / (float) frontTexture.getHeight(),
				0f, frontBitmap.getHeight() / (float) frontTexture.getHeight(),
				frontBitmap.getWidth() / (float) frontTexture.getWidth(), frontBitmap.getHeight() / (float) frontTexture.getHeight(),
				frontBitmap.getWidth() / (float) frontTexture.getWidth(), frontBitmap.getHeight() / 2f / (float) frontTexture.getHeight()
			});

			checkError(gl);

			frontBitmap.recycle();
			frontBitmap = null;
		}

		if (backBitmap != null) {
			if (backTexture != null)
				backTexture.destroy(gl);

			backTexture = Texture.createTexture(backBitmap, gl);

			backTopCard.setTexture(backTexture);
			backBottomCard.setTexture(backTexture);

			backTopCard.setCardVertices(new float[]{
				0f, backBitmap.getHeight(), 0f,                     //top left
				0f, backBitmap.getHeight() / 2.0f, 0f,              //bottom left
				backBitmap.getWidth(), backBitmap.getHeight() / 2f, 0f, //bottom right
				backBitmap.getWidth(), backBitmap.getHeight(), 0f       //top right
			});

			backTopCard.setTextureCoordinates(new float[]{
				0f, 0f,
				0f, backBitmap.getHeight() / 2f / (float) backTexture.getHeight(),
				backBitmap.getWidth() / (float) backTexture.getWidth(), backBitmap.getHeight() / 2f / (float) backTexture.getHeight(),
				backBitmap.getWidth() / (float) backTexture.getWidth(), 0f
			});

			backBottomCard.setCardVertices(new float[]{
				0f, backBitmap.getHeight() / 2f, 0f,                //top left
				0f, 0f, 0f,                                      //bottom left
				backBitmap.getWidth(), 0f, 0f,                      //bottom right
				backBitmap.getWidth(), backBitmap.getHeight() / 2f, 0f  //top right
			});

			backBottomCard.setTextureCoordinates(new float[]{
				0f, backBitmap.getHeight() / 2f / (float) backTexture.getHeight(),
				0f, backBitmap.getHeight() / (float) backTexture.getHeight(),
				backBitmap.getWidth() / (float) backTexture.getWidth(), backBitmap.getHeight() / (float) backTexture.getHeight(),
				backBitmap.getWidth() / (float) backTexture.getWidth(), backBitmap.getHeight() / 2f / (float) backTexture.getHeight()
			});

			checkError(gl);

			backBitmap.recycle();
			backBitmap = null;
		}
	}

	public void invalidateTexture() {
		//Texture is vanished when the gl context is gone, no need to delete it explicitly
		frontTexture = null;
		backTexture = null;
	}
}
