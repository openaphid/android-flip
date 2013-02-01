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

package com.aphidmobile.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class UI {

  private static Handler shared_handler = null;

  public static Handler getHandler() {
    return shared_handler;
  }

  public static boolean isMainThread() {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  public static void assertInMainThread() {
    if (!isMainThread()) {
      throw new RuntimeException("Main thread assertion failed");
    }
  }

  public static void recycleBitmap(Bitmap bm) {
    if (bm != null) {
      if (bm.isRecycled()) {
        AphidLog.w("Bitmap is recycled already?");
      } else {
        bm.recycle();
      }
    }
  }

  public static <T> T callInMainThread(Callable<T> call) throws Exception {
    if (isMainThread()) {
      return call.call();
    } else {
      FutureTask<T> task = new FutureTask<T>(call);
      getHandler().post(task);
      return task.get();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T findViewById(View parent, int id) {
    return (T) parent.findViewById(id);
  }

  @SuppressWarnings("unchecked")
  public static <T> T findViewById(Activity activity, int id) {
    return (T) activity.findViewById(id);
  }
}
