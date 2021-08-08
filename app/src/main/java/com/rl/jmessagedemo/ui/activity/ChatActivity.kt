package com.rl.jmessagedemo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.core.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.MessageListAdapter
import com.rl.jmessagedemo.constant.*
import com.rl.jmessagedemo.databinding.ActivityChatBinding
import com.rl.jmessagedemo.extensions.PictureSelectorUtil
import com.rl.jmessagedemo.extensions.SoftKeyBoardListener
import com.rl.jmessagedemo.viewmodel.ChatViewModel
import com.sqk.emojirelease.Emoji
import com.sqk.emojirelease.FaceFragment


class ChatActivity : BaseActivity(), FaceFragment.OnEmojiClickListener {
    private val binding: ActivityChatBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_chat)
    }
    private val viewModel: ChatViewModel by viewModels()
    private val mAdapter by lazy { MessageListAdapter() }
    private var username = "0" //聊天key   默认为单聊
    private var type = SINGLE_CHAT_TYPE ///聊天类型  默认为单聊
    private var showSound = false

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList != null && selectList.size == 1 && selectList[0] != null) {
                viewModel.sendImageMessage(selectList)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewModel.fetchData(username, type)
        viewModel.allMessageLiveData.observe(this) {
            mAdapter.submitList(it)
            scrollToBottom()
        }
        viewModel.isMessageStatus.observe(this) {
            scrollToBottom()
            mAdapter.notifyItemRemoved(mAdapter.itemCount - 1)
        }
    }

    override fun onResume() {
        super.onResume()
        val updateGroupName =
            SPUtils.getInstance(Context.MODE_PRIVATE).getString(UPDATE_GROUP_NAME, "")
        if (!updateGroupName.isNullOrEmpty()) {
            title = updateGroupName
            SPUtils.getInstance(Context.MODE_PRIVATE).put(UPDATE_GROUP_NAME, "")//重新置空
        }
        /*软键盘监听实现的接口*/
        SoftKeyBoardListener.setListener(this,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    scrollToBottom()
                }

                override fun keyBoardHide(height: Int) {
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return when (type) {
            SINGLE_CHAT_TYPE -> false
            GROUP_CHAT_TYPE -> true
            else -> false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            Intent(this@ChatActivity, ChatDetailActivity::class.java).apply {
                putExtra("type", type)
                putExtra("username", username)
                startActivity(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        username = intent.getStringExtra("username").toString()
        title = intent.getStringExtra("title").toString()
        type = intent.getIntExtra("type", 0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)         //显示返回按钮
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FaceFragment.Instance())
            .commitNow()
        with(binding) {
            recycleview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        hideSoftKeyboard()
                        hideAllViewLayout()
                    }
                    performClick()
                    false
                }
            }
            inputMessage.setOnEditorActionListener { _, _, _ ->
                viewModel.sendMessage(inputMessage.text.toString())
                inputMessage.text?.clear()
                true
            }
            ivSound.setOnClickListener {
                hideSoftKeyboard()
                hideAllViewLayout()
                inputMessage.clearFocus()
                if (showSound) {
                    tvSound.visibility = View.INVISIBLE
                    inputMessage.visibility = View.VISIBLE
                    ivSound.setImageResource(R.mipmap.sound_record)
                } else {
                    tvSound.visibility = View.VISIBLE
                    inputMessage.visibility = View.INVISIBLE
                    ivSound.setImageResource(R.mipmap.icon_softkeyboard)
                }
                showSound = !showSound
            }
            inputMessage.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {//获取到焦点
                    hideAllViewLayout()
                }
            }
            ivEmoji.setOnClickListener {
                initUiStyle(frameLayout, true)
            }
            ivOption.setOnClickListener {
                initUiStyle(moreOptionLayout, false)
            }
            //打开图库
            photo.setOnClickListener {
                PictureSelectorUtil.openGallerySingle(this@ChatActivity,
                    isCompress = false,
                    isCrop = false,
                    requestCode = REQUEST_CODE_PHOTO
                )
            }
            //打开相机
            camera.setOnClickListener {
                PictureSelectorUtil.openCamera(this@ChatActivity,
                    isCompress = false,
                    isCrop = false,
                    requestCode = REQUEST_CODE_PHOTO
                )
            }
            //打开文件
            file.setOnClickListener {
                ToastUtils.showLong("暂无完善")
            }

        }
    }

    //初始化去软键盘，显示Fragment
    private fun ActivityChatBinding.initUiStyle(showLayout: ViewGroup, isEmotionLayout: Boolean) {
        hideSoftKeyboard()
        inputMessage.clearFocus()
        // 延迟一会，让键盘先隐藏再显示表情键盘，否则会有一瞬间表情键盘和软键盘同时显示
        Handler(Looper.getMainLooper()).postDelayed({
            if (isEmotionLayout)
                binding.moreOptionLayout.visibility = View.INVISIBLE
            val showAnim = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
            )
            showAnim.duration = 300
            showLayout.startAnimation(showAnim)
            showLayout.isVisible = true
            tvSound.visibility = View.VISIBLE
            inputMessage.visibility = View.VISIBLE
            ivSound.setImageResource(R.mipmap.sound_record)
            showSound = false
            scrollToBottom()
        }, 50)
    }

    //emoji监听回调
    override fun onEmojiDelete() {
        val text: String = binding.inputMessage.text.toString()
        if (text.isEmpty()) {
            return
        }
        if ("]" == text.substring(text.length - 1, text.length)) {
            val index = text.lastIndexOf("[")
            if (index == -1) {
                val action = KeyEvent.ACTION_DOWN
                val code = KeyEvent.KEYCODE_DEL
                val event = KeyEvent(action, code)
                binding.inputMessage.onKeyDown(KeyEvent.KEYCODE_DEL, event)
                return
            }
            binding.inputMessage.text.delete(index, text.length)
            return
        }
        val action = KeyEvent.ACTION_DOWN
        val code = KeyEvent.KEYCODE_DEL
        val event = KeyEvent(action, code)
        binding.inputMessage.onKeyDown(KeyEvent.KEYCODE_DEL, event)
    }

    override fun onEmojiClick(emoji: Emoji?) {
        if (emoji != null) {
            val index: Int = binding.inputMessage.selectionStart
            val editable: Editable = binding.inputMessage.editableText
            if (index < 0) {
                editable.append(emoji.content)
            } else {
                editable.insert(index, emoji.content)
            }
        }
    }

    private fun hideAllViewLayout() {
        binding.frameLayout.isVisible = false
        binding.moreOptionLayout.isVisible = false
    }

    private fun scrollToBottom() {
        binding.recycleview.scrollToPosition(viewModel.allMessageLiveData.value!!.size - 1)
    }
}