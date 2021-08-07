package com.rl.jmessagedemo.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.ContactNotifyEvent
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rl.jmessagedemo.R


class MainActivity : BaseActivity() {
    private val navView: BottomNavigationView by lazy {
        findViewById(R.id.nav_view)
    }
    private lateinit var msgNum: TextView
//    private val messageReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == MESSAGE_RECEIVER_ACTION_KEY) {
//                msgNum.text = intent.getStringExtra("num")
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomBar()
        initBottomMsgNum()
        JMessageClient.registerEventReceiver(this)
//        registerReceiver(messageReceiver, IntentFilter().apply {
//            addAction(MESSAGE_RECEIVER_ACTION_KEY)
//        });//注册广播
    }

    override fun onDestroy() {
        JMessageClient.unRegisterEventReceiver(this)
//        unregisterReceiver(messageReceiver);
        super.onDestroy()
    }

    private fun initBottomMsgNum() {
        val menuView = navView.getChildAt(0) as BottomNavigationMenuView
        val view = menuView.getChildAt(0)
        val bottomNavigationMenuView = View.inflate(this, R.layout.badge, null)
        (view as BottomNavigationItemView).addView(bottomNavigationMenuView)
        msgNum = bottomNavigationMenuView.findViewById(R.id.msg)
        val allUnReadMsgCount = JMessageClient.getAllUnReadMsgCount()
        msgNum.text = if (allUnReadMsgCount > 99) "99" else "$allUnReadMsgCount"
        msgNum.isVisible = allUnReadMsgCount != 0
    }

    private fun initBottomBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /*好友相关通知事件*/
    fun onEvent(event: ContactNotifyEvent) {
        val reason = event.reason
        val fromUsername = event.fromUsername
        when (event.type) {
            ContactNotifyEvent.Type.invite_received -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("接收到好友请求")
                    setMessage(reason)
                    setNeutralButton("同意") { _, _ ->
                        ContactManager.acceptInvitation(
                            fromUsername,
                            "",
                            object : BasicCallback() {
                                override fun gotResult(p0: Int, p1: String?) {
                                }
                            })
                    }
                    setNegativeButton("拒绝") { _, _ ->
                        ContactManager.declineInvitation(fromUsername, "", "无", null)
                    }
                    create()
                }.show()
            }
            ContactNotifyEvent.Type.invite_accepted -> {
                ToastUtils.showLong("${fromUsername}已同意好友请求")
            }
            ContactNotifyEvent.Type.invite_declined -> {
                ToastUtils.showLong("${fromUsername}已拒绝好友请求")
            }
            ContactNotifyEvent.Type.contact_deleted -> {
            }
            else -> {
            }
        }
    }
}