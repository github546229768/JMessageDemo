package com.rl.jmessagedemo.extensions

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
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
    } ?: image.setImageResource(R.mipmap.ic_login_3party_wechat)
}

@BindingAdapter("loadImageBitmap", requireAll = false)
fun loadImageBitmap(image: ImageView, any: Any?) {
    if (any is UserInfo) {
        any.getAvatarBitmap(object : GetAvatarBitmapCallback() {
            override fun gotResult(i: Int, s: String, bitmap: Bitmap?) {
                if (i == 0) {
                    bitmap?.let {
                        image.setImageBitmap(it)
                    } ?: image.setImageResource(R.mipmap.ic_login_3party_wechat)
                } else
                    image.setImageResource(R.mipmap.ic_login_3party_wechat)
            }
        })
    } else if (any is GroupInfo) {
        any.getAvatarBitmap(object : GetAvatarBitmapCallback() {
            override fun gotResult(i: Int, s: String, bitmap: Bitmap?) {
                if (i == 0) {
                    bitmap?.let {
                        image.setImageBitmap(it)
                    } ?: image.setImageResource(R.mipmap.ic_login_3party_wechat)
                } else
                    image.setImageResource(R.mipmap.ic_login_3party_wechat)
            }
        })
    }
}


/*设置备注如果没有备注则显示账号*/
@BindingAdapter(value = ["nickName"])
fun nickName(textView: TextView, userInfo: UserInfo?) {
    userInfo?.let {
        textView.text =
            if (it.nickname.isNullOrEmpty()) it.userName else it.nickname
    }
}

@BindingAdapter("nickName", requireAll = false)
fun nickName(textView: TextView, any: Any?) {
    if (any is UserInfo) {
        textView.text = if (any.nickname.isNullOrEmpty()) any.userName else any.nickname
    } else if (any is GroupInfo) {
        textView.text = any.groupName
    }
}