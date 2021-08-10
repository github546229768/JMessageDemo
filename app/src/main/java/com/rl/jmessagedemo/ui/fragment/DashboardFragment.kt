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
import com.rl.jmessagedemo.ui.view.SlideBar
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
        viewModel.friendListLiveData.observe(viewLifecycleOwner) {
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

    //方法一
//    private fun getPosition(firstLetter: String) =
//        mAdapter.currentList.binarySearch {
//            it.firstLetter!!.toUpperCase().minus(firstLetter.first())
//        }
    //方法二
    private fun getPosition(firstLetter: String): Int {
        mAdapter.currentList.forEachIndexed { index, myUserInfo ->
            if (myUserInfo.firstLetter!!.toUpperCase() == firstLetter.first()) {
                return index
            }
        }
        return -1
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
            slideBar.addOnSectionChangeListener(object : SlideBar.OnSectionChangeListener {
                override fun onSectionChange(firstLetter: String) {
                    textView.text = firstLetter
                    textView.visibility = View.VISIBLE
                    val position = getPosition(firstLetter)
                    if (position >= 0) {
                        val linearLayoutManager = recyclerview.layoutManager as LinearLayoutManager
                        linearLayoutManager.scrollToPositionWithOffset(position, 0)
                    }
                }

                override fun onFinishChange() {
                    textView.visibility = View.GONE
                }
            })
        }
    }
}