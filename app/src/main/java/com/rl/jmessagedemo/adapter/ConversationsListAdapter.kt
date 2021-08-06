package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.constant.GROUP_CHAT_TYPE
import com.rl.jmessagedemo.constant.SINGLE_CHAT_TYPE
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import com.rl.jmessagedemo.ui.activity.ChatActivity
import java.text.SimpleDateFormat
import java.util.*


class ConversationsListAdapter : RecyclerView.Adapter<DataBindingViewHolder<Conversation>>() {
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
    private var currentList = mutableListOf<Conversation>()

    //对外提供的刷新列表
    fun updateList(newList: MutableList<Conversation>) {
        this.currentList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<Conversation> {
        val inflate = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            R.layout.view_conversion_item,
            parent,
            false
        )
        return DataBindingViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<Conversation>, position: Int) {
        holder.bind(currentList[position])
        with(holder.itemView) {
            findViewById<View>(R.id.layout_item).setOnClickListener {
                var key = ""
                var type = 0
                var title = ""
                when (currentList[position].type) {
                    ConversationType.single -> {
                        val userInfo = currentList[position].targetInfo as UserInfo
                        key = userInfo.userName
                        title =
                            if (userInfo.nickname.isNullOrEmpty()) userInfo.userName else userInfo.nickname
                        type = SINGLE_CHAT_TYPE
                    }
                    ConversationType.group -> {
                        val groupInfo = currentList[position].targetInfo as GroupInfo
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
                Collections.swap(currentList, position, 0)
                notifyItemMoved(position, 0)
                notifyItemRangeChanged(0, position + 1)
            }
            findViewById<View>(R.id.textView12).setOnClickListener {
                val info: Any
                when (currentList[position].type) {
                    ConversationType.group -> {
                        info = currentList[position].targetInfo as GroupInfo
                        if (JMessageClient.deleteGroupConversation(info.groupID)) {
                            updateData(position)
                        }
                    }
                    ConversationType.single -> {
                        info = currentList[position].targetInfo as UserInfo
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
        currentList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, currentList.size - position)
    }

    override fun getItemCount() = currentList.size

}
