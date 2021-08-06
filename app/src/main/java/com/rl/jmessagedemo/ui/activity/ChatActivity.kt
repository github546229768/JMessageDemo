package com.rl.jmessagedemo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.MessageListAdapter
import com.rl.jmessagedemo.constant.*
import com.rl.jmessagedemo.databinding.ActivityChatBinding
import com.rl.jmessagedemo.extensions.SoftKeyBoardListener
import com.rl.jmessagedemo.ui.fragment.HomeFragment
import com.rl.jmessagedemo.viewmodel.ChatViewModel
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon


class ChatActivity : BaseActivity(), EmojiconsFragment.OnEmojiconBackspaceClickedListener,
    EmojiconGridFragment.OnEmojiconClickedListener {
    private val binding: ActivityChatBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_chat)
    }
    private val viewModel: ChatViewModel by viewModels()
    private val mAdapter by lazy { MessageListAdapter() }
    private var username = "0" //聊天key   默认为单聊
    private var type = SINGLE_CHAT_TYPE ///聊天类型  默认为单聊
    private var showSound = false
    private val emotionFragment by lazy {
        EmojiconsFragment.newInstance(false)
    }
    private val homeFragment by lazy {
        HomeFragment.newInstance()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
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
        //显示返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //初始化表情包Fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayout, emotionFragment)
            .add(R.id.frameLayout, homeFragment)
            .commitNow()

        with(binding) {
            recycleview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
            inputMessage.setOnEditorActionListener { _, _, _ ->
                viewModel.sendMessage(inputMessage.text.toString())
                binding.inputMessage.text?.clear()
                hideSoftKeyboard()
                true
            }
            ivSound.setOnClickListener {
                hideSoftKeyboard()
                binding.frameLayout.isVisible = false
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
                    frameLayout.isVisible = false
                    hideAllFragment()
                }
            }
            ivEmoji.setOnClickListener {
                initUiStyle(arrayOf(emotionFragment, homeFragment))
            }
            ivOption.setOnClickListener {
                initUiStyle(arrayOf(homeFragment, emotionFragment))
            }
        }

        /*软键盘监听实现的接口*/
        SoftKeyBoardListener.setListener(this,object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener{
            override fun keyBoardShow(height: Int) {
                scrollToBottom()
            }

            override fun keyBoardHide(height: Int) {
                Log.i("TAG-------->", "keyBoardHide: ")
            }
        })
    }

    //隐藏所有Fragment  优化显示
    private fun hideAllFragment() {
        supportFragmentManager
            .beginTransaction()
            .hide(emotionFragment)
            .commit()
        supportFragmentManager
            .beginTransaction()
            .hide(homeFragment)
            .commit()
    }

    //初始化去软键盘，显示Fragment
    private fun ActivityChatBinding.initUiStyle(array: Array<Fragment>) {
        hideSoftKeyboard()
        inputMessage.clearFocus()
        frameLayout.isVisible = true
        supportFragmentManager
            .beginTransaction()
            .show(array[0])
            .commit()
        supportFragmentManager
            .beginTransaction()
            .hide(array[1])
            .commit()
        tvSound.visibility = View.INVISIBLE
        inputMessage.visibility = View.VISIBLE
        ivSound.setImageResource(R.mipmap.sound_record)
        showSound = true
    }

    /*表情包实现的接口*/
    override fun onEmojiconBackspaceClicked(v: View?) {
        EmojiconsFragment.backspace(binding.inputMessage)
    }

    override fun onEmojiconClicked(emojicon: Emojicon?) {
        EmojiconsFragment.input(binding.inputMessage, emojicon)
    }
    /*end表情包实现的接口*/

    private fun scrollToBottom() {
        binding.recycleview.scrollToPosition(viewModel.allMessageLiveData.value!!.size - 1)
    }
}