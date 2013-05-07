package com.aphidmobile.flip.demo.issue5;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aphidmobile.flip.FlipViewController;

import java.util.ArrayList;

public class GalleryFlipAdapter extends BaseAdapter {

  private ArrayList<GalleryPage> galleryPageList;

  private Context mContext;

  private FlipViewController controller;

  public GalleryFlipAdapter(Context context, FlipViewController controller) {
    this.mContext = context;
    this.controller = controller;

    ArrayList<GalleryPage> list = new ArrayList<GalleryPage>();
    list.add(new GalleryPage("Test 1",
                             "http://www.hotel-chantecler.be/new-images/grand_place_building.jpg",
                             "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a rutrum arcu. Curabitur a ante at elit dictum imperdiet. Vestibulum et eros nec diam bibendum placerat. Praesent quis lectus metus. Fusce non pulvinar mi. Nulla eu urna nibh.",
                             "http://upload.wikimedia.org/wikipedia/en/0/05/Windows_Photo_Viewer_Icon_on_Windows_7.png"));
    list.add(new GalleryPage("Test 2",
                             "http://www.hotel-chantecler.be/new-images/brussels-jubelpark-cinquantenaire-triumphal%20arch-1.jpg",
                             "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a rutrum arcu. Curabitur a ante at elit dictum imperdiet. Vestibulum et eros nec diam bibendum placerat. Praesent quis lectus metus. Fusce non pulvinar mi. Nulla eu urna nibh.",
                             "http://upload.wikimedia.org/wikipedia/en/0/05/Windows_Photo_Viewer_Icon_on_Windows_7.png"));
    list.add(new GalleryPage("Test 3",
                             "http://www.hotel-chantecler.be/new-images/Belgium-Waterloo-Butte-du-Lion-hill.jpg",
                             "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a rutrum arcu. Curabitur a ante at elit dictum imperdiet. Vestibulum et eros nec diam bibendum placerat. Praesent quis lectus metus. Fusce non pulvinar mi. Nulla eu urna nibh.",
                             "http://upload.wikimedia.org/wikipedia/en/0/05/Windows_Photo_Viewer_Icon_on_Windows_7.png"));
    list.add(new GalleryPage("Test 4", "http://www.hotel-chantecler.be/new-images/ATAPR048.jpg",
                             "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a rutrum arcu. Curabitur a ante at elit dictum imperdiet. Vestibulum et eros nec diam bibendum placerat. Praesent quis lectus metus. Fusce non pulvinar mi. Nulla eu urna nibh.",
                             "http://upload.wikimedia.org/wikipedia/en/0/05/Windows_Photo_Viewer_Icon_on_Windows_7.png"));
    list.add(new GalleryPage("Test 5", "http://www.hotel-chantecler.be/new-images/la_bourse.jpg",
                             "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a rutrum arcu. Curabitur a ante at elit dictum imperdiet. Vestibulum et eros nec diam bibendum placerat. Praesent quis lectus metus. Fusce non pulvinar mi. Nulla eu urna nibh.",
                             "http://upload.wikimedia.org/wikipedia/en/0/05/Windows_Photo_Viewer_Icon_on_Windows_7.png"));

    galleryPageList = list;
  }

  public int getCount() {
    return galleryPageList.size();
  }

  public Object getItem(int position) {
    return galleryPageList.get(position);
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      Log.i("GalleryFlipAdapter", "convertView == null");
      convertView =
          new GalleryFlipItem(mContext, galleryPageList.get(position), controller, position);
    } else {
      Log.i("GalleryFlipAdapter", "convertView != null");
      ((GalleryFlipItem) convertView)
          .refreshView(galleryPageList.get(position), controller, position);
    }
    return convertView;
  }

  public void clear() {
    galleryPageList.clear();
  }
}
