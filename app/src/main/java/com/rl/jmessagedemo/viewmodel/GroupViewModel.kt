package com.rl.jmessagedemo.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetGroupIDListCallback
import cn.jpush.im.android.api.model.GroupInfo

class GroupViewModel(application: Application) : BaseViewModel(application) {

    private val _groupInfoLiveData = MutableLiveData<MutableList<GroupInfo>>()
    val groupInfoLiveData: MutableLiveData<MutableList<GroupInfo>> = _groupInfoLiveData

    @Synchronized
    fun fetchData() {
        JMessageClient.getGroupIDList(object : GetGroupIDListCallback() {
            override fun gotResult(p0: Int, p1: String?, p2: MutableList<Long>?) {
                if (p0 == 0) {
                    val groupInfoList = mutableListOf<GroupInfo>()
                    p2?.forEach {
                        val conversation = JMessageClient.getGroupConversation(it)
                        if (conversation == null) {
//                            conversation = Conversation.createGroupConversation(it)
                        } else
                            groupInfoList.add(conversation.targetInfo as GroupInfo)
                    }
                    _groupInfoLiveData.value = groupInfoList
                }
            }
        })

    }
}