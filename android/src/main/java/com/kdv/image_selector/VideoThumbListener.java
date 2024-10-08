package com.kdv.image_selector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnVideoThumbnailEventListener;
import com.luck.picture.lib.utils.PictureFileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copyright:   Copyright(C) 2023-2033 KEDACOM LTD.
 * Project:     android
 * Module:      com.kdv.image_selector
 * Description: 视频缩略图
 * Author:      zhoub
 * Createdate:  2024/10/8 09:34
 * Version:     V
 */
public class VideoThumbListener implements OnVideoThumbnailEventListener {

    private Context context;

    public VideoThumbListener(Context context) {
        this.context = context;
    }

    @Override
    public void onVideoThumbnail(Context context, String videoPath, OnKeyValueResultCallbackListener call) {
        Glide.with(context).asBitmap().sizeMultiplier(0.6F).load(videoPath).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                FileOutputStream fos = null;
                String result = null;
                try {
                    File targetFile = new File(getVideoThumbnailDir(), "thumbnails_" + System.currentTimeMillis() + ".jpg");
                    fos = new FileOutputStream(targetFile);
                    fos.write(stream.toByteArray());
                    fos.flush();
                    result = targetFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    PictureFileUtils.close(fos);
                    PictureFileUtils.close(stream);
                }
                if (call != null) {
                    call.onCallback(videoPath, result);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                if (call != null) {
                    call.onCallback(videoPath, "");
                }
            }
        });
    }

    private String getVideoThumbnailDir() {
        File externalFilesDir = context.getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Thumbnail");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }
}