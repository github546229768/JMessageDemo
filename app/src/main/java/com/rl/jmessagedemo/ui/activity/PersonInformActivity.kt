package com.rl.jmessagedemo.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.constant.REQUEST_CODE_TWO
import com.rl.jmessagedemo.databinding.ActivityPersonInformBinding
import com.rl.jmessagedemo.viewmodel.PersonInfoViewModel
import java.io.File

/**

 * @Auther: 杨景

 * @datetime: 2021/8/5

 * @desc:

 */
class PersonInformActivity : BaseActivity() {
    private val binding: ActivityPersonInformBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_person_inform)
    }
    private val viewModel by viewModels<PersonInfoViewModel>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TWO) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList != null && selectList.size == 1 && selectList[0] != null) {
                JMessageClient.updateUserAvatar(File(selectList[0].path), object : BasicCallback() {
                    override fun gotResult(p0: Int, p1: String?) {
                        if (p0 == 0) {
                            ToastUtils.showLong("更新头像成功")
                            viewModel.fetchData()
                        } else
                            ToastUtils.showLong("更新头像失败$p1")
                    }

                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun initView() {
        with(binding) {
            layoutHead.setOnClickListener {
                //选图片
                PictureSelector.create(this@PersonInformActivity)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(1)
                    .minSelectNum(1)
                    .selectionMode(PictureConfig.SINGLE)
                    .previewImage(true)
                    .compress(true)
                    .forResult(REQUEST_CODE_TWO)
            }
            layoutName.setOnClickListener {
                val editText = EditText(this@PersonInformActivity).apply {
                    hint = "昵称"
                }
                with(AlertDialog.Builder(this@PersonInformActivity)) {
                    setTitle("请输入昵称")
                    setView(editText)
                    setNegativeButton("确定") { _, _ ->
                        val userInfo = JMessageClient.getMyInfo()
                        userInfo.nickname = editText.text.toString()
                        JMessageClient.updateMyInfo(
                            UserInfo.Field.nickname,
                            userInfo,
                            object : BasicCallback() {
                                override fun gotResult(p0: Int, p1: String?) {
                                    if (p0 == 0) {
                                        ToastUtils.showLong("更新昵称成功")
                                        viewModel.fetchData()
                                    } else
                                        ToastUtils.showLong("更新昵称失败$p1")
                                }

                            })
                    }
                    setNeutralButton("取消") { _, _ -> }
                    create()
                }.show()
            }
            layoutSex.setOnClickListener {
                val build = OptionsPickerBuilder(
                    this@PersonInformActivity
                ) { options1, _, _, _ ->
                    val userInfo = JMessageClient.getMyInfo()
                    if (options1 == 0) {
                        userInfo.gender = UserInfo.Gender.male
                    } else {
                        userInfo.gender = UserInfo.Gender.female
                    }
                    JMessageClient.updateMyInfo(
                        UserInfo.Field.gender,
                        userInfo,
                        object : BasicCallback() {
                            override fun gotResult(p0: Int, p1: String?) {
                                if (p0 == 0) {
                                    viewModel.fetchData()
                                    ToastUtils.showLong("性别更新成功")
                                } else
                                    ToastUtils.showLong("性别更新失败$p1")
                            }
                        })
                }.build<String>()
                build.setPicker(arrayListOf("男", "女"))
                build.show()
            }
        }
    }

}