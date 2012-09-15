package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.demo.views.NumberButton;
import com.aphidmobile.flipview.demo.R;

/**
 * @author Bo Hu
 *         created at 9/15/12, 8:25 PM
 */
public class FlipButtonActivity extends Activity {
	private FlipViewController flipView;

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
				NumberButton button = new NumberButton(parent.getContext(), position);
				button.setTextSize(360);
				return button;
			}
		});

		setContentView(flipView);

		flipView.startFlipping();
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
