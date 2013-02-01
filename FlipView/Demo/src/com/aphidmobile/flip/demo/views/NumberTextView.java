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

package com.aphidmobile.flip.demo.views;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

public class NumberTextView extends TextView {

  private int number;

  public NumberTextView(Context context, int number) {
    super(context);
    setNumber(number);
    setTextColor(Color.BLACK);
    setBackgroundColor(Color.WHITE);
    setGravity(Gravity.CENTER);
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
    setText(String.valueOf(number));
  }

  @Override
  public String toString() {
    return "NumberTextView: " + number;
  }
}
