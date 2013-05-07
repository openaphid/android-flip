package com.aphidmobile.flip.demo.data;

import com.aphidmobile.utils.AphidLog;

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
public class ImagePlaceholders {

  public static final
  String[]
      CATEGORIES =
      {"abstract", "animals", "city", "food", "nightlife", "fashion", "people",
       "nature", "sports", "technics", "transport"};

  public static String getImageUrl(String category, int w, int h) {
    return AphidLog.format("http://lorempixel.com/%d/%d/%s/1/", w, h, category);
  }
}
