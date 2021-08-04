package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import cn.jpush.im.android.api.content.MessageContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.model.Message
import com.example.gallerydemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue


class MessageListAdapter : DataBindingListAdapter<Message>(DiffCallback) {
    companion object {
        const val ITEM_TYPE_SEND_MESSAGE = 0
        const val ITEM_TYPE_RECEIVE_MESSAGE = 1

        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        @BindingAdapter("time", requireAll = false)
        fun simpleDateTime(view: TextView, time: Long) {
            view.text =
                if (DateUtils.isToday(time)) SimpleDateFormat("HH:mm").format(time)
                else SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(time)
        }

        @JvmStatic
        @BindingAdapter("text", requireAll = false)
        fun simpleText(view: TextView, messageContent: MessageContent) {
            when(messageContent.contentType){
                ContentType.text -> {
                    view.text = (messageContent as TextContent).text
                }
                ContentType.eventNotification ->{
                    view.text = "此处是自定义通知文字"
                }
                else ->{
                    view.text = "非文本消息"
                }
            }
        }
    }


    override fun getItemViewType(position: Int) =
        if (currentList[position].direct == MessageDirect.send) ITEM_TYPE_SEND_MESSAGE else ITEM_TYPE_RECEIVE_MESSAGE

    override fun getLayoutId() = R.layout.view_send_message_item


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<Message> {
        return if (viewType == ITEM_TYPE_SEND_MESSAGE)
            super.onCreateViewHolder(parent, viewType)
        else {
            val inflate = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                R.layout.view_receive_message_item,
                parent,
                false
            )
            return DataBindingViewHolder(inflate)
        }
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<Message>, position: Int) {
        super.onBindViewHolder(holder, position)
        val time = holder.itemView.findViewById<TextView>(R.id.time)
        if (position != 0) {
            val distanceTime =
                (getItem(position).createTime - getItem(position - 1).createTime).absoluteValue
            time.isVisible = distanceTime > 30 * 1000
        }
    }


    object DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }


}
