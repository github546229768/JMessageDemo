package com.rl.jmessagedemo.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.rl.jmessagedemo.R

/**

 * @Auther: 杨景

 * @datetime: 2021/8/12

 * @desc:

 */
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            if (SPUtils.getInstance(Context.MODE_PRIVATE).getBoolean("isLogin", false)) {
                ActivityUtils.startActivity(MainActivity::class.java)
            } else {
                ActivityUtils.startActivity(LoginActivity::class.java)
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish()
        }, 1000)
    }
}