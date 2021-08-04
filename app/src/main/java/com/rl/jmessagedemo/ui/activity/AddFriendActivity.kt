package com.rl.jmessagedemo.ui.activity

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.ContactManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.AddFriendListAdapter
import com.rl.jmessagedemo.databinding.ActivityAddfriendBinding


class AddFriendActivity : BaseActivity() {
    private val binding: ActivityAddfriendBinding by lazy {
        setContentView(this, R.layout.activity_addfriend)
    }

    private val mAdapter: AddFriendListAdapter by lazy {
        AddFriendListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    private fun initView() {
        with(binding) {
            recyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
            searchview.isSubmitButtonEnabled = true
            searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    ContactManager.sendInvitationRequest(
                        query,
                        "",
                        "${JMessageClient.getMyInfo().userName}请求好友！",
                        object : BasicCallback() {
                            override fun gotResult(responseCode: Int, p1: String?) {
                                if (0 == responseCode)
                                    ToastUtils.showLong("好友请求请求发送成功")
                                else
                                    ToastUtils.showLong("好友请求发送失败${p1}")
                            }
                        })
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}