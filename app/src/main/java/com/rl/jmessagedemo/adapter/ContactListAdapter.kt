package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import cn.jpush.im.api.BasicCallback
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.constant.SINGLE_CHAT_TYPE
import com.rl.jmessagedemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import com.rl.jmessagedemo.emp.MyUserInfo
import com.rl.jmessagedemo.ui.activity.ChatActivity


class ContactListAdapter : DataBindingListAdapter<MyUserInfo>(DiffCallback) {

    override fun getLayoutId() = R.layout.view_contact_item

    override fun onBindViewHolder(holder: DataBindingViewHolder<MyUserInfo>, position: Int) {
        super.onBindViewHolder(holder, position)
        with(holder.itemView) {
            val firstLetter = findViewById<TextView>(R.id.firstLetter)
            setOnClickListener {
                ActivityUtils.startActivity(
                    Intent(
                        holder.itemView.context,
                        ChatActivity::class.java
                    ).apply {
                        putExtra("username", getItem(position).userInfo.userName)
                        putExtra("type", SINGLE_CHAT_TYPE)
                        putExtra(
                            "title",
                            if (getItem(position).userInfo.nickname.isNullOrEmpty()) getItem(
                                position
                            ).userInfo.userName else getItem(
                                position
                            ).userInfo.nickname
                        )
                    })
            }
            setOnLongClickListener {
                with(AlertDialog.Builder(context)) {
                    setTitle("提示")
                    setMessage("将要从好友列表中删除${getItem(position).userInfo.userName},是否确定？")
                    setNegativeButton("确定删除") { _, _ ->
                        getItem(position).userInfo.removeFromFriendList(object : BasicCallback() {
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
            if (getItem(position).isShowFirst!!) {
                firstLetter.visibility = View.VISIBLE
                firstLetter.text = "${getItem(position).firstLetter}"
            } else
                firstLetter.visibility = View.GONE
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<MyUserInfo>() {
        override fun areItemsTheSame(oldItem: MyUserInfo, newItem: MyUserInfo): Boolean {
            return oldItem.userInfo.userID == newItem.userInfo.userID
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: MyUserInfo, newItem: MyUserInfo): Boolean {
            return oldItem == newItem
        }
    }

}