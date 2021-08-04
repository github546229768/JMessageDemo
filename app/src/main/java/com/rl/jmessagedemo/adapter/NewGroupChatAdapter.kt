package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.gallerydemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import com.rl.jmessagedemo.emp.NewGroupBean

/**

 * @Auther: 杨景

 * @datetime: 2021/8/4

 * @desc:

 */
class NewGroupChatAdapter : DataBindingListAdapter<NewGroupBean>(DiffCallback) {

    override fun getLayoutId() = R.layout.item_new_group_content

    override fun onBindViewHolder(holder: DataBindingViewHolder<NewGroupBean>, position: Int) {
        super.onBindViewHolder(holder, position)
        with(holder.itemView){
            setOnClickListener {
                getItem(position).select = !getItem(position).select
                notifyItemChanged(position)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<NewGroupBean>() {
        override fun areItemsTheSame(oldItem: NewGroupBean, newItem: NewGroupBean): Boolean {
            return oldItem.userInfo.userID == newItem.userInfo.userID
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: NewGroupBean, newItem: NewGroupBean): Boolean {
            return oldItem == newItem
        }
    }
}