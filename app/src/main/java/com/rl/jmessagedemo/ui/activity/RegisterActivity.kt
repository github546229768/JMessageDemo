package com.rl.jmessagedemo.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.databinding.ActivityRegisterBinding

/**

 * @Auther: 杨景

 * @datetime: 2021/8/3

 * @desc:

 */
class RegisterActivity : BaseActivity() {
    private val binding : ActivityRegisterBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_register)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding){
            button.setOnClickListener {
                val userName = userNameEdit.text.toString()
                val password = passwordEdit.text.toString()
                if (userName.isEmpty() || password.isEmpty())return@setOnClickListener
                JMessageClient.register(userName, password, object : BasicCallback() {
                    override fun gotResult(p0: Int, p1: String?) {
                        LogUtils.i(p1)
                        if (p0 == 0) {
                            finish()
                            ActivityUtils.startActivity(LoginActivity::class.java)
                        } else
                            ToastUtils.showLong("注册失败!$p1")
                    }
                })
            }
        }
    }
}