package com.aphidmobile.flip.demo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.demo.views.NumberTextView;
import com.aphidmobile.flipview.demo.R;

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
public class FlipTextViewFragment extends Fragment {

  private FlipViewController flipView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    flipView = new FlipViewController(inflater.getContext());

    flipView.setAdapter(new BaseAdapter() {
      @Override
      public int getCount() {
        return 10;
      }

      @Override
      public Object getItem(int position) {
        return position;
      }

      @Override
      public long getItemId(int position) {
        return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        NumberTextView view;
        if (convertView == null) {
          final Context context = parent.getContext();
          view = new NumberTextView(context, position);
          view.setTextSize(context.getResources().getDimension(R.dimen.textSize));
        } else {
          view = (NumberTextView) convertView;
          view.setNumber(position);
        }

        return view;
      }
    });

    return flipView;
  }

  @Override
  public void onResume() {
    super.onResume();
    flipView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    flipView.onPause();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    flipView = null;
  }
}
