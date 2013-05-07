package com.aphidmobile.flip.demo.issue5;

public class GalleryPage {

  private String pageTitle;
  private String imageURL;
  private String targetLinkCaption;
  private String targetURL;

  public GalleryPage(String pageTitle, String imageURL, String targetLinkCaption,
                     String targetURL) {
    this.pageTitle = pageTitle;
    this.imageURL = imageURL;
    this.targetLinkCaption = targetLinkCaption;
    this.targetURL = targetURL;
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public String getTargetLinkCaption() {
    return targetLinkCaption;
  }

  public void setTargetLinkCaption(String targetLinkCaption) {
    this.targetLinkCaption = targetLinkCaption;
  }

  public String getTargetURL() {
    return targetURL;
  }

  public void setTargetURL(String targetURL) {
    this.targetURL = targetURL;
  }

}
