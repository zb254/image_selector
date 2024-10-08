package com.kdv.image_selector;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** ImagesPickerPlugin */
public class ImagesPickerPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.RequestPermissionsResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Result _result;
  private Activity activity;
  private Context context;
  private int WRITE_IMAGE_CODE = 33;
  private int WRITE_VIDEO_CODE = 44;
  private String WRITE_IMAGE_PATH;
  private String WRITE_VIDEO_PATH;
  private String ALBUM_NAME;
  public static String channelName = "chavesgu/image_selector";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), channelName);
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }

  public static void registerWith(Registrar registrar) {
    ImagesPickerPlugin instance = new ImagesPickerPlugin();
    final MethodChannel channel = new MethodChannel(registrar.messenger(), channelName);
    channel.setMethodCallHandler(instance);
    instance.context = registrar.context();
    registrar.addRequestPermissionsResultListener(instance);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addRequestPermissionsResultListener(this);
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
                null,
                new PictureUtils.OnPictureSelectorResultListener() {
                  @Override
                  public void onResult(ArrayList<LocalMedia> medias) {
                    // 结果回调
                    onResult(medias);
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

        PictureUtils.openCamera(context, false, new PictureUtils.OnPictureSelectorResultListener() {
          @Override
          public void onResult(ArrayList<LocalMedia> medias) {
            // 结果回调
            onResult(medias);
          }
        });
        break;
      }
      case "saveVideoToAlbum": {
        String path = (String) call.argument("path");
        String albumName = call.argument("albumName");
        WRITE_VIDEO_PATH = path;
        ALBUM_NAME = albumName;
        if (hasPermission()) {
          saveVideoToGallery(path, albumName);
        } else {
          String[] permissions = new String[2];
          permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
          permissions[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
          ActivityCompat.requestPermissions(activity, permissions, WRITE_VIDEO_CODE);
        }
        break;
      }
      case "saveImageToAlbum": {
        String path = (String) call.argument("path");
        String albumName = call.argument("albumName");
        WRITE_IMAGE_PATH = path;
        ALBUM_NAME = albumName;
        if (hasPermission()) {
          saveImageToGallery(path, albumName);
        } else {
          String[] permissions = new String[2];
          permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
          permissions[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
          ActivityCompat.requestPermissions(activity, permissions, WRITE_IMAGE_CODE);
        }
        break;
      }
//      case "saveNetworkImageToAlbum": {
//        String url = (String) call.arguments;
//        saveNetworkImageToGallery(url);
//        break;
//      }
      default:
        result.notImplemented();
        break;
    }
  }

  private void onResult(ArrayList<LocalMedia> medias) {
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

  private void saveImageToGallery(final String path, String albumName) {
    boolean status = false;
    String suffix = path.substring(path.lastIndexOf('.')+1);
    Bitmap bitmap = BitmapFactory.decodeFile(path);
    status = FileSaver.saveImage(context, bitmap, suffix, albumName);
    _result.success(status);
  }

  private void saveVideoToGallery(String path, String albumName) {
    _result.success(FileSaver.saveVideo(context, path, albumName));
  }

  private boolean hasPermission() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED);
  }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      if (requestCode == WRITE_IMAGE_CODE && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
          saveImageToGallery(WRITE_IMAGE_PATH, ALBUM_NAME);
          return true;
      }
      if (requestCode == WRITE_VIDEO_CODE && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
          saveVideoToGallery(WRITE_VIDEO_PATH, ALBUM_NAME);
          return true;
      }
      return false;
    }
}
