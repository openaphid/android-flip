package com.aphidmobile.flip.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.aphidmobile.flip.demo.data.Travels;
import com.aphidmobile.flipview.demo.R;
import com.aphidmobile.utils.AphidLog;
import com.aphidmobile.utils.IO;
import com.aphidmobile.utils.UI;

public class TravelAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	public TravelAdapter(Context context) {
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
		if (convertView == null) {
			layout = inflater.inflate(R.layout.complex1, null);
			AphidLog.d("created new view from adapter: %d", position);
		}

		final Travels.Data data = Travels.IMG_DESCRIPTIONS.get(position);
		
		UI
			.<TextView>findViewById(layout, R.id.title)
			.setText(AphidLog.format("%d. %s", position, data.title));
		
		UI
			.<ImageView>findViewById(layout, R.id.photo)
			.setImageBitmap(IO.readBitmap(inflater.getContext().getAssets(), data.imageFilename));
		
		UI
			.<TextView>findViewById(layout, R.id.description)
			.setText(Html.fromHtml(data.description));
		
		UI
			.<Button>findViewById(layout, R.id.wikipedia)
			.setOnClickListener(new View.OnClickListener() {
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
}
