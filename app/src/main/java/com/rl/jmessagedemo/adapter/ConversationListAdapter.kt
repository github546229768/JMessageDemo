package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.constant.GROUP_CHAT_TYPE
import com.rl.jmessagedemo.constant.SINGLE_CHAT_TYPE
import com.rl.jmessagedemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import com.rl.jmessagedemo.ui.activity.ChatActivity
import java.text.SimpleDateFormat


class ConversationListAdapter : DataBindingListAdapter<Conversation>(DiffCallback) {
    companion object {
        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        @BindingAdapter("simpleDateTime", requireAll = false)
        fun simpleDateTime(view: TextView, time: Long) {
            view.text =
                if (DateUtils.isToday(time)) SimpleDateFormat("HH:mm").format(time)
                else SimpleDateFormat("yyyy/MM/dd HH:mm").format(time)
        }
    }

    override fun getLayoutId() = R.layout.view_conversion_item

    override fun onBindViewHolder(holder: DataBindingViewHolder<Conversation>, position: Int) {
        super.onBindViewHolder(holder, position)
        with(holder.itemView) {
            findViewById<View>(R.id.layout_item).setOnClickListener {
                var key = ""
                var type = 0
                var title = ""
                when (getItem(position).type) {
                    ConversationType.single -> {
                        val userInfo = getItem(position).targetInfo as UserInfo
                        key = userInfo.userName
                        title =
                            if (userInfo.nickname.isNullOrEmpty()) userInfo.userName else userInfo.nickname
                        type = SINGLE_CHAT_TYPE
                    }
                    ConversationType.group -> {
                        val groupInfo = getItem(position).targetInfo as GroupInfo
                        key = groupInfo.groupID.toString()
                        title = groupInfo.groupName
                        type = GROUP_CHAT_TYPE
                    }
                    else -> {  //暂定
                    }
                }
                if (key.isEmpty() || type == 0) {
                    ToastUtils.showLong("没有该会话！")
                    return@setOnClickListener
                }
                ActivityUtils.startActivity(
                    Intent(holder.itemView.context, ChatActivity::class.java).apply {
                        putExtra("title", title)
                        putExtra("username", key)
                        putExtra("type", type)
                    })
            }
            findViewById<View>(R.id.textView11).setOnClickListener {
//                Collections.swap(newCurrentList, position, 0)
//                notifyItemRangeChanged(position,position+1 )
//                submitList(newCurrentList)
                findViewById<SwipeMenuLayout>(R.id.swipeMenuLayout).isGone = true
                ToastUtils.showLong("暂未开通")
            }
            findViewById<View>(R.id.textView12).setOnClickListener {
                val info: Any
                when (getItem(position).type) {
                    ConversationType.group -> {
                        info = getItem(position).targetInfo as GroupInfo
                        if (JMessageClient.deleteGroupConversation(info.groupID)) {
                            updateData(position)
                        }
                    }
                    ConversationType.single -> {
                        info = getItem(position).targetInfo as UserInfo
                        if (JMessageClient.deleteSingleConversation(info.userName)) {
                            updateData(position)
                        }
                    }
                    else -> ToastUtils.showLong("暂未开通")
                }
            }
        }
    }

    private fun updateData(position: Int) {
        val newCurrentList = currentList.toMutableList()
        LogUtils.i(position)
//        notifyItemRemoved(position)
//        notifyItemRangeRemoved(position, newCurrentList.size - position)
        submitList(newCurrentList)
    }

    object DiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation) =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation) =
            oldItem == newItem
    }
}
