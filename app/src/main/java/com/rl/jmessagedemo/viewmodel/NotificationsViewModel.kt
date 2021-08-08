package com.rl.jmessagedemo.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : BaseViewModel(application) {

    private val _conversationLiveData = MutableLiveData<List<Conversation>>()
    val conversationLiveData: LiveData<List<Conversation>> = _conversationLiveData

    init {
        fetchData()
    }

    @Synchronized
    fun fetchData() {
        viewModelScope.launch (Dispatchers.IO){
            _conversationLiveData.postValue(JMessageClient.getConversationList())
        }
    }

}