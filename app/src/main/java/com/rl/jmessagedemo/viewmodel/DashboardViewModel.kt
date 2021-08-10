package com.rl.jmessagedemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.callback.GetUserInfoListCallback
import cn.jpush.im.android.api.model.UserInfo
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.emp.MyUserInfo
import com.rl.jmessagedemo.extensions.ChineseCharToEn


class DashboardViewModel : ViewModel() {

    private val _friendListLiveData = MutableLiveData<List<MyUserInfo>>()
    val friendListLiveData: LiveData<List<MyUserInfo>> = _friendListLiveData

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
                    val newUserInfoList = mutableListOf<MyUserInfo>()
                    userInfoList.forEach {
                        val sortTag = if (it.nickname.isNullOrEmpty()) it.userName else it.nickname
                        newUserInfoList.add(
                            MyUserInfo(
                                it,
                                null,
                                null,
                                ChineseCharToEn.getAllFirstLetter(sortTag)
                            )
                        )
                    }
                    val sortedByUserInfoList = newUserInfoList.sortedBy { it.sortTag }
                    val list = mutableListOf<MyUserInfo>()
                    sortedByUserInfoList.forEachIndexed { index, it ->
                        var beforeFirstChar = ""
                        if (index != 0) {
                            beforeFirstChar =
                                ChineseCharToEn.getAllFirstLetter(sortedByUserInfoList[index - 1].sortTag!!)
                        }
                        val firstChar = ChineseCharToEn.getAllFirstLetter(it.sortTag!!)
                        val isShow =
                            index == 0 || (if (beforeFirstChar.first() in 'a'..'z') beforeFirstChar.first() else '#') != (if (firstChar.first() in 'a'..'z') firstChar.first() else '#')
                        list.add(
                            MyUserInfo(
                                it.userInfo,
                                if (firstChar.first() in 'a'..'z') firstChar.first() else '#',
                                isShow,
                                null
                            )
                        )
                    }
                    _friendListLiveData.value = list.sortedBy { it.firstLetter }
                } else {
                    //获取好友列表失败
                    ToastUtils.showLong("获取好友列表失败$responseMessage")
                }
            }
        })
    }
}