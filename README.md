# image_selector

[![images-picker](https://img.shields.io/badge/pub-1.2.3-orange)](https://pub.dev/packages/image_selector)

Flutter plugin for selecting images/videos from the Android and iOS image library, and taking pictures/videos with the camera,save image/video to album/gallery

ios(10+): [ZLPhotoBrowser](https://github.com/longitachi/ZLPhotoBrowser)

android(21+): [PictureSelector](https://github.com/LuckSiege/PictureSelector)

### Support
- pick multiple images/videos from photo album (wechat style)
- use camera to take image/video
- crop images with custom aspectRatio
- compress images with quality/maxSize
- save image/video to album/gallery
- localizations currently support
  - System, Chinese, ChineseTraditional, English
- Android 13.0+ permission
    
### Install
For ios:
```
<key>NSCameraUsageDescription</key>
<string>Example usage description</string>
<key>NSMicrophoneUsageDescription</key>
<string>Example usage description</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Example usage description</string>
```
For android:
```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
```
And,
```yaml
image_selector: ^newest
```
```dart
import "package:image_selector/image_selector.dart";
```

### Usage

- simple picker image

```dart
Future getImage() async {
    List<Media> res = await ImageSelector.pick(
      count: 3,
      pickType: PickType.image,
    );
// Media
// .path
// .thumbPath (path for video thumb)
// .size (kb)
}
```
- simple picker video
```dart
Future getImage() async {
    List<Media> res = await ImageSelector.pick(
      count: 3,
      pickType: PickType.video,
    );
// Media
// .path
// .thumbPath (path for video thumb)
// .size (kb)
}
```
- simple open camera
```dart
Future openCamera() async {
  ImageSelector.openCamera(
    pickType: PickType.video,
    maxTime: 15, // record video max time
  );
}
```
- add gif support
```dart
Future getImage() async {
  ImageSelector.pick(
    // ...
    gif: true, // default is true
  );
}
```
- add cropper (gif crop unsupported)
```dart
Future getImage() async {
  ImagesPicker.pick(
    // ...
    // when cropOpt isn't null, crop is enabled
    cropOpt: CropOption(
      aspectRatio: CropAspectRatio.custom,
      cropType: CropType.rect, // currently for android
    ),
  );
}
```
- add compress
```dart
Future getImage() async {
  ImagesPicker.pick(
    // ...
    // when maxSize/quality isn't null, compress is enabled
    quality: 0.8, // only for android
    maxSize: 500, // only for ios (kb)
  );
}
```
- set language
```dart
Future getImage() async {
  ImagesPicker.pick(
    language: Language.English,
  // you can set Language.System for following phone language
  );
}
```
### All params
```dart
// for pick
int count = 1,
PickType pickType = PickType.image,
bool gif = true,
CropOption cropOpt,
int maxSize,
double quality,

// for camera
PickType pickType = PickType.image,
int maxTime = 15,
CropOption cropOpt,
int maxSize,
double quality,
```
### proguard-rules
```
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
```

# License
MIT License
