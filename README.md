android-flip
============

Aphid FlipView is a UI component to accomplish the flipping animation like [Flipboard](http://www.flipboard.com) does.

A pre-built demo APK file for Android OS 2.2+ can be found at:

[https://github.com/openaphid/android-flip/tree/master/FlipView/Demo/APK](https://github.com/openaphid/android-flip/tree/master/FlipView/Demo/APK)

Please also refer to our blog posts for more details:

[http://openaphid.github.com/blog/categories/flipview/](http://openaphid.github.com/blog/categories/flipview/)

## Release Notes

For a complete change list please checkout our [issue tracker](https://github.com/openaphid/android-flip/issues/milestones?state=closed)

### [v0.9.8, May 7th 2013](https://github.com/openaphid/android-flip/issues?milestone=4&state=closed)

- Fixes the occasional flash issue when flipping. ([#52](https://github.com/openaphid/android-flip/issues/52))

- Disables the dropping shadow of title bar in the demo app.

### [v0.9.7, Jan 1th 2012](https://github.com/openaphid/android-flip/issues?milestone=3&state=closed)

- The core control flow has been rewritten, which fixes several performance and reliability issues about adapter and async content support. ([#36](https://github.com/openaphid/android-flip/issues/36), [#29](https://github.com/openaphid/android-flip/issues/29), [#28](https://github.com/openaphid/android-flip/issues/28), [#8](https://github.com/openaphid/android-flip/issues/8))

- Supports different bitmap format for animation, which can be used to reduce peak memory consumption. ([#34](https://github.com/openaphid/android-flip/issues/34))

- Fixes a severe memory leak issue. ([#33](https://github.com/openaphid/android-flip/issues/33), [#21](https://github.com/openaphid/android-flip/issues/21))

- The demo [FlipAsyncContentActivity](https://github.com/openaphid/android-flip/blob/master/FlipView/Demo/src/com/aphidmobile/flip/demo/FlipAsyncContentActivity.java) has been rewritten to illustrate the correct way of handling async content. ([#37](https://github.com/openaphid/android-flip/issues/37))

- A new demo [FlipDynamicAdapterActivity](https://github.com/openaphid/android-flip/blob/master/FlipView/Demo/src/com/aphidmobile/flip/demo/FlipDynamicAdapterActivity.java) to demonstrate how to dynamically load more pages. ([#41](https://github.com/openaphid/android-flip/issues/41))

- Thanks to [@siegfriedpammer](https://github.com/siegfriedpammer) for his contribution. ([Pull #40](https://github.com/openaphid/android-flip/pull/40))

### [v0.9.6, Dec 12th 2012](https://github.com/openaphid/android-flip/issues?milestone=1&state=closed)

- Adds fling support. ([Pull #10](https://github.com/openaphid/android-flip/pull/10), [Issue #20](https://github.com/openaphid/android-flip/issues/20))

- Adds XML configuration support. ([Issue #13](https://github.com/openaphid/android-flip/issues/13))

- Adds several new demos.

- Fixes several bugs when flipping. ([Issue #17](https://github.com/openaphid/android-flip/issues/17), [Issue #16](https://github.com/openaphid/android-flip/issues/16), [Issue #15](https://github.com/openaphid/android-flip/issues/15), [Issue #14](https://github.com/openaphid/android-flip/issues/14))

- Special thanks to [@iPaulPro](https://github.com/iPaulPro) for his outstanding contributions.

### v0.9.5, Nov 9th 2012

- Supports flipping horizontally. ([Pull Request 6](https://github.com/openaphid/android-flip/pull/6). Thanks to [@axexmedearis](https://github.com/alexmedearis) for the contribution)

![screenshot](http://openaphid.github.com/images/flipview-horizontal-demo.gif "Screenshot of Aphid FlipView v0.9.5")

- Supports event listener to get notified when flipping finishes. ([Issue #3](https://github.com/openaphid/android-flip/issues/3))

- Supports content reloading when flipping. ([Issue #4](https://github.com/openaphid/android-flip/issues/3))

- Incorrect rendering of shadow in horizontal mode. ([Issue #7](https://github.com/openaphid/android-flip/issues/7))

### v0.9, Sep 21st 2012
First release

![screenshot](http://openaphid.github.com/images/flipview-demo.gif "Screenshot of Aphid FlipView v0.9")

## Copyright and License

```
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
````
