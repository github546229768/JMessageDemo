package com.rl.jmessagedemo.viewmodel

import android.app.Application
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ToastUtils


class AddFriendViewModel(application: Application) : BaseViewModel(application) {

    @Synchronized
    fun addFriend(userId: String) {
        ContactManager.sendInvitationRequest(
            userId,
            "",
            "${JMessageClient.getMyInfo().userName}请求添加好友！",
            object : BasicCallback() {
                override fun gotResult(responseCode: Int, p1: String?) {
                    if (0 == responseCode)
                        ToastUtils.showLong("好友请求发送成功")
                    else
                        ToastUtils.showLong("好友发送失败${p1}")
                }
            })
    }

}