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

import android.util.Log;

import java.util.Formatter;

public class AphidLog {

  public static final boolean ENABLE_DEBUG = false;

  private AphidLog() {
  }

  public static final String TAG = "FlipView";

  /**
   * A little trick to reuse a formatter in the same thread
   */
  private static class ReusableFormatter {

    private Formatter formatter;
    private StringBuilder builder;

    public ReusableFormatter() {
      builder = new StringBuilder();
      formatter = new Formatter(builder);
    }

    public String format(String msg, Object... args) {
      formatter.format(msg, args);
      String s = builder.toString();
      builder.setLength(0);
      return s;
    }
  }

  private static final
  ThreadLocal<ReusableFormatter>
      thread_local_formatter =
      new ThreadLocal<ReusableFormatter>() {
        protected ReusableFormatter initialValue() {
          return new ReusableFormatter();
        }
      };

  public static String format(String msg, Object... args) {
    ReusableFormatter formatter = thread_local_formatter.get();
    return formatter.format(msg, args);
  }

  public static void d(String msg) {
    Log.d(TAG, msg);
  }

  public static void d(String msg, Object... args) {
    Log.d(TAG, format(msg, args));
  }

  public static void d(Throwable err, String msg, Object... args) {
    Log.d(TAG, format(msg, args), err);
  }

  public static void i(String msg) {
    Log.i(TAG, msg);
  }

  public static void i(String msg, Object... args) {
    Log.i(TAG, format(msg, args));
  }

  public static void i(Throwable err, String msg, Object... args) {
    Log.i(TAG, format(msg, args), err);
  }

  public static void w(String msg) {
    Log.w(TAG, msg);
  }

  public static void w(String msg, Object... args) {
    Log.w(TAG, format(msg, args));
  }

  public static void w(Throwable err, String msg, Object... args) {
    Log.w(TAG, format(msg, args), err);
  }

  public static void e(String msg) {
    Log.e(TAG, msg);
  }

  public static void e(String msg, Object... args) {
    Log.e(TAG, format(msg, args));
  }

  public static void e(Throwable err, String msg, Object... args) {
    Log.e(TAG, format(msg, args), err);
  }
}
