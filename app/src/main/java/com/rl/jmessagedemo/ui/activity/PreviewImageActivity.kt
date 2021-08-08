package com.rl.jmessagedemo.ui.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.luck.picture.lib.photoview.PhotoView
import com.rl.jmessagedemo.R

class PreviewImageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val imgUrl = intent.getStringExtra("imgUrl")
        if (imgUrl.isNullOrEmpty()) finish()
        Log.i("TAG-------->", "PreviewImageActivity$imgUrl")

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        val photoView = PhotoView(this)
        Glide.with(this).load(imgUrl)
            .placeholder(R.drawable.ic_baseline_insert_photo_24)
            .error(R.drawable.ic_baseline_insert_photo_24)
            .into(photoView)


        Handler(Looper.getMainLooper()).postDelayed({
            photoView.transitionName = "shared"
            setContentView(photoView)
        }, 10)

        photoView.setOnClickListener { finish() }

    }
}
