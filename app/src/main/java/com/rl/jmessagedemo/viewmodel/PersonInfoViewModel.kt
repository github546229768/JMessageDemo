package com.rl.jmessagedemo.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.model.UserInfo


class PersonInfoViewModel(application: Application) : BaseViewModel(application) {

    private val _nickname = MutableLiveData<String>()
    val nickname: MutableLiveData<String> = _nickname

    private val _avatar = MutableLiveData<Bitmap>()
    val avatar: MutableLiveData<Bitmap> = _avatar

    private val _sex = MutableLiveData<String>()
    val sex: MutableLiveData<String> = _sex

    init {
        fetchData()
    }

    fun fetchData() {
        JMessageClient.getMyInfo()?.let {
            _nickname.value = it.nickname
            it.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                    _avatar.value = p2
                }
            })
            _sex.value = when (UserInfo.Gender.female) {
                UserInfo.Gender.male -> "男"
                UserInfo.Gender.female -> "女"
                else -> "未知"
            }
        }
    }
}