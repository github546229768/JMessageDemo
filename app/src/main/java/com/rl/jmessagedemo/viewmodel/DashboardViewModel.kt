package com.rl.jmessagedemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.model.UserInfo
import com.blankj.utilcode.util.ToastUtils


class DashboardViewModel : ViewModel() {

    private val _friendListLiveData = MutableLiveData<List<UserInfo>>()
    val friendListLiveData: LiveData<List<UserInfo>> = _friendListLiveData

    init {
        fetchData()
    }

    @Synchronized
    private fun fetchData() {
        ContactManager.getFriendList(object : GetUserInfoListCallback() {
            override fun gotResult(
                responseCode: Int,
                responseMessage: String,
                userInfoList: List<UserInfo>
            ) {
                if (0 == responseCode) {
                    //获取好友列表成功
                    _friendListLiveData.value = userInfoList
                } else {
                    //获取好友列表失败
                    ToastUtils.showLong("获取好友列表失败$responseMessage")
                }
            }
        })
    }
}