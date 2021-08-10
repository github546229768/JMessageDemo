package com.rl.jmessagedemo.extensions

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.content.*
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.bumptech.glide.Glide
import com.rl.jmessagedemo.R
import com.sqk.emojirelease.EmojiUtil

/**

 * @Auther: 杨景

 * @datetime: 2021/8/5

 * @desc:kotlin扩展函数

 */

/*加载头像*/
@BindingAdapter(value = ["loadImageBitmap"])
fun loadImageBitmap(image: ImageView, bitmap: Bitmap?) {
    bitmap?.let {
        image.setImageBitmap(it)
    } ?: image.setImageResource(R.drawable.my_avatar)
}

/*重载*/
@BindingAdapter("loadImageBitmap", requireAll = false)
fun loadImageBitmap(image: ImageView, any: Any?) {
    if (any is UserInfo) {
        any.getAvatarBitmap(object : GetAvatarBitmapCallback() {
            override fun gotResult(i: Int, s: String, bitmap: Bitmap?) {
                if (i == 0) {
                    bitmap?.let {
                        image.setImageBitmap(it)
                    } ?: image.setImageResource(R.drawable.my_avatar)
                } else
                    image.setImageResource(R.drawable.my_avatar)
            }
        })
    } else if (any is GroupInfo) {
        any.getAvatarBitmap(object : GetAvatarBitmapCallback() {
            override fun gotResult(i: Int, s: String, bitmap: Bitmap?) {
                if (i == 0) {
                    bitmap?.let {
                        image.setImageBitmap(it)
                    } ?: image.setImageResource(R.drawable.my_avatar)
                } else
                    image.setImageResource(R.drawable.my_avatar)
            }
        })
    }
}
/*end*/

/*设置备注如果没有备注则显示账号*/
@BindingAdapter("nickName", requireAll = false)
fun nickName(textView: TextView, any: Any?) {
    if (any is UserInfo) {
        textView.text = if (any.nickname.isNullOrEmpty()) any.userName else any.nickname
    } else if (any is GroupInfo) {
        textView.text = any.groupName
    }
}
/*end*/

/*处理不同的消息文本类型（私聊文本、群通知、图片）*/
@BindingAdapter("text", requireAll = false)
fun text(view: TextView, messageContent: MessageContent?) {
    when (messageContent?.contentType) {
        ContentType.text -> {
            EmojiUtil.handlerEmojiText(view, (messageContent as TextContent).text, view.context)
        }
        ContentType.eventNotification -> {
            view.text = (messageContent as EventNotificationContent).eventText
        }
        ContentType.image -> {
            view.text = "收到一条图片消息"
        }
        ContentType.voice -> {
            view.apply {
                text = context.getString(
                    R.string.voice_message_duration,
                    (messageContent as VoiceContent).duration
                )
            }
        }
        else -> {
            view.text = "非文本消息"
        }
    }
}
/*end*/

/*加载图片类型消息*/
@BindingAdapter("loadImageMessage", requireAll = false)
fun loadImageMessage(image: ImageView, messageContent: MessageContent?) {
    if (messageContent?.contentType == ContentType.image) {
        Glide.with(image.context).load((messageContent as ImageContent).localThumbnailPath)
            .placeholder(R.drawable.ic_baseline_insert_photo_24)
            .error(R.drawable.ic_baseline_insert_photo_24)
            .into(image)
    }
}
/*end*/