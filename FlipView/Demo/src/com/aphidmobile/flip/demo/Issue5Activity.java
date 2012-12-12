package com.aphidmobile.flip.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
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
		private static List<Data> IMG_DESCRIPTIONS = new ArrayList<Data>();

		static {
			IMG_DESCRIPTIONS.add(new Data("Potala Palace", "potala_palace.jpg", "The <b>Potala Palace</b> is located in Lhasa, Tibet Autonomous Region, China. It is named after Mount Potalaka, the mythical abode of Chenresig or Avalokitesvara.", "China", "Lhasa"));
			IMG_DESCRIPTIONS.add(new Data("Drepung Monastery", "drepung_monastery.jpg", "<b>Drepung Monastery</b>, located at the foot of Mount Gephel, is one of the \"great three\" Gelukpa university monasteries of Tibet.", "China", "Lhasa"));
			IMG_DESCRIPTIONS.add(new Data("Sera Monastery", "sera_monastery.jpg", "<b>Sera Monastery</b> is one of the 'great three' Gelukpa university monasteries of Tibet, located 1.25 miles (2.01 km) north of Lhasa.", "China", "Lhasa"));
			IMG_DESCRIPTIONS.add(new Data("Samye Monastery", "samye_monastery.jpg", "<b>Samye Monastery</b> is the first Buddhist monastery built in Tibet, was most probably first constructed between 775 and 779 CE.", "China", "Samye"));
			IMG_DESCRIPTIONS.add(new Data("Tashilunpo Monastery", "tashilunpo_monastery.jpg", "<b>Tashilhunpo Monastery</b>, founded in 1447 by Gendun Drup, the First Dalai Lama, is a historic and culturally important monastery next to Shigatse, the second-largest city in Tibet.", "China", "Shigatse"));
			IMG_DESCRIPTIONS.add(new Data("Zhangmu Port", "zhangmu_port.jpg", "<b>Zhangmu/Dram</b> is a customs town and port of entry located in Nyalam County on the Nepal-China border, just uphill and across the Bhote Koshi River from the Nepalese town of Kodari.", "China", "Zhangmu"));
			IMG_DESCRIPTIONS.add(new Data("Kathmandu", "kathmandu.jpg", "<b>Kathmandu</b> is the capital and, with more than one million inhabitants, the largest metropolitan city of Nepal.", "Nepal", "Kathmandu"));
			IMG_DESCRIPTIONS.add(new Data("Pokhara", "pokhara.jpg", "<b>Pokhara Sub-Metropolitan City</b> is the second largest city of Nepal with approximately 250,000 inhabitants and is situated about 200 km west of the capital Kathmandu.", "Nepal", "Pokhara"));
			IMG_DESCRIPTIONS.add(new Data("Patan", "patan.jpg", "<b>Patan</b>, officially Lalitpur Sub-Metropolitan City, is one of the major cities of Nepal located in the south-central part of Kathmandu Valley.", "Nepal", "Patan"));
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
				layout = inflater.inflate(R.layout.issue5, null);

			final Data data = IMG_DESCRIPTIONS.get(position);
			
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

		private static class Data {
			public final String title;
			public final String imageFilename;
			public final String description;
			public final String country;
			public final String city; 

			private Data(String title, String imageFilename, String description, String country, String city) {
				this.title = title;
				this.imageFilename = imageFilename;
				this.description = description;
				this.country = country;
				this.city = city;
			}
		}
	}
}