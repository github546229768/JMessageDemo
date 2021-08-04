package com.rl.jmessagedemo.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.gallerydemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.emp.ContactEmp


class AddFriendListAdapter : DataBindingListAdapter<ContactEmp>(DiffCallback) {

    override fun getLayoutId() = R.layout.view_add_friend_item


    object DiffCallback : DiffUtil.ItemCallback<ContactEmp>() {
        override fun areItemsTheSame(oldItem: ContactEmp, newItem: ContactEmp): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: ContactEmp, newItem: ContactEmp): Boolean {
            return oldItem == newItem
        }
    }

}

