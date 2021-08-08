package com.rl.jmessagedemo.extensions

import android.app.Activity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType

object PictureSelectorUtil {
    fun openCamera(activity: Activity?, isCompress: Boolean, isCrop: Boolean, requestCode: Int) {
        PictureSelector.create(activity)
            .openCamera(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine())
            .selectionMode(PictureConfig.SINGLE)
            .isCompress(isCompress)
            .isEnableCrop(isCrop)
            .withAspectRatio(1, 1)
            .forResult(requestCode)
    }

    fun openGallerySingle(
        activity: Activity?,
        isCompress: Boolean,
        isCrop: Boolean,
        requestCode: Int
    ) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine())
            .selectionMode(PictureConfig.SINGLE)
            .isPreviewImage(true)
            .isCamera(true)
            .isCompress(isCompress)
            .isEnableCrop(isCrop)
            .withAspectRatio(1, 1)
            .forResult(requestCode)
    }

    fun openGalleryMultiple(activity: Activity?, maxCount: Int, requestCode: Int) {
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine())
            .selectionMode(PictureConfig.MULTIPLE)
            .isPreviewImage(true)
            .isCamera(true)
            .maxSelectNum(maxCount)
            .forResult(requestCode)
    }

}