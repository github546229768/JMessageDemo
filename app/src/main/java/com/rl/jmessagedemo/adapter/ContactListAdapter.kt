package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.gallerydemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.constant.SINGLE_CHAT_TYPE
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import com.rl.jmessagedemo.ui.activity.ChatActivity


class ContactListAdapter : DataBindingListAdapter<UserInfo>(DiffCallback) {

    override fun getLayoutId() = R.layout.view_contact_item

    override fun onBindViewHolder(holder: DataBindingViewHolder<UserInfo>, position: Int) {
        super.onBindViewHolder(holder, position)
        with(holder.itemView) {
            setOnClickListener {
                ActivityUtils.startActivity(
                    Intent(
                        holder.itemView.context,
                        ChatActivity::class.java
                    ).apply {
                        putExtra("username", getItem(position).userName)
                        putExtra("type", SINGLE_CHAT_TYPE)
                        putExtra("title", getItem(position).userName)
                    })
            }
            setOnLongClickListener {
                with(AlertDialog.Builder(context)) {
                    setTitle("提示")
                    setMessage("将要从好友列表中删除${getItem(position).userName},是否确定？")
                    setNegativeButton("确定删除") { _, _ ->
                        getItem(position).removeFromFriendList(object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String) {
                                if (0 == responseCode) {
                                    val toMutableList = currentList.toMutableList()
                                    toMutableList.removeAt(position)
                                    submitList(toMutableList)
                                    ToastUtils.showLong("删除好友成功！")
                                } else {
                                    ToastUtils.showLong("移出好友列表失败！")
                                }
                            }
                        })
                    }
                    setNeutralButton("取消") { _, _ -> }
                    create()
                }.show()
                false
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<UserInfo>() {
        override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem.userID == newItem.userID
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem == newItem
        }
    }

}