package com.rl.jmessagedemo.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil.*
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.databinding.ActivityLoginBinding

/**

 * @Auther: 杨景

 * @datetime: 2021/8/3

 * @desc:

 */
class LoginActivity : BaseActivity() {
    private val binding: ActivityLoginBinding by lazy {
        setContentView(this, R.layout.activity_login)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        if (SPUtils.getInstance(Context.MODE_PRIVATE).getBoolean("isLogin", false)) {
            finish()
            ActivityUtils.startActivity(MainActivity::class.java)
        }
        with(binding) {
            register.setOnClickListener {
                ActivityUtils.startActivity(RegisterActivity::class.java)
            }
            button.setOnClickListener {
                val userName = userNameEdit.text.toString()
                val password = passwordEdit.text.toString()
                if (userName.isEmpty() || password.isEmpty()) return@setOnClickListener
                JMessageClient.login(userName, password, object : BasicCallback() {
                    override fun gotResult(p0: Int, p1: String?) {
                        if (p0 == 0) {
                            SPUtils.getInstance(Context.MODE_PRIVATE).put("isLogin", true)
                            finish()
                            ActivityUtils.startActivity(MainActivity::class.java)
                        } else
                            ToastUtils.showLong("登录失败!$p1")
                    }
                })
            }
        }
    }
}