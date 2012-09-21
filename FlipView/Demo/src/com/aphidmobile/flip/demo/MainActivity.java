package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.aphidmobile.flipview.demo.R;

import java.util.*;

/**
 * @author Bo Hu
 *         created at 9/15/12, 4:17 PM
 */
public class MainActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(
			new SimpleAdapter(
				this, getData(), android.R.layout.simple_list_item_1, new String[]{"title"}, new int[]{android.R.id.text1}
			)
		);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
		Intent intent = new Intent(this, (Class<? extends Activity>)map.get("activity"));
		startActivity(intent);
	}

	private List<? extends Map<String, ?>> getData() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		addItem(data, "Flip Text Views", FlipTextViewActivity.class);
		addItem(data, "Flip Buttons", FlipButtonActivity.class);
		addItem(data, "Flip Complex Layouts", FlipComplexLayoutActivity.class);
		return data;
	}

	private void addItem(List<Map<String, Object>> data, String title, Class<? extends Activity> activityClass) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("activity", activityClass);
		data.add(map);
	}
}
