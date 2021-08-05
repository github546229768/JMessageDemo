package com.rl.jmessagedemo.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback

class HomeViewModel : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: MutableLiveData<String> = _userName

    private val _avatar = MutableLiveData<Bitmap>()
    val avatar: MutableLiveData<Bitmap> = _avatar

    init {
        JMessageClient.getMyInfo()?.let {
            _userName.value = it.userName
            it.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                    _avatar.value = p2
                }
            })
        }
    }
}