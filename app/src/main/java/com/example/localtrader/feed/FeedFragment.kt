package com.example.localtrader.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentFeedBinding
import com.example.localtrader.feed.adapters.FeedAdapter
import com.example.localtrader.feed.models.FeedAdItem
import com.example.localtrader.feed.models.FeedItem
import com.example.localtrader.viewmodels.BusinessViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FeedFragment : Fragment(),
    FeedAdapter.onItemClickListener
{

    private lateinit var binding : FragmentFeedBinding
    private val feedViewModel : FeedViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()

    private lateinit var adapter : FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        setUpListeners()
        createRecycle()
        return binding.root
    }

    override fun onPause() {
        feedViewModel.feedItems.removeObservers(viewLifecycleOwner)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.feedItems.observe(viewLifecycleOwner, { feedItems ->
            //val newFeedItems = addAdsToFeed(feedItems)
            //Log.d("MYFEED", "${feedItems}")
            adapter.updateData(feedItems)
        })

    }

    private fun setUpListeners(){
        binding.searchIcon.setOnClickListener{
            findNavController().navigate(R.id.action_feedFragment_to_searchFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_feedFragment_to_timeLineFragment)
        }
    }

    private fun createRecycle(){
        adapter = FeedAdapter(requireActivity(), this)
        binding.recycleView.adapter = adapter
        val verticalLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleView.layoutManager = verticalLayout
        binding.recycleView.setHasFixedSize(true)

        Log.d("MYFEED","add observer")



        feedViewModel.loadFeed(viewLifecycleOwner)
        addScrollListener()
    }


    private fun addScrollListener(){

        binding.recycleView.addOnScrollListener( object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)


                val itemCount = feedViewModel.feedItems.value!!.size
                val layoutManager = binding.recycleView.layoutManager as LinearLayoutManager

                val firstPosition = layoutManager.findFirstVisibleItemPosition()
                val lastPosition = layoutManager.findLastVisibleItemPosition()

                Log.d("MYFEED", "last: ${lastPosition} total: ${itemCount}")
                if (lastPosition >= itemCount - 1) {

                    GlobalScope.launch {
                        feedViewModel.loadNextItems()
                    }
                }
            }
        })
    }

    private fun addAdsToFeed(feedItems : List<FeedItem>) : List<FeedItem>{
        val newFeedItems = feedItems.toMutableList()
        val atEveryXIndex = 5
        val numberOfAds = feedItems.size / atEveryXIndex

        for (i in 1..numberOfAds)
        newFeedItems.add(i * atEveryXIndex, FeedAdItem())

        return newFeedItems
    }

    override fun onBusinessClick(id: String) {
        businessViewModel.businessId = id
        businessViewModel.originFragment = 3
        findNavController().navigate(R.id.action_feedFragment_to_businessProfileFragment)
    }
}