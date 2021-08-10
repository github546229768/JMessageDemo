package com.rl.jmessagedemo.emp

import cn.jpush.im.android.api.model.UserInfo

/**

 * @Auther: 杨景

 * @datetime: 2021/8/10

 * @desc:

 */
data class MyUserInfo(
    val userInfo: UserInfo,
    val firstLetter: Char?,
    val isShowFirst: Boolean?,
    val sortTag: String?
)
