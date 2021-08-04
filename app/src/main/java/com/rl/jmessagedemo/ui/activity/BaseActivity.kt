package com.rl.jmessagedemo.ui.activity

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

/**

 * @Auther: 杨景

 * @datetime: 2021/8/3

 * @desc:

 */
open class BaseActivity : AppCompatActivity() {
     private val inputService by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
     }


    open fun hideSoftKeyboard(){
        inputService.hideSoftInputFromWindow(currentFocus?.windowToken, 0) //获取当前焦点
    }

}