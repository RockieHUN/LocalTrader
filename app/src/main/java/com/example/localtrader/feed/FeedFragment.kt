package com.example.localtrader.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


class FeedFragment : Fragment()
{

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
        setUpListeners()
        createRecycle()
        return binding.root
    }

    private fun setUpListeners(){
        binding.searchIcon.setOnClickListener{
            findNavController().navigate(R.id.action_feedFragment_to_searchFragment)
        }
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
                if (lastPosition >= itemCount - 3) {
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
        newFeedItems.add(i*atEveryXIndex, FeedAdItem())

        return newFeedItems
    }
}