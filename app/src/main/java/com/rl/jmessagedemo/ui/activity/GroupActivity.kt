package com.rl.jmessagedemo.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.GroupAdapter
import com.rl.jmessagedemo.constant.IS_UPDATE_GROUP_INFO
import com.rl.jmessagedemo.databinding.ActivityGroupBinding
import com.rl.jmessagedemo.viewmodel.GroupViewModel

/**

 * @Auther: 杨景

 * @datetime: 2021/8/6

 * @desc:

 */
class GroupActivity : BaseActivity() {
    private val binding: ActivityGroupBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_group)
    }
    private val viewModel by viewModels<GroupViewModel>()
    private val mAdapter = GroupAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewModel.fetchData()
        viewModel.groupInfoLiveData.observe(this) {
            mAdapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.getInstance(Context.MODE_PRIVATE).getBoolean(IS_UPDATE_GROUP_INFO, false)) {
            viewModel.fetchData()
            SPUtils.getInstance(Context.MODE_PRIVATE).put(IS_UPDATE_GROUP_INFO, false)
        }
    }


    private fun initView() {
        with(binding) {
            recyclView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
        }
    }
}