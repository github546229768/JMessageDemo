package com.rl.jmessagedemo.ui.fragment

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.rl.jmessagedemo.R

/**

 * @Auther: 杨景

 * @datetime: 2021/8/3

 * @desc:

 */
open class BaseFragment : Fragment() {
    //加载框
    private val loadDialog by lazy {
        DialogFragment(R.layout.dialog_loading).apply {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.loadDialog)
        }
    }

    fun isLoadDialog(isShow: Boolean) {
        if (isShow) {
            loadDialog.show(parentFragmentManager, "")
        } else loadDialog.dismiss()
        //注意要再Activity销毁之前关闭
    }

}