package com.aphidmobile.utils;

import com.aphidmobile.flip.Texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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
public class TextureUtils {

  public static boolean isValidTexture(Texture t) {
    return t != null && !t.isDestroyed();
  }

  public static float d2r(float degree) {
    return degree * (float) Math.PI / 180f;
  }

  public static FloatBuffer toFloatBuffer(float[] v) {
    ByteBuffer buf = ByteBuffer.allocateDirect(v.length * 4);
    buf.order(ByteOrder.nativeOrder());
    FloatBuffer buffer = buf.asFloatBuffer();
    buffer.put(v);
    buffer.position(0);
    return buffer;
  }

  public static ShortBuffer toShortBuffer(short[] v) {
    ByteBuffer buf = ByteBuffer.allocateDirect(v.length * 2);
    buf.order(ByteOrder.nativeOrder());
    ShortBuffer buffer = buf.asShortBuffer();
    buffer.put(v);
    buffer.position(0);
    return buffer;
  }
}
