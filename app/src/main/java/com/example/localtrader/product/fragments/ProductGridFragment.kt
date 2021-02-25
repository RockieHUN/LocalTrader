package com.example.localtrader.product.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.business.models.Business
import com.example.localtrader.databinding.FragmentProductGridBinding
import com.example.localtrader.main_screens.adapters.PopularProductsAdapter
import com.example.localtrader.product.ProductGridRecycleAdapter
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.ProductViewModel


class ProductGridFragment : Fragment(),
        ProductGridRecycleAdapter.OnItemClickListener
{
    private lateinit var binding : FragmentProductGridBinding
    private val productViewModel : ProductViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_grid, container, false)

        setRecycle()
        return binding.root
    }

    private fun setRecycle(){

        val businessId = businessViewModel.businessId

        productViewModel.businessProducts.observe(viewLifecycleOwner,{ products ->
            val adapter = ProductGridRecycleAdapter(this, requireActivity(), products)
            binding.recycleView.adapter = adapter
            val gridLayout = GridLayoutManager(context,2)
            binding.recycleView.layoutManager = gridLayout
            binding.recycleView.setHasFixedSize(true)
        })

        productViewModel.loadBusinessProducts(businessId)

    }

    override fun onItemClick(position: Int) {
        return
    }


}