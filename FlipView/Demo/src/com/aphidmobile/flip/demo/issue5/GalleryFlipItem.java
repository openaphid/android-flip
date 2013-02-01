package com.aphidmobile.flip.demo.issue5;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flipview.demo.R;
import com.aphidmobile.utils.AphidLog;
import com.aphidmobile.utils.IO;

import junit.framework.Assert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Modified from the issue#5 comment contributed by @cagkanciloglu <p/> Only corrected its async
 * loading logic, although its layout hierarchy can be optimized too.
 */
public class GalleryFlipItem extends LinearLayout {

  /**
   * Borrowed from the official BitmapFun tutorial: http://developer.android.com/training/displaying-bitmaps/index.html
   */
  private static final class AsyncDrawable extends BitmapDrawable {

    private final WeakReference<ContentPhotosDownloader> taskRef;

    public AsyncDrawable(Resources res, Bitmap bitmap, ContentPhotosDownloader task) {
      super(res, bitmap);
      this.taskRef = new WeakReference<ContentPhotosDownloader>(task);
    }

    public static ContentPhotosDownloader getTask(ImageView imageView) {
      Drawable drawable = imageView.getDrawable();
      if (drawable instanceof AsyncDrawable) {
        return ((AsyncDrawable) drawable).taskRef.get();
      }

      return null;
    }
  }

  //Change to a static inner class from a normal inner class, otherwise it may holds a strong reference to GalleryFlipItem, which is not a good practise for AsyncTask 
  private static class ContentPhotosDownloader extends AsyncTask<Void, Void, Bitmap> {

    private String url;

    private WeakReference<FlipViewController> controllerRef;
    //Use WeakReference
    private WeakReference<ImageView> imgViewRef;
    private WeakReference<ProgressBar> progressBarRef;
    private int pageIndex;
    private boolean hideProgress;

    public ContentPhotosDownloader(String url, ImageView imgView, ProgressBar progressBar,
                                   FlipViewController controller, int pageIndex) {
      Assert.assertNotNull(url);
      Assert.assertNotNull(imgView);
      Assert.assertNotNull(controller);
      //progressBar can be null

      this.url = url;
      this.imgViewRef = new WeakReference<ImageView>(imgView);
      this.controllerRef = new WeakReference<FlipViewController>(controller);
      this.pageIndex = pageIndex;
      if (progressBar != null) {
        progressBarRef = new WeakReference<ProgressBar>(progressBar);
      }
    }

    public String getUrl() {
      return url;
    }

    public int getPageIndex() {
      return pageIndex;
    }

    private ProgressBar getProgressBar() {
      return progressBarRef != null ? progressBarRef.get() : null;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();

      ProgressBar bar = getProgressBar();
      if (bar != null) {
        bar.setVisibility(View.VISIBLE);
        ImageView iv = imgViewRef.get();
        if (iv != null) {
          iv.setVisibility(GONE);
        }
      }

      //Commented out; pd and imgBackground should not be accessed directly                         
      /*
        pd.setVisibility(View.VISIBLE);
        imgBackground.setVisibility(View.GONE);
	*/
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
      InputStream is = null;
      try {
        URL aURL = new URL(url);
        URLConnection conn = aURL.openConnection();

        conn.connect();

        is = new BufferedInputStream(conn.getInputStream());

        return BitmapFactory.decodeStream(is);
      } catch (IOException e) {
        AphidLog.e(e, "Failed to load bitmap from url: " + url);
      } finally {
        IO.close(is);
      }

      return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      if (isCancelled()) {
        return;
      }

      ImageView imgView = imgViewRef.get();

      if (imgView != null && AsyncDrawable.getTask(imgView)
                             == this) { //make sure the ImageView instance has not been reused for another page
        if (result != null) {
          imgView.setImageBitmap(result);
          imgView.setVisibility(View.VISIBLE);
        }

        ProgressBar bar = getProgressBar();
        if (bar != null) {
          bar.setVisibility(View.GONE);
        }

        FlipViewController controller = controllerRef.get();
        if (controller != null) {
          controller.refreshPage(pageIndex);
        }
      }
    }
  }

  public GalleryPage mGalleryPage;

  private ImageView imgBackground;
  private ProgressBar pd;
  private LinearLayout lnlPlace;

  private ImageView imgIcon;
  private TextView txtName;
  private TextView txtDistrict;
  private TextView txtCity;

  private Context mContext;

  public void refreshView(GalleryPage mGalleryPage, FlipViewController controller, int pageIndex) {
    this.mGalleryPage = mGalleryPage;

    if (shouldStartAsyncLoad(imgIcon, mGalleryPage.getTargetURL(), pageIndex)) {
      ContentPhotosDownloader
          downloader =
          new ContentPhotosDownloader(mGalleryPage.getTargetURL(), imgIcon, null, controller,
                                      pageIndex);
      imgIcon.setImageDrawable(new AsyncDrawable(getResources(), null, downloader));
      downloader.execute();
    }

    txtName.setText(this.mGalleryPage.getPageTitle());

    if (shouldStartAsyncLoad(imgBackground, mGalleryPage.getImageURL(), pageIndex)) {
      ContentPhotosDownloader
          downloader =
          new ContentPhotosDownloader(mGalleryPage.getImageURL(), imgBackground, pd, controller,
                                      pageIndex);
      imgBackground.setImageDrawable(new AsyncDrawable(getResources(), null, downloader));
      downloader.execute();
    }
  }

  private boolean shouldStartAsyncLoad(ImageView imageView, String url, int pageIndex) {
    ContentPhotosDownloader downloader = AsyncDrawable.getTask(imageView);
    boolean shouldStart = true;
    if (downloader != null) {
      if (downloader.getPageIndex() == pageIndex && url.equals(downloader.getUrl())) {
        shouldStart = false;
      } else {
        downloader.cancel(true);
      }
    }
    return shouldStart;
  }

  @SuppressWarnings("deprecation")
  public GalleryFlipItem(Context context, GalleryPage mGalleryPage, FlipViewController controller,
                         int pageIndex) {
    super(context);
    mContext = context;

    LayoutInflater
        inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.gallery_flip_item_layout, this);

    pd = (ProgressBar) findViewById(R.id.gallery_flip_item_background_progressbar);
    imgBackground = (ImageView) findViewById(R.id.gallery_flip_item_background_imageview);

    lnlPlace = (LinearLayout) findViewById(R.id.gallery_flip_item_place_linearlayout);

    imgIcon = (ImageView) findViewById(R.id.gallery_flip_item_place_icon_imageview);
    txtName = (TextView) findViewById(R.id.gallery_flip_item_place_name_textview);
    txtDistrict = (TextView) findViewById(R.id.gallery_flip_item_place_district_textview);
    txtCity = (TextView) findViewById(R.id.gallery_flip_item_place_city_textview);

    int measuredwidth = 0;
    int measuredheight = 0;
    WindowManager w = ((Activity) context).getWindowManager();

    Display d = w.getDefaultDisplay();
    measuredwidth = d.getWidth();
    measuredheight = d.getHeight();

    //notes: it's not the right approach to make the background fill its parent, using a RelativeLayout could be much better
    imgBackground.setLayoutParams(new LayoutParams(measuredwidth, measuredheight));

    refreshView(mGalleryPage, controller, pageIndex);
  }
}
