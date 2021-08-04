package com.rl.jmessagedemo

import android.app.Application
import cn.jpush.im.android.api.JMessageClient
import com.blankj.utilcode.util.Utils

/**

 * @Auther: 杨景

 * @datetime: 2021/8/3

 * @desc:

 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        JMessageClient.setDebugMode(true)
        JMessageClient.init(this)
    }
}