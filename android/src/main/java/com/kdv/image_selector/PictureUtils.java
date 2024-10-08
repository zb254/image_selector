package com.kdv.image_selector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnCameraInterceptListener;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;
import java.util.ArrayList;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * Copyright:   Copyright(C) 2023-2033 KEDACOM LTD.
 * Project:     android
 * Module:      com.kdv.image_selector
 * Description:
 * Author:      zhoub
 * Createdate:  2024/10/8 09:28
 * Version:     V
 */
public class PictureUtils {

        /**
         * 打开摄像头 拍照
         *
         * @param context
         * @param isRotateImage                   true 前置 false 后置
         * @param onPictureSelectorResultListener 回调
         */
        public static void openCamera(Context context, boolean isRotateImage, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            openCamera(context, SelectMimeType.ofImage(), isRotateImage, onPictureSelectorResultListener);
        }

        /**
         * 打开摄像头 录制视频
         *
         * @param context
         * @param isRotateImage                   true 前置 false 后置
         * @param onPictureSelectorResultListener 回调
         */
        public static void openVideo(Context context, boolean isRotateImage, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            openCamera(context, SelectMimeType.ofVideo(), isRotateImage, onPictureSelectorResultListener);
        }

        /**
         * 打开摄像头
         *
         * @param context                         上下文
         * @param onPictureSelectorResultListener 回调
         */
        public static void openCamera(Context context, int openCamera, boolean isRotateImage, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            PictureSelector.create(context)
                    .openCamera(openCamera)
                    .isCameraAroundState(isRotateImage)
                    .setVideoThumbnailListener(new VideoThumbListener(context))
                    .setCompressEngine((CompressFileEngine) (context1, source, call) -> Luban.with(context1).load(source).ignoreBy(100)
                            .setCompressListener(new OnNewCompressListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onSuccess(String source, File compressFile) {
                                    if (call != null) {
                                        call.onCallback(source, compressFile.getAbsolutePath());
                                    }
                                }

                                @Override
                                public void onError(String source, Throwable e) {
                                    if (call != null) {
                                        call.onCallback(source, null);
                                    }
                                }
                            }).launch())
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(ArrayList<LocalMedia> result) {
                            onPictureSelectorResultListener.onResult(result);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
        }

        /**
         * 设置头像
         *
         * @param mContext
         * @param selectResult                    结果
         * @param onPictureSelectorResultListener 结果回调
         */
        public static void createAvatar(Context mContext, String language, ArrayList<LocalMedia> selectResult, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            create(mContext, SelectMimeType.ofImage(), selectResult, 1, 1, true, language, onPictureSelectorResultListener);
        }

    /**
     * 选择单张图片或视频
     *
     * @param mContext
     * @param selectResult                    结果
     * @param onPictureSelectorResultListener 结果回调
     */
    public static void createMin(Context mContext, String pickType, String language, ArrayList<LocalMedia> selectResult, OnPictureSelectorResultListener onPictureSelectorResultListener) {
        switch (pickType) {
            case "PickType.video":
                createVideo(mContext, language, selectResult, onPictureSelectorResultListener);
                break;
            case "PickType.image":
                createImageMin(mContext, language, selectResult, onPictureSelectorResultListener);
                break;
            case "PickType.all":
                createPicture(mContext, language, selectResult, onPictureSelectorResultListener);
            default:
                break;
        }


    }

        /**
         * 选择单张图片
         *
         * @param mContext
         * @param selectResult                    结果
         * @param onPictureSelectorResultListener 结果回调
         */
        public static void createImageMin(Context mContext, String language, ArrayList<LocalMedia> selectResult, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            create(mContext, SelectMimeType.ofImage(), selectResult, 1, 1, false, language, onPictureSelectorResultListener);
        }


        /**
         * 选择多张图片
         *
         * @param mContext
         * @param selectResult                    结果
         * @param selectMax                       最多选择
         * @param onPictureSelectorResultListener 结果回调
         */
        public static void createImageMax(Context mContext, int selectMax, String language, ArrayList<LocalMedia> selectResult, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            create(mContext, SelectMimeType.ofImage(), selectResult, 1, selectMax, false, language, onPictureSelectorResultListener);
        }

