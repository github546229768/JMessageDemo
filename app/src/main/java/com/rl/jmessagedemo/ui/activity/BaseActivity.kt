package com.rl.jmessagedemo.ui.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.LoginStateChangeEvent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.rl.jmessagedemo.R

/**

 * @Auther: 杨景

 * @datetime: 2021/8/3

 * @desc:

 */
open class BaseActivity : AppCompatActivity() {
    private val inputService by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    //加载框
    private val loadDialog by lazy {
        Dialog(this, R.style.loadDialog).apply {
            setContentView(View.inflate(context, R.layout.dialog_loading, null))
        }
    }

    fun isLoadDialog(isShow: Boolean) {
        if (isShow) loadDialog.show() else loadDialog.hide()
        //注意要再Activity销毁之前关闭
    }

    override fun onDestroy() {
        if (loadDialog.isShowing)
            loadDialog.dismiss()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JMessageClient.registerEventReceiver(this)
    }

    open fun hideSoftKeyboard() {
        inputService.hideSoftInputFromWindow(currentFocus?.windowToken, 0) //获取当前焦点
    }


    //登录状态发生改变
    fun onEvent(event: LoginStateChangeEvent) {
        when (event.reason) {
            LoginStateChangeEvent.Reason.user_logout -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("提示")
                    setMessage("用户在其他设备登陆")
                    setNeutralButton("退出登录") { _, _ ->
                        SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                        ActivityUtils.startActivity(LoginActivity::class.java)
                    }
                    setNegativeButton("退出登录") { _, _ ->
                        SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                        ActivityUtils.startActivity(LoginActivity::class.java)
                    }
                    create()
                }.show()
            }
            LoginStateChangeEvent.Reason.user_login_status_unexpected -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("提示")
                    setMessage("用户登陆状态异常,请重新登录")
                    setNeutralButton("重新登录") { _, _ ->
                        SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                        ActivityUtils.startActivity(LoginActivity::class.java)


                    }
                    create()
                }.show()
            }
            LoginStateChangeEvent.Reason.user_deleted -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("提示")
                    setMessage("用户信息被服务器端删除")
                    setNeutralButton("重新注册") { _, _ ->
                        SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                        ActivityUtils.startActivity(RegisterActivity::class.java)


                    }
                    create()
                }.show()
            }
            LoginStateChangeEvent.Reason.user_password_change -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("提示")
                    setMessage("用户密码在其他端被修改")
                    setNeutralButton("重新登录") { _, _ ->
                        SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                        ActivityUtils.startActivity(LoginActivity::class.java)


                    }
                    create()
                }.show()
            }
            LoginStateChangeEvent.Reason.user_disabled -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("提示")
                    setMessage("用户被服务器端封停")
                    setNeutralButton("重新登录") { _, _ ->
                        SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                        ActivityUtils.startActivity(LoginActivity::class.java)


                    }
                    create()
                }.show()
            }
            else -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("提示")
                    setMessage("用户被服务器端封停")
                    setNeutralButton("重新登录") { _, _ ->
                        SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                        ActivityUtils.startActivity(LoginActivity::class.java)


                    }
                    create()
                }.show()
            }
        }
    }


}