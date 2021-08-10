package com.rl.jmessagedemo.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.MessageEvent
import com.blankj.utilcode.util.SPUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.ConversationsListAdapter
import com.rl.jmessagedemo.constant.IS_UPDATE_GROUP_INFO
import com.rl.jmessagedemo.databinding.FragmentNotificationsBinding
import com.rl.jmessagedemo.ui.activity.NewGroupChatActivity
import com.rl.jmessagedemo.viewmodel.NotificationsViewModel


class NotificationsFragment : BaseFragment() {

    private val viewModel: NotificationsViewModel by viewModels()

    private lateinit var binding: FragmentNotificationsBinding

    private val mAdapter by lazy {
        ConversationsListAdapter()
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.fetchData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_notifications,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        JMessageClient.registerEventReceiver(this)
        initView()
        viewModel.conversationLiveData.observe(viewLifecycleOwner) {
            it?.let { data -> mAdapter.updateList(data.toMutableList()) }
        }
        viewModel.loadingEvent.observe(viewLifecycleOwner) {
            isLoadDialog(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.getInstance(Context.MODE_PRIVATE).getBoolean(IS_UPDATE_GROUP_INFO, false)) {
            viewModel.fetchData()
            SPUtils.getInstance(Context.MODE_PRIVATE).put(IS_UPDATE_GROUP_INFO, false)
        }
    }

    override fun onDestroyView() {
        JMessageClient.unRegisterEventReceiver(this)
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.more_option, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_group) {
            resultLauncher.launch(Intent(requireActivity(), NewGroupChatActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        setHasOptionsMenu(true)
        with(binding) {
            recyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
            smartRefreshLayout.setOnRefreshListener {
                it.finishRefresh(1000)
                viewModel.fetchData()
            }
        }
        //功能待定
//        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            /*做到这里来了*/
//            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
//                super.onItemRangeChanged(positionStart, itemCount)
//                Log.i("TAG-------->", "onItemRangeChanged: $positionStart")
//            }
//        })
    }

    /*在线消息通知事件*/
    fun onEvent(event: MessageEvent) {
        viewModel.fetchData()
    }

}