package com.aphidmobile.flip.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flipview.demo.R;

import java.util.ArrayList;
import java.util.List;

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

/**
 * NOTES: Both FlipViewController and WebView are not lightweight UI widgets, please re-consider
 * your design before combining them together. <p/> This demo is only for illustrative purpose.
 * Please refer to the comments inside it for more details.
 */
public class FlipWebViewActivity extends Activity {

  private FlipViewController flipView;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle(R.string.activity_title);

    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    //flip horizontally may be the better choice as most web pages need scroll vertically
    flipView = new FlipViewController(this, FlipViewController.HORIZONTAL);

    flipView.setAdapter(new MyBaseAdapter(this, flipView));

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

    List<String> urls = new ArrayList<String>();
    FlipViewController controller;
    Activity activity;
    int activeLoadingCount = 0;

    private MyBaseAdapter(Activity activity, FlipViewController controller) {
      this.activity = activity;
      this.controller = controller;

      urls.add("http://www.github.com");
      urls.add("http://www.amazon.com");
      urls.add("http://www.yahoo.com");
      urls.add("http://www.google.com");
    }

    @Override
    public int getCount() {
      return urls.size();
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
      WebView webView = new WebView(controller.getContext());
      webView.setWebViewClient(new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
          activity.setProgressBarIndeterminateVisibility(true);
          activeLoadingCount++;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
          controller.refreshPage(
              view);//This works as the webView is the view for a page. Please use refreshPage(int pageIndex) if the webview is only a part of page view.

          activeLoadingCount--;
          activity.setProgressBarIndeterminateVisibility(activeLoadingCount == 0);
        }
      });

      webView.setWebChromeClient(new WebChromeClient() {
        private int lastRefreshProgress = 0;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
          if (newProgress - lastRefreshProgress
              > 20) { //limit the invocation frequency of refreshPage
            controller.refreshPage(view);
            lastRefreshProgress = newProgress;
          }
        }
      });

      webView.loadUrl(urls.get(position));

      return webView;
    }
  }
}
