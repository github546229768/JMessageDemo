package com.rl.jmessagedemo.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.Conversation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : BaseViewModel(application) {

    private val _conversationLiveData = MutableLiveData<List<Conversation>>()
    val conversationLiveData: LiveData<List<Conversation>> = _conversationLiveData

    init {
        fetchData()
    }

    fun fetchData() {
        loadingEvent.value = true
        viewModelScope.launch {
            delay(1000)
            _conversationLiveData.value = JMessageClient.getConversationList()
            loadingEvent.value = false
        }
    }

}