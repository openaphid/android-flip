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

package com.aphidmobile.flip.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aphidmobile.flip.FlipViewController;

public class FlipTextViewAltActivity extends FlipTextViewActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    flipView.setOnViewFlipListener(new FlipViewController.ViewFlipListener() {

      @Override
      public void onViewFlipped(View view, int position) {
        Toast.makeText(view.getContext(), "Flipped to page " + position, Toast.LENGTH_SHORT).show();
      }
    });
  }

}
