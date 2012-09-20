package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flipview.demo.R;
import com.aphidmobile.utils.IO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bo Hu
 *         created at 9/17/12, 5:48 PM
 */
public class FlipComplexLayoutActivity extends Activity {
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
		private static List<Data> IMG_DESCRIPTIONS = new ArrayList<Data>();

		static {
			IMG_DESCRIPTIONS.add(new Data("potala_palace.jpg", "http://en.wikipedia.org/wiki/Potala_Palace", "The Potala Palace is located in Lhasa, Tibet Autonomous Region, China. It is named after Mount Potalaka, the mythical abode of Chenresig or Avalokitesvara."));
			IMG_DESCRIPTIONS.add(new Data("drepung_monastery.jpg", "http://en.wikipedia.org/wiki/Drepung", "Drepung Monastery, located at the foot of Mount Gephel, is one of the \"great three\" Gelukpa university monasteries of Tibet."));
		}

		private LayoutInflater inflater;

		private MyBaseAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return IMG_DESCRIPTIONS.size();
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
				layout = inflater.inflate(R.layout.complex1, null);

			final Data data = IMG_DESCRIPTIONS.get(position);
			
			ImageView photoView = (ImageView) layout.findViewById(R.id.photo);
			photoView.setImageBitmap(IO.readBitmap(inflater.getContext().getAssets(), data.imageFilename));
			
			TextView textView = (TextView) layout.findViewById(R.id.description);
			textView.setText(data.description);
			
			Button wikipedia = (Button) layout.findViewById(R.id.wikipedia);
			wikipedia.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse(data.link)
					);
					inflater.getContext().startActivity(intent);
				}
			});
			
			return layout;
		}

		private static class Data {
			public String imageFilename;
			public String link;
			public String description;

			private Data(String imageFilename, String link, String description) {
				this.imageFilename = imageFilename;
				this.link = link;
				this.description = description;
			}
		}
	}
}
