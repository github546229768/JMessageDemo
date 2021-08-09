package com.rl.jmessagedemo.ui.activity

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.AddFriendListAdapter
import com.rl.jmessagedemo.databinding.ActivityAddfriendBinding
import com.rl.jmessagedemo.viewmodel.AddFriendViewModel


class AddFriendActivity : BaseActivity() {
    private val binding: ActivityAddfriendBinding by lazy {
        setContentView(this, R.layout.activity_addfriend)
    }
    private val viewModel by viewModels<AddFriendViewModel>()
    private val mAdapter by lazy { AddFriendListAdapter() }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            recyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
            searchview.isSubmitButtonEnabled = true
            searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.addFriend(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}