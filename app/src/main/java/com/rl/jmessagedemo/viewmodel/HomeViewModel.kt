package com.rl.jmessagedemo.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.model.UserInfo
import com.blankj.utilcode.util.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : BaseViewModel(application) {

    private val _avatar = MutableLiveData<Bitmap>()
    val avatar: MutableLiveData<Bitmap> = _avatar

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: MutableLiveData<UserInfo> = _userInfo

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            JMessageClient.getMyInfo()?.let {
                _userInfo.postValue(it)
                Log.i("TAG-------->", "gotResult: ${it.toString()}")
                SPUtils.getInstance(Context.MODE_PRIVATE).put("userName", it.userName)
            }
        }
    }

    fun loadAvatar(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            userInfo.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                    if (p0 == 0)
                        _avatar.postValue(p2)
                    Log.i("TAG-------->", "gotResult: userInfo$userInfo")
                    Log.i("TAG-------->", "gotResult: p1$p1")
                }
            })
        }
    }
}