package com.yangmaolie.hunter.presentation.ui.upcoming

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yangmaolie.hunter.core.base.BaseFragment
import com.yangmaolie.hunter.databinding.FragmentUpcomingBinding
import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.repository.DealRepository
import com.yangmaolie.hunter.presentation.adapter.DealsAdapter
import com.yangmaolie.hunter.presentation.ui.deal_detail.DealDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpcomingFragment : BaseFragment<FragmentUpcomingBinding>() {

    override val binding: FragmentUpcomingBinding by lazy {
        FragmentUpcomingBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var dealRepository: DealRepository

    private lateinit var adapter: DealsAdapter

    override fun initViews() {
        adapter = DealsAdapter { deal ->
            openDealDetail(deal)
        }
        binding.rvUpcoming.layoutManager = LinearLayoutManager(context)
        binding.rvUpcoming.adapter = adapter

        loadUpcomingDeals()
    }

    private fun loadUpcomingDeals() {
        viewLifecycleOwner.lifecycleScope.launch {
            val deals = dealRepository.getUpcomingDeals().filter { !it.isExpired() }
            if (deals.isEmpty()) {
                binding.emptyState.visibility = android.view.View.VISIBLE
                binding.rvUpcoming.visibility = android.view.View.GONE
            } else {
                binding.emptyState.visibility = android.view.View.GONE
                binding.rvUpcoming.visibility = android.view.View.VISIBLE
                adapter.submitList(deals)
            }
        }
    }

    private fun openDealDetail(deal: Deal) {
        val intent = Intent(requireContext(), DealDetailActivity::class.java)
        intent.putExtra(DealDetailActivity.EXTRA_DEAL_ID, deal.dealId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadUpcomingDeals()
    }
}
