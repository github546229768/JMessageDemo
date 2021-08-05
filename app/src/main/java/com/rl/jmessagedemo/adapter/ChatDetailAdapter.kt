package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import cn.jpush.im.android.api.model.GroupMemberInfo
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.databinding.DataBindingViewHolder

/**

 * @Auther: 杨景

 * @datetime: 2021/8/5

 * @desc:

 */
class ChatDetailAdapter : DataBindingListAdapter<GroupMemberInfo>(DiffCallback),
    View.OnClickListener {
    private var mOnItemClickListener:OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnFootItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(view, view.tag as Int)
    }

    override fun getLayoutId() = R.layout.item_chat_detail

    override fun onBindViewHolder(holder: DataBindingViewHolder<GroupMemberInfo>, position: Int) {
        super.onBindViewHolder(holder, position)
        getItem(position) ?: kotlin.run {
            holder.itemView.findViewById<View>(R.id.rl_add).apply {
                setOnClickListener(this@ChatDetailAdapter)
                tag = position
            }
            return
        }
        holder.itemView.apply {
            findViewById<ImageView>(R.id.iv).isVisible = true
            findViewById<TextView>(R.id.tv).isVisible = true
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<GroupMemberInfo>() {
        override fun areItemsTheSame(oldItem: GroupMemberInfo, newItem: GroupMemberInfo): Boolean {
            return oldItem.userInfo.userID == newItem.userInfo.userID
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: GroupMemberInfo,
            newItem: GroupMemberInfo
        ): Boolean {
            return oldItem == newItem
        }
    }

}