package com.aphidmobile.flip;

import android.graphics.Bitmap;
import android.view.View;

import javax.microedition.khronos.opengles.GL10;

import static com.aphidmobile.flip.FlipRenderer.*;

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
	private Texture texture;
	private Bitmap bitmap;

	private Card topCard;
	private Card bottomCard;

	public FlipCards() {
		topCard = new Card();
		bottomCard = new Card();

		//topCard.setAnimating(true);
		bottomCard.setAnimating(true);
	}

	public void reloadTexture(View view) {
		bitmap = GrabIt.takeScreenshot(view);
	}

	public void draw(GL10 gl) {
		applyTexture(gl);

		if (texture == null)
			return;

		topCard.draw(gl);
		bottomCard.draw(gl);
	}

	private void applyTexture(GL10 gl) {
		if (bitmap != null) {
			if (texture != null)
				texture.destroy(gl);

			texture = Texture.createTexture(bitmap, gl);

			topCard.setTexture(texture);
			bottomCard.setTexture(texture);

			topCard.setCardVertices(new float[]{
				0f, bitmap.getHeight(), 0f,                     //top left
				0f, bitmap.getHeight() / 2.0f, 0f,              //bottom left
				bitmap.getWidth(), bitmap.getHeight() / 2f, 0f, //bottom right
				bitmap.getWidth(), bitmap.getHeight(), 0f       //top right
			});

			topCard.setTextureCoordinates(new float[]{
				0f, 0f,
				0, bitmap.getHeight() / 2f / (float) texture.getHeight(),
				bitmap.getWidth() / (float) texture.getWidth(), bitmap.getHeight() / 2f / (float) texture.getHeight(),
				bitmap.getWidth() / (float) texture.getWidth(), 0f
			});

			bottomCard.setCardVertices(new float[]{
				0f, bitmap.getHeight() / 2f, 0f,                //top left
				0f, 0, 0f,                                      //bottom left
				bitmap.getWidth(), 0f, 0f,                      //bottom right
				bitmap.getWidth(), bitmap.getHeight() / 2f, 0f  //top right
			});

			bottomCard.setTextureCoordinates(new float[]{
				0f, bitmap.getHeight() / 2f / (float) texture.getHeight(),
				0, bitmap.getHeight() / (float) texture.getHeight(),
				bitmap.getWidth() / (float) texture.getWidth(), bitmap.getHeight() / (float) texture.getHeight(),
				bitmap.getWidth() / (float) texture.getWidth(), bitmap.getHeight() / 2f / (float) texture.getHeight()
			});

			checkError(gl);

			bitmap.recycle();
			bitmap = null;

		}
	}

	public void invalidateTexture() {
		//Texture is vanished when the gl context is gone, no need to delete it explicitly
		texture = null;
	}
}
