package com.rl.jmessagedemo.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.ChatDetailAdapter
import com.rl.jmessagedemo.constant.*
import com.rl.jmessagedemo.databinding.ActivityChatDetailBinding
import com.rl.jmessagedemo.extensions.PictureSelectorUtil
import com.rl.jmessagedemo.viewmodel.ChatDetailViewModel
import java.io.File

class ChatDetailActivity : BaseActivity() {
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private val binding: ActivityChatDetailBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_chat_detail)
    }
    private val viewModel: ChatDetailViewModel by viewModels()
    private val mAdapter by lazy {
        ChatDetailAdapter()
    }
    private val groupId by lazy {
        intent.getStringExtra("username")!!.toLong()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.fetchData(groupId)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList != null && selectList.size == 1 && selectList[0] != null) {
                val groupInfo =
                    JMessageClient.getGroupConversation(groupId).targetInfo as GroupInfo
                groupInfo.updateAvatar(File(selectList[0].path), "", object : BasicCallback() {
                    override fun gotResult(p0: Int, p1: String?) {
                        if (p0 == 0) {
                            SPUtils.getInstance(Context.MODE_PRIVATE)
                                .put(IS_UPDATE_GROUP_INFO, true)
                            ToastUtils.showLong("更新群头像成功")
                        } else
                            ToastUtils.showLong("更新群头像失败$p1")
                    }
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewModel.fetchData(groupId)
        viewModel.groupMemberLiveData.observe(this) {
            mAdapter.submitList(it)
        }
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAdapter.setOnFootItemClickListener(object : ChatDetailAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (position == mAdapter.currentList.size - 1) {
                    Intent(this@ChatDetailActivity, NewGroupChatActivity::class.java).apply {
                        putExtra(TYPE, GROUP_ADD)
                        putExtra(DATA, groupId)
                        resultLauncher.launch(this)
                    }
                }
            }
        })
        with(binding) {
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 4)
                adapter = mAdapter
            }
            layoutUpdateName.setOnClickListener {
                val editText = EditText(this@ChatDetailActivity).apply {
                    hint = "新的群名"
                }
                with(AlertDialog.Builder(this@ChatDetailActivity)) {
                    setTitle("请输入新的群名")
                    setView(editText)
                    setNegativeButton("确定") { _, _ ->
                        val groupInfo =
                            JMessageClient.getGroupConversation(groupId).targetInfo as GroupInfo
                        groupInfo.updateName("${editText.text}", object : BasicCallback() {
                            override fun gotResult(p0: Int, p1: String?) {
                                if (p0 == 0) {
                                    SPUtils.getInstance(Context.MODE_PRIVATE)
                                        .put(UPDATE_GROUP_NAME, "${editText.text}")
                                    SPUtils.getInstance(Context.MODE_PRIVATE)
                                        .put(IS_UPDATE_GROUP_INFO, true)
                                    ToastUtils.showLong("修改群名成功")
                                } else
                                    ToastUtils.showLong("修改群名失败$p1")
                            }
                        })
                    }
                    setNeutralButton("取消") { _, _ -> }
                    create()
                }.show()
            }
            layoutUpdateAvatar.setOnClickListener {
                PictureSelectorUtil.openGallerySingle(
                    this@ChatDetailActivity,
                    isCompress = false,
                    isCrop = false,
                    requestCode = REQUEST_CODE_PHOTO
                )
            }
        }
    }
}