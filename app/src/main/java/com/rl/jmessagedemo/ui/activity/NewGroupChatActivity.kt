package com.rl.jmessagedemo.ui.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.CreateGroupCallback
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.NewGroupChatAdapter
import com.rl.jmessagedemo.constant.DATA
import com.rl.jmessagedemo.constant.GROUP_ADD
import com.rl.jmessagedemo.constant.NEW_GROUP
import com.rl.jmessagedemo.constant.TYPE
import com.rl.jmessagedemo.databinding.ActivityNewGroupChatBinding
import com.rl.jmessagedemo.emp.NewGroupBean
import com.rl.jmessagedemo.viewmodel.DashboardViewModel

class NewGroupChatActivity : BaseActivity() {
    private val binding: ActivityNewGroupChatBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_new_group_chat)
    }
    private val viewModel: DashboardViewModel by viewModels()
    private val mAdapter by lazy {
        NewGroupChatAdapter()
    }
    private val alreadyList by lazy {
        intent.extras?.getSerializable("alreadyList") as ArrayList<*>
    }
    private var type = NEW_GROUP    //默认当前页面是新建群聊
    private var groupId = 0L    //默认当前页面是新建群聊

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewModel.friendListLiveData.observe(this) {
            val list = mutableListOf<NewGroupBean>()
            it.forEach { dataBean ->
                list.add(NewGroupBean(dataBean.userInfo))
            }
            if (intent.extras != null)
                alreadyList.forEach {
                    //系统版本小于24则不会隐藏已有的群成员       这里偷个懒！！！
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        list.removeIf { dateBean -> dateBean.userInfo.userID == it }
                    }
                }
            mAdapter.submitList(list)
        }

    }

    private fun initView() {
        intent.extras?.let {
            type = it.getInt(TYPE, NEW_GROUP)
            groupId = it.getLong(DATA, 0L)
        }
        with(binding) {
            recyclerView.apply {
                setHasFixedSize(true)
                adapter = mAdapter
                layoutManager = LinearLayoutManager(this@NewGroupChatActivity)
            }
            tvConfirm.setOnClickListener {
                val userNameList = mutableListOf<String>()
                mAdapter.currentList.forEach {
                    if (it.select)
                        userNameList.add(it.userInfo.userName)
                }
                if (userNameList.isEmpty()) {
                    ToastUtils.showLong("请选择群聊人员")
                    return@setOnClickListener
                }
                if (type == NEW_GROUP) {//新建群聊
                    JMessageClient.createGroup("未命名群", "暂无描述", object : CreateGroupCallback() {
                        override fun gotResult(p0: Int, p1: String?, groupID: Long) {
                            if (p0 == 0) {
                                JMessageClient.addGroupMembers(
                                    groupID,
                                    userNameList,
                                    object : BasicCallback() {
                                        override fun gotResult(p0: Int, p1: String?) {
                                            if (p0 == 0) {
                                                ToastUtils.showLong("添加成功")
                                                setResult(Activity.RESULT_OK)
                                                finish()
                                            } else {
                                                ToastUtils.showLong("添加失败$p1")
                                            }
                                        }
                                    })
                            } else
                                ToastUtils.showLong("创建失败$p1")
                        }
                    })
                } else if (type == GROUP_ADD) {//添加群聊
                    JMessageClient.addGroupMembers(groupId, userNameList, object : BasicCallback() {
                        override fun gotResult(p0: Int, p1: String?) {
                            if (p0 == 0) {
                                ToastUtils.showLong("添加成功")
                                setResult(Activity.RESULT_OK)
                                finish()
                            } else {
                                ToastUtils.showLong("添加失败$p1")
                            }
                        }

                    })
                }
            }
        }
    }
}