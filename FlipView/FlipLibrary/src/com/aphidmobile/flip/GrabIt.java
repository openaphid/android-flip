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
import android.graphics.Canvas;
import android.view.View;

import com.aphidmobile.utils.AphidLog;

public class GrabIt {

  private GrabIt() {
  }

  public static Bitmap takeScreenshot(View view, Bitmap.Config config) {
    int width = view.getWidth();
    int height = view.getHeight();

    if (view != null && width > 0 && height > 0) {
      Bitmap bitmap = Bitmap.createBitmap(width, height, config);
      Canvas canvas = new Canvas(bitmap);
      view.draw(canvas);

      //canvas.drawColor(Color.RED, PorterDuff.Mode.DARKEN); //NOTES: debug option

      if (AphidLog.ENABLE_DEBUG) {
        AphidLog.d("create bitmap %dx%d, format %s", width, height, config);
      }

      return bitmap;
    } else {
      return null;
    }
  }
}
