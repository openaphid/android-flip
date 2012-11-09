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
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flipview.demo.R;
import com.aphidmobile.utils.AphidLog;
import com.novoda.imageloader.core.*;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;

public class FlipAsyncContentActivity extends Activity {
	private static ImageManager GLOBAL_IMAGE_MANAGER = null;

	private static void setupImageManager(Context application) {
		if (GLOBAL_IMAGE_MANAGER == null) {
			LoaderSettings settings = new LoaderSettings.SettingsBuilder().withDisconnectOnEveryCall(true).build(application);
			GLOBAL_IMAGE_MANAGER = new ImageManager(application, settings);
		}
	}

	public static final ImageManager getImageManager() {
		return GLOBAL_IMAGE_MANAGER;
	}

	private FlipViewController flipView;
	private OnImageLoadedListener imageLoadedListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupImageManager(getApplicationContext());

		setTitle(R.string.activity_title);

		flipView = new FlipViewController(this);
		flipView.setAdapter(new MyBaseAdapter(this));

		imageLoadedListener = new OnImageLoadedListener() {
			@Override
			public void OnImageLoaded(ImageView view) {

				//Since the listener monitors the progress globally, We have to verify that the ImageView instance is the one inside our flip animation
				if (view.getId() == R.id.async_photo) {
					/*
					// One way to refresh the page is check up through its parent hierarchy and pass the correct container (which should be the exact view of the page returned in your adapter) to FlipViewController.refreshPage(pageView)
					final ViewParent parent = view.getParent();
					
					if (parent instanceof LinearLayout && ((LinearLayout)parent).getId() == R.id.async_container) {
						flipView.refreshPage((LinearLayout) parent);					
					}
					*/

					// Another approach is to use the page index, which should be simpler for most scenarios.
					Object tag = view.getTag(R.id.tag_async_image_view_position);
					if (tag instanceof Integer) {
						flipView.refreshPage((Integer) tag);
					}
				}
			}
		};

		getImageManager().setOnImageLoadedListener(imageLoadedListener);

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getImageManager().getCacheManager().clean();
		getImageManager().getFileManager().clean();
		getImageManager().unRegisterOnImageLoadedListener(imageLoadedListener);
	}

	private static class MyBaseAdapter extends BaseAdapter {
		static final String[] CATEGORIES = { "abstract", "animals", "city", "food", "nightlife", "fashion", "people",
				"nature", "sports", "technics", "transport" };

		private LayoutInflater inflater;
		private ImageTagFactory imageTagFactory;

		private MyBaseAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			imageTagFactory = ImageTagFactory.getInstance(context, android.R.drawable.alert_dark_frame);
		}

		@Override
		public int getCount() {
			return CATEGORIES.length;
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
				layout = inflater.inflate(R.layout.async1, null);

			TextView titleView = (TextView) layout.findViewById(R.id.title);
			ImageView photoView = (ImageView) layout.findViewById(R.id.async_photo);

			titleView.setText(CATEGORIES[position]);
			
			ImageTag tag = imageTagFactory
					.build(AphidLog.format("http://lorempixel.com/400/400/%s/1/", CATEGORIES[position]));
			photoView.setTag(tag);
			photoView.setTag(R.id.tag_async_image_view_position, position);
			FlipAsyncContentActivity.getImageManager().getLoader().load(photoView);
			
			return layout;
		}
	}
}
