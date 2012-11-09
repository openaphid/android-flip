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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.widget.*;
import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flipview.demo.R;
import com.aphidmobile.utils.AphidLog;
import com.aphidmobile.utils.IO;

import java.util.ArrayList;
import java.util.List;

public class FlipHorizontalLayoutActivity extends Activity {
	private FlipViewController flipView;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.activity_title);

		flipView = new FlipViewController(this, false);

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
			IMG_DESCRIPTIONS.add(new Data("Potala Palace", "potala_palace.jpg", "http://en.wikipedia.org/wiki/Potala_Palace", "The <b>Potala Palace</b> is located in Lhasa, Tibet Autonomous Region, China. It is named after Mount Potalaka, the mythical abode of Chenresig or Avalokitesvara."));
			IMG_DESCRIPTIONS.add(new Data("Drepung Monastery", "drepung_monastery.jpg", "http://en.wikipedia.org/wiki/Drepung", "<b>Drepung Monastery</b>, located at the foot of Mount Gephel, is one of the \"great three\" Gelukpa university monasteries of Tibet."));
			IMG_DESCRIPTIONS.add(new Data("Sera Monastery", "sera_monastery.jpg", "http://en.wikipedia.org/wiki/Sera_Monastery", "<b>Sera Monastery</b> is one of the 'great three' Gelukpa university monasteries of Tibet, located 1.25 miles (2.01 km) north of Lhasa."));
			IMG_DESCRIPTIONS.add(new Data("Samye Monastery", "samye_monastery.jpg", "http://en.wikipedia.org/wiki/Samye", "<b>Samye Monastery</b> is the first Buddhist monastery built in Tibet, was most probably first constructed between 775 and 779 CE."));
			IMG_DESCRIPTIONS.add(new Data("Tashilunpo Monastery", "tashilunpo_monastery.jpg", "http://en.wikipedia.org/wiki/Tashilhunpo_Monastery", "<b>Tashilhunpo Monastery</b>, founded in 1447 by Gendun Drup, the First Dalai Lama, is a historic and culturally important monastery next to Shigatse, the second-largest city in Tibet."));
			IMG_DESCRIPTIONS.add(new Data("Zhangmu Port", "zhangmu_port.jpg", "http://en.wikipedia.org/wiki/Zhangmu", "<b>Zhangmu/Dram</b> is a customs town and port of entry located in Nyalam County on the Nepal-China border, just uphill and across the Bhote Koshi River from the Nepalese town of Kodari."));
			IMG_DESCRIPTIONS.add(new Data("Kathmandu", "kathmandu.jpg", "http://en.wikipedia.org/wiki/Kathmandu", "<b>Kathmandu</b> is the capital and, with more than one million inhabitants, the largest metropolitan city of Nepal."));
			IMG_DESCRIPTIONS.add(new Data("Pokhara", "pokhara.jpg", "http://en.wikipedia.org/wiki/Pokhara", "<b>Pokhara Sub-Metropolitan City</b> is the second largest city of Nepal with approximately 250,000 inhabitants and is situated about 200 km west of the capital Kathmandu."));
			IMG_DESCRIPTIONS.add(new Data("Patan", "patan.jpg", "http://en.wikipedia.org/wiki/Patan,_Nepal", "<b>Patan</b>, officially Lalitpur Sub-Metropolitan City, is one of the major cities of Nepal located in the south-central part of Kathmandu Valley."));
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
			
			TextView titleView = (TextView) layout.findViewById(R.id.title);
			titleView.setText(AphidLog.format("%d. %s", position, data.title));
			
			ImageView photoView = (ImageView) layout.findViewById(R.id.photo);
			photoView.setImageBitmap(IO.readBitmap(inflater.getContext().getAssets(), data.imageFilename));
			
			TextView textView = (TextView) layout.findViewById(R.id.description);
			textView.setText(Html.fromHtml(data.description));
			
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
			public String title;
			public String imageFilename;
			public String link;
			public String description;

			private Data(String title, String imageFilename, String link, String description) {
				this.title = title;
				this.imageFilename = imageFilename;
				this.link = link;
				this.description = description;
			}
		}
	}
}
