package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.demo.views.NumberTextView;
import com.aphidmobile.flipview.demo.R;

public class FlipTextViewActivity extends Activity {

  protected FlipViewController flipView;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle(R.string.activity_title);

    flipView = new FlipViewController(this);

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

    setContentView(flipView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    flipView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    flipView.onPause();
  }
}
