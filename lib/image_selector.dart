import 'dart:async';
import 'dart:io';
import 'package:flutter/services.dart';

class ImageSelector {
  static const MethodChannel _channel =
      const MethodChannel('');

  static Future<List<Media>?> pick({
    int count = 1,
    PickType pickType = PickType.image,
    bool gif = true,
    CropOption? cropOpt,
    int? maxSize,
    double? quality,
    Language language = Language.System,
  }) async {
    return null;
  }

  static Future<List<Media>?> openCamera({
    PickType pickType = PickType.image,
    int maxTime = 15,
    CropOption? cropOpt,
    int? maxSize,
    double? quality,
    Language language = Language.System,
  }) async {
    return null;
  }

  static Future<bool> saveImageToAlbum(File file, {String? albumName}) async {
    return false;
  }

  static Future<bool> saveVideoToAlbum(File file, {String? albumName}) async {
    return false;
  }
}

enum PickType {
  image,
  video,
  all,
}

enum CropType {
  rect,
  circle,
}

enum Language {
  System,
  Chinese,
  ChineseTraditional,
  English,
  Japanese,
  French,
  Korean,
  German,
  Vietnamese,
}

class CropAspectRatio {
  final int aspectRatioX;
  final int aspectRatioY;

  const CropAspectRatio(this.aspectRatioX, this.aspectRatioY)
      : assert(aspectRatioX > 0, 'aspectRatioX must > 0'),
        assert(aspectRatioY > 0, 'aspectRatioY must > 0');

  static const custom = null;
  static const wh2x1 = CropAspectRatio(2, 1);
  static const wh1x2 = CropAspectRatio(1, 2);
  static const wh3x4 = CropAspectRatio(3, 4);
  static const wh4x3 = CropAspectRatio(4, 3);
  static const wh16x9 = CropAspectRatio(16, 9);
  static const wh9x16 = CropAspectRatio(9, 16);
}

class CropOption {
  final CropType cropType;
  final CropAspectRatio? aspectRatio;

  CropOption({
    this.aspectRatio = CropAspectRatio.custom,
    this.cropType = CropType.rect,
  });
}

class Media {
  ///视频缩略图图片路径
  ///Video thumbnail image path
  String? thumbPath;

  ///视频路径或图片路径
  ///Video path or image path
  String path;

  /// 文件大小
  String size;

  Media({
    required this.path,
    this.thumbPath,
    required this.size,
  });
}
