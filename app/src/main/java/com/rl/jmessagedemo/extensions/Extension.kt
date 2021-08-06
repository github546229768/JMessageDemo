package com.rl.jmessagedemo.extensions

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.content.EventNotificationContent
import cn.jpush.im.android.api.content.MessageContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.rl.jmessagedemo.R

/**

 * @Auther: 杨景

 * @datetime: 2021/8/5

 * @desc:

 */

/*设置头像*/
@BindingAdapter(value = ["loadImageBitmap"])
fun loadImageBitmap(image: ImageView, bitmap: Bitmap?) {
    bitmap?.let {
        image.setImageBitmap(it)
    } ?: image.setImageResource(R.mipmap.head_default)
}

@BindingAdapter("loadImageBitmap", requireAll = false)
fun loadImageBitmap(image: ImageView, any: Any?) {
    if (any is UserInfo) {
        Log.i("TAG-------->", "UserInfo: ${any.userName}")
        any.getAvatarBitmap(object : GetAvatarBitmapCallback() {
            override fun gotResult(i: Int, s: String, bitmap: Bitmap?) {
                if (i == 0) {
                    bitmap?.let {
                        image.setImageBitmap(it)
                    } ?: image.setImageResource(R.mipmap.head_default)
                } else
                    image.setImageResource(R.mipmap.head_default)
            }
        })
    } else if (any is GroupInfo) {
        Log.i("TAG-------->", "GroupInfo: ${any.groupName}")
        any.getAvatarBitmap(object : GetAvatarBitmapCallback() {
            override fun gotResult(i: Int, s: String, bitmap: Bitmap?) {
                if (i == 0) {
                    bitmap?.let {
                        image.setImageBitmap(it)
                    } ?: image.setImageResource(R.mipmap.head_default)
                } else
                    image.setImageResource(R.mipmap.head_default)
            }
        })
    }
}

/*设置备注如果没有备注则显示账号*/

@BindingAdapter("nickName", requireAll = false)
fun nickName(textView: TextView, any: Any?) {
    if (any is UserInfo) {
        textView.text = if (any.nickname.isNullOrEmpty()) any.userName else any.nickname
    } else if (any is GroupInfo) {
        textView.text = any.groupName
    }
}

/*不同的消息类型*/
@BindingAdapter("text", requireAll = false)
fun simpleText(view: TextView, messageContent: MessageContent?) {
    when (messageContent?.contentType) {
        ContentType.text -> {
            view.text = (messageContent as TextContent).text
        }
        ContentType.eventNotification -> {
            view.text = (messageContent as EventNotificationContent).eventText
        }
        else -> {
            view.text = "非文本消息"
        }
    }
}