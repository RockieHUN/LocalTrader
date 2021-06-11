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
    FeedAdapter.OnItemClickListener
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
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        setUpListeners()
        createRecycle()
        return binding.root
    }

    override fun onPause() {
        feedViewModel.feedManager.feedItems.removeObservers(viewLifecycleOwner)
        super.onPause()
    }

    override fun onResume() {
        feedViewModel.feedManager.feedItems.observe(viewLifecycleOwner, { feedItems ->
            adapter.updateData(feedItems)
        })
        super.onResume()
    }

    override fun onDestroy() {
        for (item in adapter.adList){
            item.ad?.destroy()
            item.ad = null
        }
        super.onDestroy()
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
        adapter = FeedAdapter(requireActivity(), viewLifecycleOwner,this)
        binding.recycleView.adapter = adapter
        val verticalLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleView.layoutManager = verticalLayout
        binding.recycleView.setHasFixedSize(true)
        feedViewModel.loadNextItems()
        addScrollListener()
    }


    private fun addScrollListener(){

        binding.recycleView.addOnScrollListener( object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (feedViewModel.feedManager.feedItems.value == null) return

                val itemCount = feedViewModel.feedManager.feedItems.value!!.size
                val layoutManager = binding.recycleView.layoutManager as LinearLayoutManager

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                Log.d("MYFEED", "last: ${lastVisibleItemPosition} total: ${itemCount}")
                if (lastVisibleItemPosition >= itemCount - 1) {

                    GlobalScope.launch {
                        feedViewModel.loadNextItems()
                    }
                }
            }
        })
    }

    override fun onBusinessClick(id: String) {
        businessViewModel.businessId = id
        businessViewModel.originFragment = 3
        findNavController().navigate(R.id.action_feedFragment_to_businessProfileFragment)
    }
}