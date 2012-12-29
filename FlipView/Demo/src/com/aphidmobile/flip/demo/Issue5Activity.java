package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.demo.data.Travels;
import com.aphidmobile.flip.demo.issue5.GalleryFlipAdapter;
import com.aphidmobile.flipview.demo.R;
import com.aphidmobile.utils.AphidLog;
import com.aphidmobile.utils.IO;
import com.aphidmobile.utils.UI;

public class Issue5Activity extends Activity {
	private FlipViewController flipView;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.activity_title);

		flipView = new FlipViewController(this);

		flipView.setAdapter(new GalleryFlipAdapter(this, flipView));

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