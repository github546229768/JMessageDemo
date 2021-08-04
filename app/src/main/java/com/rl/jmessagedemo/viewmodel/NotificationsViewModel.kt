package com.rl.jmessagedemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.Conversation

class NotificationsViewModel : ViewModel() {

    private val _conversationLiveData = MutableLiveData<List<Conversation>>()
    val conversationLiveData: LiveData<List<Conversation>> = _conversationLiveData

    init {
        fetchData()
    }

    fun fetchData() {
        _conversationLiveData.value = JMessageClient.getConversationList()
    }

}