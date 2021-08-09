package com.rl.jmessagedemo.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.entity.LocalMedia
import com.rl.jmessagedemo.constant.GROUP_CHAT_TYPE
import com.rl.jmessagedemo.constant.SINGLE_CHAT_TYPE
import com.rl.jmessagedemo.extensions.PictureSelectorUtil.getPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ChatViewModel(application: Application) : BaseViewModel(application) {

    private val _allMessageLiveData = MutableLiveData<MutableList<Message>>()
    val allMessageLiveData: LiveData<MutableList<Message>> = _allMessageLiveData

    private val _isFriendLiveData = MutableLiveData<Boolean>()
    val isFriendLiveData: LiveData<Boolean> = _isFriendLiveData

    private lateinit var mConversation: Conversation
    var isMessageStatus = MutableLiveData<Boolean>()
    var userId: String? = null


    @Synchronized
    fun fetchData(userName: String, type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                SINGLE_CHAT_TYPE -> {
                    //创建单聊会话
                    mConversation = Conversation.createSingleConversation(userName, "")
                    _allMessageLiveData.postValue(mConversation.allMessage)
                }
                GROUP_CHAT_TYPE -> {
                    //群聊会话
                    mConversation = Conversation.createGroupConversation(userName.toLong())
                    _allMessageLiveData.postValue(mConversation.allMessage)
                }
            }
            if (mConversation.targetInfo is UserInfo) {
                val userInfo = mConversation.targetInfo as UserInfo
                _isFriendLiveData.postValue(userInfo.isFriend)
                userId = userInfo.userName
            }
        }
    }

    fun sendMessage(messageDesc: String) {
        val message = mConversation.createSendMessage(TextContent(messageDesc))
        _allMessageLiveData.value?.add(message)
        message.setOnSendCompleteCallback(object : BasicCallback() {
            override fun gotResult(responseCode: Int, p1: String?) {
                if (responseCode == 0) {
                    //消息发送成功
                    isMessageStatus.value = true
                    ToastUtils.showLong("消息发送成功")
                } else {
                    //消息发送失败
                    isMessageStatus.value = false
                    ToastUtils.showLong("消息发送失败$p1")
                }
            }
        })
        JMessageClient.sendMessage(message)
    }

    //图片
    fun sendImageMessage(selectList: MutableList<LocalMedia>) {
        val message = mConversation.createSendMessage(ImageContent(File(getPath(selectList[0]))))
        _allMessageLiveData.value?.add(message)
        message.setOnSendCompleteCallback(object : BasicCallback() {
            override fun gotResult(responseCode: Int, p1: String?) {
                if (responseCode == 0) {
                    //消息发送成功
                    isMessageStatus.value = true
                    ToastUtils.showLong("消息发送成功")
                } else {
                    //消息发送失败
                    isMessageStatus.value = false
                    ToastUtils.showLong("消息发送失败$p1")
                }
            }
        })
        JMessageClient.sendMessage(message)
    }
}