package com.rl.jmessagedemo.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.RequestCallback
import cn.jpush.im.android.api.model.GroupMemberInfo
import com.blankj.utilcode.util.ToastUtils


class ChatDetailViewModel(application: Application) : BaseViewModel(application) {

    private val _groupMemberLiveData = MutableLiveData<MutableList<GroupMemberInfo?>>()
    val groupMemberLiveData: LiveData<MutableList<GroupMemberInfo?>> = _groupMemberLiveData


    @Synchronized
    fun fetchData(groupId: Long) {
        JMessageClient.getGroupMembers(groupId, object : RequestCallback<List<GroupMemberInfo?>>() {
            override fun gotResult(p0: Int, p1: String?, p2: List<GroupMemberInfo?>) {
                if (p0 == 0) {
                    val toMutableList = p2.toMutableList()
                    toMutableList.add(null)
                    _groupMemberLiveData.value = toMutableList
                } else
                    ToastUtils.showLong("获取列表失败$p1")
            }

        })
    }

}