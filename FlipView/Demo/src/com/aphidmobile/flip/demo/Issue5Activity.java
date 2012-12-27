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

		flipView.setAdapter(new MyBaseAdapter(this));

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

	private static class MyBaseAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private MyBaseAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return Travels.IMG_DESCRIPTIONS.size();
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
			View layout = convertView;
			if (convertView == null)
				layout = inflater.inflate(R.layout.issue5, null);

			final Travels.Data data = Travels.IMG_DESCRIPTIONS.get(position);
			
			UI
			.<TextView>findViewById(layout, R.id.gallery_flip_item_place_name_textview)
			.setText(AphidLog.format("%d. %s", position, data.title));
			
			UI
			.<ImageView>findViewById(layout, R.id.gallery_flip_item_background_imageview)
			.setImageBitmap(IO.readBitmap(inflater.getContext().getAssets(), data.imageFilename));
			
			UI
			.<TextView>findViewById(layout, R.id.gallery_flip_item_place_description_textview)
			.setText(Html.fromHtml(data.description));
			
			UI
			.<TextView>findViewById(layout, R.id.gallery_flip_item_place_city_textview)
			.setText(data.city);
			
			UI
			.<TextView>findViewById(layout, R.id.gallery_flip_item_place_country_textview)
			.setText(data.country);
			
			return layout;
		}

	}
}