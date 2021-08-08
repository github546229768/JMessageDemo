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
import cn.jpush.im.android.api.model.Message
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
            mAdapter.updateList(it.toMutableList())
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
        }
    }

    /*在线消息通知事件*/
    fun onEvent(event: MessageEvent) {
        val msg: Message = event.message
        viewModel.fetchData()
//        when (msg.contentType) {
//            ContentType.text -> {
//                val text = (msg.content as TextContent).text
//                Log.i("TAG-------->", "onEvent: ${text}")
//            }
//            ContentType.image -> {
//                val text = (msg.content as ImageContent).localPath
//                Log.i("TAG-------->", "onEvent: ${text}")
//            }
//            ContentType.voice -> {
////                val voiceContent = msg.content
////                voiceContent.localPath //语音文件本地地址
////                voiceContent.duration //语音文件时长
//            }
//            ContentType.eventNotification -> {
//                //处理事件提醒消息
//                when ((msg.content as EventNotificationContent).eventNotificationType) {
//                    EventNotificationType.group_member_added -> {
//                        //群成员加群事件
//                    }
//                    EventNotificationType.group_member_removed -> {
//                        //群成员被踢事件
//                    }
//                    EventNotificationType.group_member_exit -> {
//                        //群成员退群事件
//                    }
//                    EventNotificationType.group_info_updated -> {
//                        //群信息变更事件
//                    }
//                    else -> {}
//                }
//            }
//            else ->{}
//        }
    }

}