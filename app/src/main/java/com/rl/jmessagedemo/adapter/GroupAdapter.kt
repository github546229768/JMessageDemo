package com.rl.jmessagedemo.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import cn.jpush.im.android.api.model.GroupInfo
import com.blankj.utilcode.util.ActivityUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.constant.GROUP_CHAT_TYPE
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import com.rl.jmessagedemo.ui.activity.ChatActivity


class GroupAdapter :
    RecyclerView.Adapter<DataBindingViewHolder<GroupInfo>>() {

    private var currentList = mutableListOf<GroupInfo>()

    //对外提供的刷新列表
    fun submitList(newList: MutableList<GroupInfo>) {
        this.currentList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<GroupInfo> {
        val inflate = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_group,
            parent,
            false
        )
        return DataBindingViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<GroupInfo>, position: Int) {
        holder.bind(currentList[position])
        with(holder.itemView) {
            setOnClickListener {
                ActivityUtils.startActivity(
                    Intent(holder.itemView.context, ChatActivity::class.java).apply {
                        putExtra("title", currentList[position].groupName)
                        putExtra("username", currentList[position].groupID.toString())
                        putExtra("type", GROUP_CHAT_TYPE)
                    })
            }
        }
    }


    override fun getItemCount() = currentList.size

}
