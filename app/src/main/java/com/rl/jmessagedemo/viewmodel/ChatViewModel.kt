package com.rl.jmessagedemo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.constant.GROUP_CHAT_TYPE
import com.rl.jmessagedemo.constant.SINGLE_CHAT_TYPE


class ChatViewModel(application: Application) : BaseViewModel(application) {

    private val _allMessageLiveData = MutableLiveData<MutableList<Message>>()
    val allMessageLiveData: LiveData<MutableList<Message>> = _allMessageLiveData

    private lateinit var mConversation: Conversation

    var isMessageStatus = MutableLiveData<Boolean>()


    @Synchronized
    fun fetchData(userName: String, type: Int) {
        when(type){
            SINGLE_CHAT_TYPE -> {
                //创建单聊会话
                mConversation = Conversation.createSingleConversation(userName, "")
                _allMessageLiveData.value = mConversation.allMessage
            }
            GROUP_CHAT_TYPE -> {
                //群聊会话
                mConversation = Conversation.createGroupConversation(userName.toLong())
                _allMessageLiveData.value = mConversation.allMessage
            }
        }
    }

    fun sendMessage(messageDesc: String) {
        val message = mConversation.createSendMessage(TextContent(messageDesc))
        message.setOnSendCompleteCallback(object : BasicCallback() {
            override fun gotResult(responseCode: Int, p1: String?) {
                if (responseCode == 0) {
                    //消息发送成功
                    _allMessageLiveData.value?.add(message)
                    isMessageStatus.value = true
                    ToastUtils.showLong("消息发送成功")
                } else {
                    //消息发送失败
                    isMessageStatus.value = false
                    ToastUtils.showLong("消息发送失败$p1")
                }
            }
        });
        JMessageClient.sendMessage(message)
    }
}