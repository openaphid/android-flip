package com.aphidmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aphidmobile.flip.FlipViewGroup;

public class MainActivity extends Activity {
	private FlipViewGroup contentView;
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.activity_title);

		contentView = new FlipViewGroup(this);
		
		contentView.addFlipView(View.inflate(this, R.layout.second_page, null));
		contentView.addFlipView(View.inflate(this, R.layout.first_page, null));
		
		setContentView(contentView);
		
		contentView.startFlipping(); //make the first_page view flipping
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://openaphid.github.com/blog/2012/05/21/how-to-implement-flipboard-animation-on-android/"));
		startActivity(intent);
		
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		contentView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		contentView.onPause();
	}
	
	
}
