package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.demo.views.NumberTextView;
import com.aphidmobile.flipview.demo.R;

public class MainActivity extends Activity {
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
				NumberTextView view = new NumberTextView(parent.getContext(), position);
				return view;
			}
		});

		setContentView(flipView);

		flipView.startFlipping();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(
			Intent.ACTION_VIEW,
			Uri.parse("http://openaphid.github.com/blog/2012/05/21/how-to-implement-flipboard-animation-on-android/")
		);
		startActivity(intent);

		return true;
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
