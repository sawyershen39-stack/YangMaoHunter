package com.yangmaolie.hunter.presentation.ui.home

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.yangmaolie.hunter.core.base.BaseFragment
import com.yangmaolie.hunter.databinding.FragmentHomeBinding
import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.presentation.adapter.CategoriesAdapter
import com.yangmaolie.hunter.presentation.adapter.DealsAdapter
import com.yangmaolie.hunter.presentation.ui.deal_detail.DealDetailActivity
import com.yangmaolie.hunter.presentation.ui.onboard.PreferenceOnboardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var dealsAdapter: DealsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun initViews() {
        setupCategories()
        setupDealsList()
        setupSwipeRefresh()
        setupRefreshButton()
        dealsAdapter = DealsAdapter { deal ->
            openDealDetail(deal)
        }
        binding.rvDeals.adapter = dealsAdapter

        viewModel.checkOnboardingStatus()
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deals.collect { deals ->
                    updateDealsList(deals)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRefreshing.collect { isRefreshing ->
                    binding.swipeRefresh.isRefreshing = isRefreshing
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldShowOnboarding.collect { shouldShow ->
                    if (shouldShow) {
                        startActivity(Intent(requireContext(), PreferenceOnboardActivity::class.java))
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        showShimmer()
                    } else {
                        hideShimmer()
                    }
                }
            }
        }
    }

    private fun setupCategories() {
        categoriesAdapter = CategoriesAdapter { category ->
            viewModel.loadRecommendations(if (category == "all") null else category)
        }
        binding.rvCategories.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = categoriesAdapter
    }

    private fun setupDealsList() {
        binding.rvDeals.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onRefresh()
        }
    }

    private fun setupRefreshButton() {
        binding.ivRefresh.setOnClickListener {
            viewModel.onRefresh()
        }
    }

    private fun updateDealsList(deals: List<Deal>) {
        if (deals.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
            dealsAdapter.submitList(deals)
        }
    }

    private fun showShimmer() {
        binding.shimmerContainer.visibility = android.view.View.VISIBLE
        binding.shimmerContainer.startShimmer()
        binding.swipeRefresh.visibility = android.view.View.GONE
        binding.emptyState.visibility = android.view.View.GONE
    }

    private fun hideShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = android.view.View.GONE
        binding.swipeRefresh.visibility = android.view.View.VISIBLE
    }

    private fun showEmptyState() {
        binding.emptyState.visibility = android.view.View.VISIBLE
        binding.swipeRefresh.visibility = android.view.View.GONE
    }

    private fun hideEmptyState() {
        binding.emptyState.visibility = android.view.View.GONE
        binding.swipeRefresh.visibility = android.view.View.VISIBLE
    }

    private fun openDealDetail(deal: Deal) {
        val intent = Intent(requireContext(), DealDetailActivity::class.java)
        intent.putExtra(DealDetailActivity.EXTRA_DEAL_ID, deal.dealId)
        startActivity(intent)
    }
}
