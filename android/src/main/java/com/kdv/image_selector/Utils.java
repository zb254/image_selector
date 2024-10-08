package com.kdv.image_selector;

import android.content.pm.ActivityInfo;

import com.luck.picture.lib.basic.PictureSelectionCameraModel;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.language.LanguageConfig;

public class Utils {
    public static PictureSelectionModel setPhotoSelectOpt(PictureSelectionModel model, int count, double quality) {
        model
                .setImageEngine(MyGlideEngine.createGlideEngine())
                .setMaxSelectNum(count)
                .setMinSelectNum(1)
                .setMaxVideoSelectNum(count)
                .setMinVideoSelectNum(1)
                .setSelectionMode(count > 1 ? SelectModeConfig.MULTIPLE : SelectModeConfig.SINGLE)
                .isDirectReturnSingle(false)
                .isWebp(true)
                .isDisplayCamera(false)
                .isSelectZoomAnim(true)
                .isGif(true)
//                .isEnableCrop(false)
//                .isCompress(false)
//                .compressFocusAlpha(true)
//                .minimumCompressSize(100)
                .isEmptyResultReturn(false)
//                .isAndroidQTransform(true)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .isOriginalControl(false)
                .isMaxSelectEnabledMask(true)
//                .cameraFileName("image_selector_camera")
//                .renameCompressFile("image_selector_compress")
//                .renameCropFileName("image_selector_crop")
        ;
//        if (quality > 0) {
//            model.isCompress(true).compressQuality((int) ((double) quality * 100));
//        }
        return model;
    }

//    public static PictureSelectionModel setCropOpt(PictureSelectionModel model, HashMap<String, Object> opt) {
//        model
////                .isEnableCrop(true)
//                .freeStyleCropEnabled(true)
//                .circleDimmedLayer(opt.get("cropType").equals("CropType.circle"))
//                .showCropFrame(!opt.get("cropType").equals("CropType.circle"))
//                .showCropGrid(false)
//                .rotateEnabled(true)
//                .scaleEnabled(true)
//                .isDragFrame(true)
//                .hideBottomControls(false)
//                .isMultipleSkipCrop(true)
//                .compressFocusAlpha(true)
//                .cutOutQuality((int) ((double) opt.get("quality") * 100));
//        if (opt.get("aspectRatioX") != null) {
//            model.isDragFrame(false);
//            model.withAspectRatio((int) opt.get("aspectRatioX"), (int) opt.get("aspectRatioY"));
//        }
//        return model;
//    }

    public static PictureSelectionModel setLanguage(PictureSelectionModel model, String language) {
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
        return model;
    }

    public static PictureSelectionCameraModel setLanguageOnCamera(PictureSelectionCameraModel model, String language) {
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
        return model;
    }
}
