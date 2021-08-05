package com.rl.jmessagedemo.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**

 * @Auther: 杨景

 * @datetime: 2021/6/18

 * @desc:

 */
abstract class DataBindingListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, DataBindingViewHolder<T>>(diffCallback) {

    abstract fun getLayoutId(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T> {
        val inflate = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            getLayoutId(),
            parent,
            false
        )
        return DataBindingViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) {
        holder.bind(getItem(position))
    }
}