package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flipview.demo.R;
import com.aphidmobile.utils.AphidLog;
import com.novoda.imageloader.core.*;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;

/**
 * @author Bo Hu
 *         created at 11/5/12, 10:23 PM
 */
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
	
	private Handler handler = new Handler();
	
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
				final ViewParent parent = view.getParent();
				if (parent instanceof LinearLayout && ((LinearLayout)parent).getId() == R.id.async_container) {
					flipView.refreshPage((LinearLayout) parent);					
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
		static final String[] CATEGORIES = {
			"abstract",
			"animals",
			"city",
			"food",
			"nightlife",
			"fashion",
			"people",
			"nature",
			"sports",
			"technics",
			"transport"
		};
		
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
			ImageView photoView = (ImageView) layout.findViewById(R.id.photo);
			
			titleView.setText(CATEGORIES[position]);
			ImageTag tag = imageTagFactory.build(AphidLog.format("http://lorempixel.com/400/400/%s/1/", CATEGORIES[position]));
			photoView.setTag(tag);
			FlipAsyncContentActivity.getImageManager().getLoader().load(photoView);
			return layout;
		}
	}
}