        /**
         * 选择单个视频
         *
         * @param mContext
         * @param selectResult                    结果
         * @param onPictureSelectorResultListener 结果回调
         */
        public static void createVideo(Context mContext, String language, ArrayList<LocalMedia> selectResult, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            create(mContext, SelectMimeType.ofVideo(), selectResult, 1, 1, false, language, onPictureSelectorResultListener);
        }

        /**
         * 选择单个音频
         *
         * @param mContext
         * @param selectResult                    结果
         * @param onPictureSelectorResultListener 结果回调
         */
        public static void createAudio(Context mContext, String language, ArrayList<LocalMedia> selectResult, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            create(mContext, SelectMimeType.ofAudio(), selectResult, 1, 1, false, language, onPictureSelectorResultListener);
        }

        /**
         * 选择 媒体 图片 视频 音频
         *
         * @param mContext
         * @param selectResult                    结果
         * @param onPictureSelectorResultListener 结果回调
         */
        public static void createPicture(Context mContext, String language, ArrayList<LocalMedia> selectResult, OnPictureSelectorResultListener onPictureSelectorResultListener) {
            create(mContext, SelectMimeType.ofAll(), selectResult, 1, 1, false, language, onPictureSelectorResultListener);
        }

        /**
         * 默认设置
         *
         * @param mContext
         * @param selectMimeType                  结果
         * @param selectResult                    结果
         * @param selectMin                       最少选择
         * @param selectMax                       最多选择
         * @param isCrop                          是否剪裁
         * @param onPictureSelectorResultListener 结果回到
         */
        public static void create(Context mContext, int selectMimeType, ArrayList<LocalMedia> selectResult, int selectMin, int selectMax, boolean isCrop, String language,
                                  OnPictureSelectorResultListener onPictureSelectorResultListener) {
            PictureSelectionModel pictureSelectionModel = PictureSelector.create(mContext)
                    .openGallery(selectMimeType);

            setLanguage(pictureSelectionModel, language);

            pictureSelectionModel.setMaxSelectNum(selectMax)
                    .isDisplayCamera(false)
                    .setCropEngine(getCropFileEngine(isCrop))
                    .setMinSelectNum(selectMin)
                    .setFilterVideoMaxSecond(selectMimeType == SelectMimeType.ofVideo() ? 60 : 60 * 10)
                    .setFilterVideoMinSecond(1)
                    .setRecordVideoMaxSecond(selectMimeType == SelectMimeType.ofVideo() ? 60 : 60 * 10)
                    .setRecordVideoMinSecond(1)
                    .setFilterMaxFileSize(100 * 1024 * 1024)
                    .setCameraInterceptListener(new MeOnCameraInterceptListener())
                    .isFilterSizeDuration(true)
                    .setSelectedData(selectResult)
                    .setRecordAudioInterceptListener(new MeOnRecordAudioInterceptListener())
                    .setCompressEngine((CompressFileEngine) (context, source, call) -> Luban.with(context).load(source).ignoreBy(100)
                            .setCompressListener(new OnNewCompressListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onSuccess(String source, File compressFile) {
                                    if (call != null) {
                                        call.onCallback(source, compressFile.getAbsolutePath());
                                    }
                                }

                                @Override
                                public void onError(String source, Throwable e) {
                                    if (call != null) {
                                        call.onCallback(source, null);
                                    }
                                }
                            }).launch()).setImageEngine(MyGlideEngine.createGlideEngine())
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(ArrayList<LocalMedia> result) {
                            for (LocalMedia media : result) {
                                if (media.getWidth() == 0 || media.getHeight() == 0) {
                                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                                        MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(mContext, media.getPath());
                                        media.setWidth(imageExtraInfo.getWidth());
                                        media.setHeight(imageExtraInfo.getHeight());
                                    } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                        MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(mContext, media.getPath());
                                        media.setWidth(videoExtraInfo.getWidth());
                                        media.setHeight(videoExtraInfo.getHeight());
                                    }
                                }
//                            LogUtils.e("文件名: " + media.getFileName() + "\n" +
//                                    "是否压缩:" + media.isCompressed() + "\n" +
//                                    "压缩路径:" + media.getCompressPath() + "\n" +
//                                    "初始路径:" + media.getPath() + "\n" +
//                                    "绝对路径:" + media.getRealPath() + "\n" +
//                                    "是否裁剪:" + media.isCut() + "\n" +
//                                    "裁剪路径:" + media.getCutPath() + "\n" +
//                                    "是否开启原图:" + media.isOriginal() + "\n" +
//                                    "原图路径:" + media.getOriginalPath() + "\n" +
//                                    "沙盒路径:" + media.getSandboxPath() + "\n" +
//                                    "水印路径:" + media.getWatermarkPath() + "\n" +
//                                    "视频缩略图:" + media.getVideoThumbnailPath() + "\n" +
//                                    "原始宽高: " + media.getWidth() + "x" + media.getHeight() + "\n" +
//                                    "裁剪宽高: " + media.getCropImageWidth() + "x" + media.getCropImageHeight() + "\n" +
//                                    "文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(media.getSize()) + "\n" +
//                                    "文件大小: " + media.getSize() + "\n" +
//                                    "文件时长: " + media.getDuration()
//                            );
                            }
                            onPictureSelectorResultListener.onResult(result);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        }

        /**
         * 查看图片大图
         *
         * @param mContext
         * @param position
         * @param localMedia
         */
        public static void openImage(Context mContext, int position, ArrayList<LocalMedia> localMedia) {
            PictureSelector.create(mContext)
                    .openPreview()
                    .isHidePreviewDownload(true)
                    .setImageEngine(MyGlideEngine.createGlideEngine())
                    .setExternalPreviewEventListener(new OnExternalPreviewEventListener() {
                        @Override
                        public void onPreviewDelete(int position) {

                        }

                        @Override
                        public boolean onLongPressDownload(Context context, LocalMedia media) {
                            return false;
                        }

                    }).startActivityPreview(position, false, localMedia);
        }

        public static void openImage(Context mContext, int position, String imageUrl) {
            ArrayList<LocalMedia> localMedia = new ArrayList<>();
            for (String url : imageUrl.split(",")) {
                localMedia.add(LocalMedia.generateHttpAsLocalMedia(url));
            }
            PictureSelector.create(mContext)
                    .openPreview()
                    .setImageEngine(MyGlideEngine.createGlideEngine())
                    .setExternalPreviewEventListener(new OnExternalPreviewEventListener() {
                        @Override
                        public void onPreviewDelete(int position) {

                        }

                        @Override
                        public boolean onLongPressDownload(Context context, LocalMedia media) {
                            return false;
                        }

                    }).startActivityPreview(position, false, localMedia);
        }

        /**
         * 预览视频
         *
         * @param mContext
         * @param position
         * @param localMedia
         */
        public static void openVideo(Context mContext, int position, ArrayList<LocalMedia> localMedia) {
            openVideo(mContext, position, localMedia, null);
        }

        /**
         * 预览视频
         *
         * @param mContext
         * @param position
         * @param localMedia
         */
        public static void openVideo(Context mContext, int position, ArrayList<LocalMedia> localMedia, VideoPlayerEngine videoPlayerEngine) {
            PictureSelector.create(mContext)
                    .openPreview()
                    .setImageEngine(MyGlideEngine.createGlideEngine())
                    .setVideoPlayerEngine(videoPlayerEngine)
                    .isAutoVideoPlay(true)
                    .setExternalPreviewEventListener(new OnExternalPreviewEventListener() {
                        @Override
                        public void onPreviewDelete(int position) {

                        }

                        @Override
                        public boolean onLongPressDownload(Context context, LocalMedia media) {
                            return false;
                        }

                    }).startActivityPreview(position, false, localMedia);
        }


        private static void setLanguage(PictureSelectionModel model, String language) {
            switch (language) {
                case "Language.Chinese":
                    model.setLanguage(LanguageConfig.CHINESE);
                    break;
                case "Language.ChineseTraditional":
                    model.setLanguage(LanguageConfig.TRADITIONAL_CHINESE);
                    break;
                case "Language.English":
                    model.setLanguage(LanguageConfig.ENGLISH);
                    break;
                case "Language.Japanese":
                    model.setLanguage(LanguageConfig.JAPAN);
                    break;
                case "Language.French":
                    model.setLanguage(LanguageConfig.FRANCE);
                    break;
                case "Language.Korean":
                    model.setLanguage(LanguageConfig.KOREA);
                    break;
                case "Language.German":
                    model.setLanguage(LanguageConfig.GERMANY);
                    break;
                case "Language.Vietnamese":
                    model.setLanguage(LanguageConfig.VIETNAM);
                    break;
                default:
                    model.setLanguage(-1);
            }
        }

        /**
         * 裁剪引擎
         *
         * @return
         */
        private static ImageFileCropEngine getCropFileEngine(boolean isCrop) {
            return isCrop ? new ImageFileCropEngine() : null;
        }

        /**
         * 自定义裁剪
         */
        private static class ImageFileCropEngine implements CropFileEngine {

            @Override
            public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
                UCrop.Options options = buildOptions();
                UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
                uCrop.withOptions(options);
                uCrop.setImageEngine(new UCropImageEngine() {
                    @Override
                    public void loadImage(Context context, String url, ImageView imageView) {
                        Glide.with(context).load(url).override(180, 180).into(imageView);
                    }

                    @Override
                    public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                        Glide.with(context)
                                .asBitmap()
                                .load(url)
                                .override(maxWidth, maxHeight)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        if (call != null) {
                                            call.onCall(resource);
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        if (call != null) {
                                            call.onCall(null);
                                        }
                                    }
                                });
                    }
                });
                uCrop.start(fragment.requireActivity(), fragment, requestCode);
            }
        }


        /**
         * 配制UCrop，可根据需求自我扩展
         *
         * @return
         */
        private static UCrop.Options buildOptions() {
            UCrop.Options options = new UCrop.Options();
            options.setHideBottomControls(true);
            options.setFreeStyleCropEnabled(true);
            options.setShowCropFrame(true);
            options.setShowCropGrid(false);
            options.setCircleDimmedLayer(false);
            options.withAspectRatio(1, 1);
            options.isCropDragSmoothToCenter(false);
            options.setMaxScaleMultiplier(100);
            return options;
        }


        public interface OnPictureSelectorResultListener {
            void onResult(ArrayList<LocalMedia> result);
        }

        /**
         * 自定义拍照
         */
        private static class MeOnCameraInterceptListener implements OnCameraInterceptListener {

            @Override
            public void openCamera(Fragment fragment, int cameraMode, int requestCode) {
                SimpleCameraX camera = SimpleCameraX.of();
                camera.isAutoRotation(true);
                camera.setCameraMode(cameraMode);
                camera.setVideoFrameRate(50);
                camera.setVideoBitRate(5 * 1024 * 1024);
                camera.isDisplayRecordChangeTime(true);
                camera.isManualFocusCameraPreview(true);
                camera.isZoomCameraPreview(true);
                camera.setImageEngine((context, url, imageView) -> Glide.with(context).load(url).into(imageView));
                camera.start(fragment.requireActivity(), fragment, requestCode);
            }
        }

        /**
         * 录音回调事件
         */
        private static class MeOnRecordAudioInterceptListener implements OnRecordAudioInterceptListener {

            @Override
            public void onRecordAudio(Fragment fragment, int requestCode) {
                String[] recordAudio = {Manifest.permission.RECORD_AUDIO};
                if (PermissionChecker.isCheckSelfPermission(fragment.getContext(), recordAudio)) {
                    startRecordSoundAction(fragment, requestCode);
                } else {
                    PermissionChecker.getInstance().requestPermissions(fragment,
                            new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionResultCallback() {
                                @Override
                                public void onGranted() {
                                    startRecordSoundAction(fragment, requestCode);
                                }

                                @Override
                                public void onDenied() {
                                }
                            });
                }
            }
        }

        /**
         * 启动录音意图
         *
         * @param fragment
         * @param requestCode
         */
        private static void startRecordSoundAction(Fragment fragment, int requestCode) {
            Intent recordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if (recordAudioIntent.resolveActivity(fragment.requireActivity().getPackageManager()) != null) {
                fragment.startActivityForResult(recordAudioIntent, requestCode);
            } else {
                ToastUtils.showToast(fragment.getContext(), "The system is missing a recording component");
            }
        }


}
