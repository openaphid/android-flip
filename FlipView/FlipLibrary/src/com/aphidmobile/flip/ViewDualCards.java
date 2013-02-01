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

import android.graphics.Bitmap;
import android.view.View;

import com.aphidmobile.utils.TextureUtils;
import com.aphidmobile.utils.UI;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.opengles.GL10;

import static com.aphidmobile.flip.FlipRenderer.checkError;

public class ViewDualCards {

  private int index = -1;
  private WeakReference<View> viewRef;
  private Texture texture;

  private Bitmap screenshot;

  private Card topCard = new Card();
  private Card bottomCard = new Card();

  private boolean orientationVertical = true;

  public ViewDualCards(boolean orientationVertical) {
    topCard.setOrientation(orientationVertical);
    bottomCard.setOrientation(orientationVertical);
    this.orientationVertical = orientationVertical;
  }

  public int getIndex() {
    return index;
  }

  public View getView() {
    return viewRef != null ? viewRef.get() : null;
  }

  synchronized void resetWithIndex(int index) {
    this.index = index;
    viewRef = null;
    recycleScreenshot();
    recycleTexture();
  }

  synchronized boolean loadView(int index, View view, Bitmap.Config format) {
    UI.assertInMainThread();

    if (this.index == index
        && getView() == view
        && (screenshot != null || TextureUtils.isValidTexture(texture))
        ) {
      return false;
    }

    this.index = index;
    viewRef = null;
    recycleTexture();
    if (view != null) {
      viewRef = new WeakReference<View>(view);
      recycleScreenshot();
      screenshot = GrabIt.takeScreenshot(view, format);
    } else {
      recycleScreenshot();
    }

    return true;
  }

  public Texture getTexture() {
    return texture;
  }

  public Bitmap getScreenshot() {
    return screenshot;
  }

  public Card getTopCard() {
    return topCard;
  }

  public Card getBottomCard() {
    return bottomCard;
  }

  public synchronized void buildTexture(FlipRenderer renderer, GL10 gl) {
    if (screenshot != null) {
      if (texture != null) {
        texture.destroy(gl);
      }
      texture = Texture.createTexture(screenshot, renderer, gl);
      recycleScreenshot();

      topCard.setTexture(texture);
      bottomCard.setTexture(texture);

      final float viewHeight = texture.getContentHeight();
      final float viewWidth = texture.getContentWidth();
      final float textureHeight = texture.getHeight();
      final float textureWidth = texture.getWidth();

      if (orientationVertical) {
        topCard.setCardVertices(new float[]{0f, viewHeight, 0f, // top left
                                            0f, viewHeight / 2.0f, 0f, // bottom left
                                            viewWidth, viewHeight / 2f, 0f, // bottom right
                                            viewWidth, viewHeight, 0f // top right
        });

        topCard.setTextureCoordinates(new float[]{0f, 0f, 0f,
                                                  viewHeight / 2f / textureHeight,
                                                  viewWidth / textureWidth,
                                                  viewHeight / 2f / textureHeight,
                                                  viewWidth / textureWidth, 0f});

        bottomCard.setCardVertices(new float[]{0f, viewHeight / 2f,
                                               0f, // top left
                                               0f, 0f, 0f, // bottom left
                                               viewWidth, 0f, 0f, // bottom right
                                               viewWidth, viewHeight / 2f, 0f // top right
        });

        bottomCard.setTextureCoordinates(new float[]{0f,
                                                     viewHeight / 2f / textureHeight, 0f,
                                                     viewHeight / textureHeight,
                                                     viewWidth / textureWidth,
                                                     viewHeight / textureHeight,
                                                     viewWidth / textureWidth,
                                                     viewHeight / 2f / textureHeight});
      } else {
        topCard.setCardVertices(new float[]{0f, viewHeight, 0f, // top left
                                            0f, 0f, 0f, // bottom left
                                            viewWidth / 2f, 0f, 0f, // bottom right
                                            viewWidth / 2f, viewHeight, 0f // top right
        });

        topCard.setTextureCoordinates(new float[]{0f, 0f, 0f,
                                                  viewHeight / textureHeight,
                                                  viewWidth / 2f / textureWidth,
                                                  viewHeight / textureHeight,
                                                  viewWidth / 2f / textureWidth, 0f});

        bottomCard.setCardVertices(new float[]{viewWidth / 2f,
                                               viewHeight, 0f, // top left
                                               viewWidth / 2f, 0f, 0f, // bottom left
                                               viewWidth, 0f, 0f, // bottom right
                                               viewWidth, viewHeight, 0f // top right
        });

        bottomCard.setTextureCoordinates(new float[]{
            viewWidth / 2f / textureWidth, 0f,
            viewWidth / 2f / textureWidth,
            viewHeight / textureHeight, viewWidth / textureWidth,
            viewHeight / textureHeight, viewWidth / textureWidth,
            0f});
      }

      checkError(gl);
    }
  }

  public synchronized void abandonTexture() {
    texture = null;
  }

  @Override
  public String toString() {
    return "ViewDualCards: (" + index + ", view: " + getView() + ")";
  }

  private void recycleScreenshot() {
    UI.recycleBitmap(screenshot);
    screenshot = null;
  }

  private void recycleTexture() {
    if (texture != null) {
      texture.postDestroy();
      texture = null;
    }
  }


}
