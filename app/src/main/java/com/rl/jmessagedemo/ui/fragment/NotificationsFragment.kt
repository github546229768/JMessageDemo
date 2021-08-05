package com.rl.jmessagedemo.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.ConversationListAdapter
import com.rl.jmessagedemo.databinding.FragmentNotificationsBinding
import com.rl.jmessagedemo.ui.activity.NewGroupChatActivity
import com.rl.jmessagedemo.viewmodel.NotificationsViewModel


class NotificationsFragment : Fragment() {

    private val viewModel: NotificationsViewModel by viewModels()

    private lateinit var binding: FragmentNotificationsBinding

    private val mAdapter by lazy {
        ConversationListAdapter()
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.fetchData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_notifications,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.conversationLiveData.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
            binding.recyclerview.scrollToPosition(0)
        }
        val progressDialog = ProgressDialog(requireActivity())
        viewModel.loadingEvent.observe(viewLifecycleOwner) {
            if (it) progressDialog.show() else progressDialog.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.more_option, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_group) {
            resultLauncher.launch(Intent(requireActivity(), NewGroupChatActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        setHasOptionsMenu(true)
        with(binding) {
            recyclerview.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
        }
    }
}