package com.rl.jmessagedemo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import cn.jpush.im.android.api.JMessageClient
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.rl.jmessagedemo.R
import com.rl.jmessagedemo.databinding.FragmentHomeBinding
import com.rl.jmessagedemo.ui.activity.LoginActivity
import com.rl.jmessagedemo.ui.activity.PersonInformActivity
import com.rl.jmessagedemo.viewmodel.HomeViewModel

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_home,
            container,
            false
        )
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.vm = viewModel
        binding.lifecycleOwner = this
        viewModel.userInfo.observe(viewLifecycleOwner) {
            viewModel.loadAvatar(it)
        }
    }

    private fun initView() {
        with(binding){
            logout.setOnClickListener {
                JMessageClient.logout()
                SPUtils.getInstance(Context.MODE_PRIVATE).clear()
                ActivityUtils.startActivity(LoginActivity::class.java)
                requireActivity().finish()
            }
            rlInfo.setOnClickListener {
                ActivityUtils.startActivity(PersonInformActivity::class.java)
            }
        }

    }
}