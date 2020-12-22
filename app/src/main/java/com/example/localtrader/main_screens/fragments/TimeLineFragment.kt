package com.example.localtrader.main_screens.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentTimeLineBinding
import com.example.localtrader.main_screens.adapters.PopularBusinessesAdapter
import com.example.localtrader.main_screens.adapters.RecommendedProductsAdapter
import java.util.*
import kotlin.concurrent.timerTask

class TimeLineFragment : Fragment(),
    RecommendedProductsAdapter.OnItemClickListener,
    PopularBusinessesAdapter.OnItemClickListener

{

    private lateinit var binding : FragmentTimeLineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_time_line,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()

        recycleRecommendedProducts()
        recyclePopularBusinesses()
    }

    private fun recycleRecommendedProducts()
    {
        val adapter = RecommendedProductsAdapter(this)
        binding.recycleRecommendedProducts.adapter = adapter
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recycleRecommendedProducts.layoutManager = horizontalLayout
        binding.recycleRecommendedProducts.setHasFixedSize(true)
    }

    private fun recyclePopularBusinesses()
    {
        val adapter = PopularBusinessesAdapter(this)
        binding.recyclePopularBusinesses.adapter = adapter
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclePopularBusinesses.layoutManager = horizontalLayout
        binding.recyclePopularBusinesses.setHasFixedSize(true)

    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }

    private fun setUpListeners()
    {
        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(requireContext(), "Press again to exit", Toast.LENGTH_SHORT).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)
                callbackCounter++
            } else requireActivity().finish()
        }
    }

    override fun onItemClick(position: Int) {
        return
    }




}