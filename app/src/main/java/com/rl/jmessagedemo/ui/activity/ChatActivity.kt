package com.rl.jmessagedemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.MessageListAdapter
import com.rl.jmessagedemo.constant.GROUP_CHAT_TYPE
import com.rl.jmessagedemo.constant.SINGLE_CHAT_TYPE
import com.rl.jmessagedemo.databinding.ActivityChatBinding
import com.rl.jmessagedemo.viewmodel.ChatViewModel


class ChatActivity : BaseActivity() {
    private val binding: ActivityChatBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_chat)
    }
    private val viewModel: ChatViewModel by viewModels()
    private val mAdapter by lazy { MessageListAdapter() }
    private var username = "0" //聊天key   默认为单聊
    private var type = SINGLE_CHAT_TYPE ///聊天类型  默认为单聊

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        with(binding) {
            recycleview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
                adapter = mAdapter
//                addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                        //当前状态没发生滚动时
//                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                            val linearLayoutManager = layoutManager as LinearLayoutManager
//                            //如果当前可见条目的位置为第1个
//                            if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
//                                presenter.loadMoreData(username)//加载更多数据
//                            }
//                        }
//                    }
//                })
            }
            sedMessage.setOnClickListener {
                viewModel.sendMessage(inputMessage.text.toString())
                binding.inputMessage.text.clear()
                hideSoftKeyboard()
            }
            inputMessage.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    sedMessage.isEnabled = !s.isNullOrEmpty()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }

    }

    private fun scrollToBottom() {
        binding.recycleview.scrollToPosition(viewModel.allMessageLiveData.value!!.size - 1)
    }
}