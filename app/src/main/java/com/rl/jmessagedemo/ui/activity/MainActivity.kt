package com.rl.jmessagedemo.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.ContactNotifyEvent
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.viewmodel.DashboardViewModel


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomBar()
        JMessageClient.registerEventReceiver(this);
    }

    override fun onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

    /*好友相关通知事件*/
    fun onEvent(event: ContactNotifyEvent) {
        val reason = event.reason
        val fromUsername = event.fromUsername
//        val appkey = event.getfromUserAppKey()
        when (event.type) {
            ContactNotifyEvent.Type.invite_received -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("接收到好友请求")
                    setMessage(reason)
                    setNeutralButton("同意") { _, _ ->
                        ContactManager.acceptInvitation(fromUsername, "", object : BasicCallback() {
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

    private fun initBottomBar() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


}