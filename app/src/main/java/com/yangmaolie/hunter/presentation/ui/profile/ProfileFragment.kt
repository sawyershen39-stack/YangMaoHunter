package com.yangmaolie.hunter.presentation.ui.profile

import com.yangmaolie.hunter.core.base.BaseFragment
import com.yangmaolie.hunter.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val binding: FragmentProfileBinding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        // Coming soon
    }
}
