package com.rl.jmessagedemo.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.ContactNotifyEvent
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.constant.MESSAGE_RECEIVER_ACTION_KEY
import com.rl.jmessagedemo.constant.MY_PERMISSIONS_REQUEST_CODE
import com.rl.jmessagedemo.viewmodel.MainViewModel


class MainActivity : BaseActivity() {
    private val navView: BottomNavigationView by lazy {
        findViewById(R.id.nav_view)
    }
//    private val navigationView: BottomNavigationView by lazy {
//        findViewById(R.id.navigationView)
//    }
    private lateinit var msgNum: TextView
    private val viewModel by viewModels<MainViewModel>()

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.fetchData()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JMessageClient.registerEventReceiver(this)
        registerReceiver(messageReceiver, IntentFilter().apply {
            addAction(MESSAGE_RECEIVER_ACTION_KEY)
        })
        initBottomBar()
        initBottomMsgNum()
        initSidewall()
        viewModel.msgNumLiveData.observe(this) {
            msgNum.text = if (it > 99) "99" else "$it"
            msgNum.isVisible = it != 0
        }
        requestPermission()
    }

    private fun initSidewall() {
    }

    private fun requestPermission() {
        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        val requestedPermissions = packageInfo.requestedPermissions
        val mPermissionList = mutableListOf<String>()
        requestedPermissions.forEach {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, it)) {
                mPermissionList.add(it)
            }
        }
        if (mPermissionList.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                mPermissionList.toTypedArray(),
                MY_PERMISSIONS_REQUEST_CODE
            )
        }

    }


    override fun onDestroy() {
        JMessageClient.unRegisterEventReceiver(this)
        unregisterReceiver(messageReceiver)
        super.onDestroy()
    }

    private fun initBottomMsgNum() {
        val menuView = navView.getChildAt(0) as BottomNavigationMenuView
        val view = menuView.getChildAt(0)
        val bottomNavigationMenuView = View.inflate(this, R.layout.badge, null)
        (view as BottomNavigationItemView).addView(bottomNavigationMenuView)
        msgNum = bottomNavigationMenuView.findViewById(R.id.msg)
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

    /*????????????????????????*/
    fun onEvent(event: ContactNotifyEvent) {
        val reason = event.reason
        val fromUsername = event.fromUsername
        when (event.type) {
            ContactNotifyEvent.Type.invite_received -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("?????????????????????")
                    setMessage(reason)
                    setNeutralButton("??????") { _, _ ->
                        ContactManager.acceptInvitation(
                            fromUsername,
                            "",
                            object : BasicCallback() {
                                override fun gotResult(p0: Int, p1: String?) {
                                    JMessageClient.createSingleTextMessage(
                                        fromUsername,
                                        "?????????????????????????????????"
                                    )
                                }
                            })
                    }
                    setNegativeButton("??????") { _, _ ->
                        ContactManager.declineInvitation(fromUsername, "", "???", null)
                    }
                    create()
                }.show()
            }
            ContactNotifyEvent.Type.invite_accepted -> {
                ToastUtils.showLong("${fromUsername}?????????????????????")
            }
            ContactNotifyEvent.Type.invite_declined -> {
                ToastUtils.showLong("${fromUsername}?????????????????????")
            }
            ContactNotifyEvent.Type.contact_deleted -> {
            }
            else -> {
            }
        }
    }

    /*????????????????????????*/
    fun onEvent(event: MessageEvent) {
        ToastUtils.showLong("???????????????")
        viewModel.fetchData()
    }
}