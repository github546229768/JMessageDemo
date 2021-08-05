package com.rl.jmessagedemo.extensions

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

/**

 * @Auther: 杨景

 * @datetime: 2021/8/5

 * @desc:

 */

@BindingAdapter(value = ["loadImageBitmap"])
fun loadImageBitmap(image: ImageView, bitmap: Bitmap?) {
    bitmap?.let {
        image.setImageBitmap(it)
    }
}