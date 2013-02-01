package com.aphidmobile.flip.demo.data;

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
public class Travels {

  public static final List<Data> IMG_DESCRIPTIONS = new ArrayList<Data>();

  static {
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Potala Palace", "potala_palace.jpg",
                                                  "The <b>Potala Palace</b> is located in Lhasa, Tibet Autonomous Region, China. It is named after Mount Potalaka, the mythical abode of Chenresig or Avalokitesvara.",
                                                  "China", "Lhasa",
                                                  "http://en.wikipedia.org/wiki/Potala_Palace"));
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Drepung Monastery", "drepung_monastery.jpg",
                                                  "<b>Drepung Monastery</b>, located at the foot of Mount Gephel, is one of the \"great three\" Gelukpa university monasteries of Tibet.",
                                                  "China", "Lhasa",
                                                  "http://en.wikipedia.org/wiki/Drepung"));
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Sera Monastery", "sera_monastery.jpg",
                                                  "<b>Sera Monastery</b> is one of the 'great three' Gelukpa university monasteries of Tibet, located 1.25 miles (2.01 km) north of Lhasa.",
                                                  "China", "Lhasa",
                                                  "http://en.wikipedia.org/wiki/Sera_Monastery"));
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Samye Monastery", "samye_monastery.jpg",
                                                  "<b>Samye Monastery</b> is the first Buddhist monastery built in Tibet, was most probably first constructed between 775 and 779 CE.",
                                                  "China", "Samye",
                                                  "http://en.wikipedia.org/wiki/Samye"));
    Travels.IMG_DESCRIPTIONS.add(
        new Travels.Data("Tashilunpo Monastery", "tashilunpo_monastery.jpg",
                         "<b>Tashilhunpo Monastery</b>, founded in 1447 by Gendun Drup, the First Dalai Lama, is a historic and culturally important monastery next to Shigatse, the second-largest city in Tibet.",
                         "China", "Shigatse",
                         "http://en.wikipedia.org/wiki/Tashilhunpo_Monastery"));
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Zhangmu Port", "zhangmu_port.jpg",
                                                  "<b>Zhangmu/Dram</b> is a customs town and port of entry located in Nyalam County on the Nepal-China border, just uphill and across the Bhote Koshi River from the Nepalese town of Kodari.",
                                                  "China", "Zhangmu",
                                                  "http://en.wikipedia.org/wiki/Zhangmu"));
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Kathmandu", "kathmandu.jpg",
                                                  "<b>Kathmandu</b> is the capital and, with more than one million inhabitants, the largest metropolitan city of Nepal.",
                                                  "Nepal", "Kathmandu",
                                                  "http://en.wikipedia.org/wiki/Kathmandu"));
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Pokhara", "pokhara.jpg",
                                                  "<b>Pokhara Sub-Metropolitan City</b> is the second largest city of Nepal with approximately 250,000 inhabitants and is situated about 200 km west of the capital Kathmandu.",
                                                  "Nepal", "Pokhara",
                                                  "http://en.wikipedia.org/wiki/Pokhara"));
    Travels.IMG_DESCRIPTIONS.add(new Travels.Data("Patan", "patan.jpg",
                                                  "<b>Patan</b>, officially Lalitpur Sub-Metropolitan City, is one of the major cities of Nepal located in the south-central part of Kathmandu Valley.",
                                                  "Nepal", "Patan",
                                                  "http://en.wikipedia.org/wiki/Patan,_Nepal"));
  }

  public static final class Data {

    public final String title;
    public final String imageFilename;
    public final String description;
    public final String country;
    public final String city;
    public final String link;

    private Data(String title, String imageFilename, String description, String country,
                 String city, String link) {
      this.title = title;
      this.imageFilename = imageFilename;
      this.description = description;
      this.country = country;
      this.city = city;
      this.link = link;
    }
  }
}
