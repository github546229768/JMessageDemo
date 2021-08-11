package com.rl.jmessagedemo.extensions

import android.content.Context
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**

 * @Auther: 杨景

 * @datetime: 2021/8/2

 * @desc:

 */
object CrashUtil {
    @JvmStatic
    fun initCrash(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread: Thread?, throwable: Throwable ->
            //获取崩溃时的UNIX时间戳
            val timeMillis = System.currentTimeMillis()
            val stringBuilder =
                StringBuilder(SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date(timeMillis)))
            stringBuilder.append(":\n")
            //获取错误信息
            stringBuilder.append(throwable.message)
            stringBuilder.append("\n")
            //获取堆栈信息
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            stringBuilder.append(sw.toString())

            val errorLog = stringBuilder.toString()

//            val filePath = getExternalFilesDir(null)?.absolutePath + File.separator + "errorLog.txt"//外部
            val filePath = context.filesDir.absolutePath + "/errorLog.txt"//内部


//            val file = File(context.filesDir, "picFromCamera") //在sd卡目录下新建picFromCamera目录

            Log.i("TAG-------->", "initCrash: $filePath")

            File(filePath).appendText(errorLog) //覆盖原先的文本内容

            defaultHandler?.uncaughtException(thread!!, throwable)
        }
    }

}