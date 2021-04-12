package com.example.localtrader.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentFeedBinding
import com.example.localtrader.feed.adapters.FeedAdapter
import com.example.localtrader.feed.models.FeedAdItem
import com.example.localtrader.feed.models.FeedItem
import com.example.localtrader.retrofit.models.SearchTerm
import kotlinx.coroutines.launch


class FeedFragment : Fragment(),
SearchView.OnQueryTextListener{

    private lateinit var binding : FragmentFeedBinding
    private val feedViewModel : FeedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        createRecycle()
        return binding.root
    }

    private fun createRecycle(){
        val adapter = FeedAdapter(requireActivity())
        binding.recycleView.adapter = adapter
        val verticalLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleView.layoutManager = verticalLayout
        binding.recycleView.setHasFixedSize(true)

        feedViewModel.feedItems.observe(viewLifecycleOwner, { feedItems ->
            //val newFeedItems = addAdsToFeed(feedItems)
            adapter.updateData(feedItems)
        })
        feedViewModel.loadFeed(viewLifecycleOwner)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {

        }
        return true
    }

    private fun addAdsToFeed(feedItems : List<FeedItem>) : List<FeedItem>{
        val newFeedItems = feedItems.toMutableList()
        val atEveryXIndex = 2
        val numberOfAds = feedItems.size / atEveryXIndex

        for (i in 1..numberOfAds)
        newFeedItems.add(i*atEveryXIndex, FeedAdItem())

        return newFeedItems
    }
}