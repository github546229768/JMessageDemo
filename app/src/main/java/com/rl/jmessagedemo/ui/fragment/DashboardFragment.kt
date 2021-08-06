package com.rl.jmessagedemo.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.adapter.ContactListAdapter
import com.rl.jmessagedemo.databinding.FragmentDashboardBinding
import com.rl.jmessagedemo.ui.activity.AddFriendActivity
import com.rl.jmessagedemo.ui.activity.GroupActivity
import com.rl.jmessagedemo.viewmodel.DashboardViewModel

class DashboardFragment : BaseFragment() {

    private lateinit var binding: FragmentDashboardBinding

    private val viewModel: DashboardViewModel by viewModels()

    private val mAdapter by lazy {
        ContactListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_dashboard,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        viewModel.friendListLiveData.observe(viewLifecycleOwner){
            mAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addmenu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            ActivityUtils.startActivity(AddFriendActivity::class.java)
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
            layoutGroup.setOnClickListener {
                ActivityUtils.startActivity(GroupActivity::class.java)
            }
        }

    }
}