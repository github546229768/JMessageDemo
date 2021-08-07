package com.rl.jmessagedemo

import android.app.Application
import cn.jpush.im.android.api.JMessageClient
import com.blankj.utilcode.util.Utils
import com.lqr.emoji.LQREmotionKit

/**

 * @Auther: 杨景

 * @datetime: 2021/8/3

 * @desc:

 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        JMessageClient.setDebugMode(true)
        JMessageClient.init(this)
        LQREmotionKit.init(this)//表情包
    }
}