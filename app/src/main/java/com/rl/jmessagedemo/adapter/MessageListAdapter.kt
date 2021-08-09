package com.rl.jmessagedemo.adapter

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.enums.MessageStatus
import cn.jpush.im.android.api.model.Message
import com.blankj.utilcode.util.ActivityUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.databinding.DataBindingListAdapter
import com.rl.jmessagedemo.databinding.DataBindingViewHolder
import com.rl.jmessagedemo.ui.activity.PreviewImageActivity
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue


class MessageListAdapter : DataBindingListAdapter<Message>(DiffCallback) {
    companion object {
        const val ITEM_TYPE_SEND_MESSAGE = 0   //文本发送视图
        const val ITEM_TYPE_RECEIVE_MESSAGE = 1 //文本接收试图
        const val ITEM_TYPE_SEND_IMG_MESSAGE = 2 //图片发送试图
        const val ITEM_TYPE_RECEIVE_IMG_MESSAGE = 3 //图片接收试图
        const val ITEM_TYPE_SEND_VOICE_MESSAGE = 4 //语音发送试图
        const val ITEM_TYPE_RECEIVE_VOICE_MESSAGE = 5 //语音接收试图

        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        @BindingAdapter("time", requireAll = false)
        fun simpleDateTime(view: TextView, time: Long) {
            view.text =
                if (DateUtils.isToday(time)) SimpleDateFormat("HH:mm").format(time)
                else SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(time)
        }

        @JvmStatic
        @BindingAdapter("isMessageStatus", requireAll = false)
        fun isMessageStatus(view: View, message: Message) {
            when (message.status) {
                MessageStatus.send_success -> {
                    view.isVisible = false
                    view.isVisible = false
                }
                MessageStatus.send_fail -> {
                    if (view is ProgressBar)
                        view.isVisible = false
                    else if (view is ImageView)
                        view.isVisible = true
                }
                MessageStatus.send_going -> {
                    if (view is ProgressBar)
                        view.isVisible = true
                    else if (view is ImageView)
                        view.isVisible = false
                }
                else -> {
                    view.isVisible = false
                    view.isVisible = false
                }
            }
        }
    }

    override fun getItemViewType(position: Int) =
        when (getItem(position).content.contentType) {
            ContentType.text -> {
                if (getItem(position).direct == MessageDirect.send) ITEM_TYPE_SEND_MESSAGE
                else ITEM_TYPE_RECEIVE_MESSAGE
            }
            ContentType.image -> {
                if (getItem(position).direct == MessageDirect.send) ITEM_TYPE_SEND_IMG_MESSAGE
                else ITEM_TYPE_RECEIVE_IMG_MESSAGE
            }
            ContentType.voice -> {
                if (getItem(position).direct == MessageDirect.send) ITEM_TYPE_SEND_VOICE_MESSAGE
                else ITEM_TYPE_RECEIVE_VOICE_MESSAGE
            }
            else -> {
                if (getItem(position).direct == MessageDirect.send) ITEM_TYPE_SEND_MESSAGE
                else ITEM_TYPE_RECEIVE_MESSAGE
            }
        }

    override fun getLayoutId() = R.layout.view_send_message_item
    private fun getLayoutId2() = R.layout.view_receive_message_item
    private fun getImageSendLayoutId() = R.layout.view_img_send_message_item
    private fun getImageReceiveLayoutId() = R.layout.view_img_receive_message_item
    private fun getVoiceSendLayoutId() = R.layout.view_voice_send_message_item
    private fun getVoiceReceiveLayoutId() = R.layout.view_voice_receive_message_item

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<Message> {
        return when (viewType) {
            ITEM_TYPE_SEND_MESSAGE -> super.onCreateViewHolder(parent, viewType)
            ITEM_TYPE_RECEIVE_MESSAGE ->
                bindingViewHolder(getLayoutId2(), parent)
            ITEM_TYPE_SEND_IMG_MESSAGE ->
                bindingViewHolder(getImageSendLayoutId(), parent)
            ITEM_TYPE_RECEIVE_IMG_MESSAGE ->
                bindingViewHolder(getImageReceiveLayoutId(), parent)
            ITEM_TYPE_SEND_VOICE_MESSAGE ->
                bindingViewHolder(getVoiceSendLayoutId(), parent)
            ITEM_TYPE_RECEIVE_VOICE_MESSAGE ->
                bindingViewHolder(getVoiceReceiveLayoutId(), parent)
            else ->
                super.onCreateViewHolder(parent, viewType)
        }
    }

    //绑定DataBindingViewHolder
    private fun bindingViewHolder(@LayoutRes layoutId: Int, parent: ViewGroup): DataBindingViewHolder<Message> =
        DataBindingViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutId,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: DataBindingViewHolder<Message>, position: Int) {
        super.onBindViewHolder(holder, position)
        with(holder.itemView) {
            val time = findViewById<TextView>(R.id.time)
            if (position != 0) {
                val distanceTime =
                    (getItem(position).createTime - getItem(position - 1).createTime).absoluteValue
                time.isVisible = distanceTime > 120 * 1000
            }
            if (holder.itemViewType == ITEM_TYPE_SEND_IMG_MESSAGE
                || holder.itemViewType == ITEM_TYPE_RECEIVE_IMG_MESSAGE
            ) {
                val imageView: ImageView = findViewById(R.id.imageview)
                imageView.setOnClickListener {
                    val imageContent = getItem(position).content as ImageContent
                    ActivityUtils.startActivity(
                        Intent(context, PreviewImageActivity::class.java).apply {
                            putExtra(
                                "imgUrl",
                                imageContent.localPath ?: imageContent.localThumbnailPath
                            )
                        },
                        ActivityOptions.makeSceneTransitionAnimation(
                            ActivityUtils.getTopActivity(),
                            imageView,
                            "shared"
                        ).toBundle()
                    )
                }
            }
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
