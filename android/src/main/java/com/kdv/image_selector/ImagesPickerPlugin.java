package com.kdv.image_selector;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.utils.PictureFileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** ImagesPickerPlugin */
public class ImagesPickerPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Result _result;
  private Activity activity;
  private Context context;

  public static String channelName = "chavesgu/image_selector";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), channelName);
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {

  }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    _result = result;
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "pick": {
        int count = (int) call.argument("count");
        String pickType = call.argument("pickType");
        double quality = call.argument("quality");
        boolean supportGif = call.argument("gif");
        HashMap<String, Object> cropOption = call.argument("cropOption");
        String language = call.argument("language");

        PictureUtils.createMin(activity,
                pickType,
                language,
                new ArrayList<>(),
                new PictureUtils.OnPictureSelectorResultListener() {
                  @Override
                  public void onResult(ArrayList<LocalMedia> medias) {
                    // 结果回调
                    handleResult(medias);
                  }
                });
        break;
      }
      case "openCamera": {
        String pickType = call.argument("pickType");
        int maxTime = call.argument("maxTime");
        double quality = call.argument("quality");
        HashMap<String, Object> cropOption = call.argument("cropOption");
        String language = call.argument("language");

        int chooseType = SelectMimeType.TYPE_VIDEO;
        switch (pickType) {
          case "PickType.image":
            chooseType = SelectMimeType.TYPE_ALL;
            break;
          default:
            chooseType = PictureMimeType.getMimeType(PictureMimeType.MIME_TYPE_PREFIX_VIDEO);
            break;
        }

        PictureUtils.openCamera(activity, false, new PictureUtils.OnPictureSelectorResultListener() {
          @Override
          public void onResult(ArrayList<LocalMedia> medias) {
            // 结果回调
            handleResult(medias);
          }
        });
        break;
      }
      default:
        result.notImplemented();
        break;
    }
  }

  private void handleResult(ArrayList<LocalMedia> medias) {
    new Thread() {
      @Override
      public void run() {
        final List<Object> resArr = new ArrayList<Object>();
        for (LocalMedia media : medias) {
          HashMap<String, Object> map = new HashMap<String, Object>();
          String path = media.getPath();

          if (media.isCut()) path = media.getCutPath();
          if (media.isCompressed()) path = media.getCompressPath();
          map.put("path", path);

          String thumbPath;
          if (media.getMimeType().contains("image")) {
            thumbPath = path;
          } else {
            thumbPath = media.getVideoThumbnailPath();
          }
          map.put("thumbPath", thumbPath);

          String size = PictureFileUtils.formatAccurateUnitFileSize(media.getSize());
          map.put("size", size);

          resArr.add(map);
        }
        new Handler(context.getMainLooper()).post(new Runnable() {
          @Override
          public void run() {
            _result.success(resArr);
          }
        });
      }
    }.start();
  }
}
