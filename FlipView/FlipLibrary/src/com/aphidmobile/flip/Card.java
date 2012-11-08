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

import static com.aphidmobile.flip.FlipRenderer.*;
import static com.aphidmobile.utils.TextureUtils.*;
import static javax.microedition.khronos.opengles.GL10.*;

import android.util.FloatMath;

import javax.microedition.khronos.opengles.GL10;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Card {
	public static final int AXIS_TOP = 0;
	public static final int AXIS_BOTTOM = 1;

	private float cardVertices[];

	private short[] indices = {0, 1, 2, 0, 2, 3};

	private FloatBuffer vertexBuffer;

	private ShortBuffer indexBuffer;

	private float textureCoordinates[];

	private FloatBuffer textureBuffer;

	private Texture texture;

	private float angle = 0f;

	private int axis = AXIS_TOP;

	private boolean orientationVertical = true;
	
	private boolean dirty = false;

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public float[] getCardVertices() {
		return cardVertices;
	}

	public short[] getIndices() {
		return indices;
	}

	public ShortBuffer getIndexBuffer() {
		return indexBuffer;
	}

	public void setCardVertices(float[] cardVertices) {
		this.cardVertices = cardVertices;
		this.dirty = true;
	}

	public void setTextureCoordinates(float[] textureCoordinates) {
		this.textureCoordinates = textureCoordinates;
		this.dirty = true;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void setAxis(int axis) {
		this.axis = axis;
	}

	public void setOrientation(boolean orientationVertical) {
		this.orientationVertical = orientationVertical;
	}
	
	public void draw(GL10 gl) {
		if (dirty)
			updateVertices();

		if (cardVertices == null)
			return;

		gl.glFrontFace(GL_CCW);

		gl.glEnable(GL_CULL_FACE);
		gl.glCullFace(GL_BACK);

		gl.glEnableClientState(GL_VERTEX_ARRAY);

		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		gl.glColor4f(1f, 1.0f, 1f, 1.0f);

		if (isValidTexture(texture)) {
			gl.glEnable(GL_TEXTURE_2D);
			gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			gl.glTexCoordPointer(2, GL_FLOAT, 0, textureBuffer);
			gl.glBindTexture(GL_TEXTURE_2D, texture.getId()[0]);
		}

		checkError(gl);

		gl.glPushMatrix();

		if(orientationVertical){
			if (angle > 0) {
				if (axis == AXIS_TOP) {
					gl.glTranslatef(0, cardVertices[1], 0f);
					gl.glRotatef(-angle, 1f, 0f, 0f);
					gl.glTranslatef(0, -cardVertices[1], 0f);
				} else {
					gl.glTranslatef(0, cardVertices[7], 0f);
					gl.glRotatef(angle, 1f, 0f, 0f);
					gl.glTranslatef(0, -cardVertices[7], 0f);
				}
			}			
		} else {
			if (angle > 0) {
				if (axis == AXIS_TOP) {
					gl.glTranslatef(cardVertices[0], 0,  0f);
					gl.glRotatef(-angle, 0f, 1f, 0f);
					gl.glTranslatef(-cardVertices[0], 0, 0f);
				} else {
					gl.glTranslatef(cardVertices[6], 0, 0f);
					gl.glRotatef(angle, 0f, 1f, 0f);
					gl.glTranslatef(-cardVertices[6], 0, 0f);
				}
			}

		}
		

		gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
		gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);

		checkError(gl);

		gl.glPopMatrix();

		if (isValidTexture(texture)) {
			gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL_TEXTURE_2D);
		}

		if (angle > 0) {
			gl.glDisable(GL_LIGHTING);
			gl.glDisable(GL_DEPTH_TEST);

			if (axis == AXIS_TOP) {
				float w,h,z;
				float[] shadowVertices;
				if(orientationVertical){
					w = cardVertices[9] - cardVertices[0];
					h = (cardVertices[1] - cardVertices[4]) * (1f - FloatMath.cos(d2r(angle)));
					z = (cardVertices[1] - cardVertices[4]) * FloatMath.sin(d2r(angle));
					shadowVertices = new float[]{
						cardVertices[0], h + cardVertices[4], z,
						cardVertices[3], cardVertices[4], 0f,
						w, cardVertices[7], 0f,
						w, h + cardVertices[4], z
					};
				} else {
					w = (cardVertices[9] - cardVertices[0]) * (1f - FloatMath.cos(d2r(angle)));
					h = (cardVertices[1] - cardVertices[4]);
					z = (cardVertices[1] - cardVertices[4]) * FloatMath.sin(d2r(angle));
					
					shadowVertices = new float[]{
						cardVertices[9] - w, cardVertices[1], z,
						cardVertices[6] - w, cardVertices[4], z,
						cardVertices[6], cardVertices[7], 0f,
						cardVertices[9], cardVertices[10], 0f
					};

				}

				float alpha = 1f * (90f - angle) / 90f;

				gl.glColor4f(0f, 0.0f, 0f, alpha);
				gl.glVertexPointer(3, GL_FLOAT, 0, toFloatBuffer(shadowVertices));
				gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);
			} else {
				float w,h,z;
				float[] shadowVertices;
				if(orientationVertical){
					w = cardVertices[9] - cardVertices[0];
					h = (cardVertices[1] - cardVertices[4]) * (1f - FloatMath.cos(d2r(angle)));
					z = (cardVertices[1] - cardVertices[4]) * FloatMath.sin(d2r(angle));
					shadowVertices = new float[]{
						cardVertices[0], cardVertices[1], 0f,
						cardVertices[3], cardVertices[1] - h, z,
						w, cardVertices[1] - h, z,
						w, cardVertices[1], 0f
					};
				} else {
					w = (cardVertices[9] - cardVertices[0])  * (1f - FloatMath.cos(d2r(angle)));
					h = (cardVertices[1] - cardVertices[4]);
					z = (cardVertices[1] - cardVertices[4]) * FloatMath.sin(d2r(angle));
					shadowVertices = new float[]{
						cardVertices[0], cardVertices[1], 0f,
						cardVertices[3], cardVertices[4], 0f,
						cardVertices[0] + w, cardVertices[7], z,
						cardVertices[3] + w, cardVertices[10], z
					};
				}
				float alpha = 1f * (90f - angle) / 90f;

				gl.glColor4f(0f, 0.0f, 0f, alpha);
				gl.glVertexPointer(3, GL_FLOAT, 0, toFloatBuffer(shadowVertices));
				gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);
			}
			gl.glEnable(GL_DEPTH_TEST);
			gl.glEnable(GL_LIGHTING);
		}

		checkError(gl);

		gl.glDisable(GL_BLEND);
		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisable(GL_CULL_FACE);
	}

	private void updateVertices() {
		vertexBuffer = toFloatBuffer(cardVertices);
		indexBuffer = toShortBuffer(indices);
		textureBuffer = toFloatBuffer(textureCoordinates);
	}
}
