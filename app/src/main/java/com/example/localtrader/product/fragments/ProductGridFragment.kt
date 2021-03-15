package com.example.localtrader.product.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentProductGridBinding
import com.example.localtrader.product.adapters.ProductGridRecycleAdapter
import com.example.localtrader.product.models.Product
import com.example.localtrader.utils.date.DateComparator
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.NavigationViewModel
import com.example.localtrader.viewmodels.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProductGridFragment : Fragment(),
        ProductGridRecycleAdapter.OnItemClickListener
{

    private lateinit var binding : FragmentProductGridBinding
    private lateinit var auth : FirebaseAuth

    private val productViewModel : ProductViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()
    private val navigationViewModel : NavigationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        navigationViewModel.origin = 3
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
        val adapter = ProductGridRecycleAdapter(this, requireActivity(), listOf())
        binding.recycleView.adapter = adapter
        val gridLayout = GridLayoutManager(context,2)
        binding.recycleView.layoutManager = gridLayout
        binding.recycleView.setHasFixedSize(true)

        productViewModel.businessProducts.observe(viewLifecycleOwner,{ products ->
            val sortedList = products.sortedWith(DateComparator)
            adapter.updateData(sortedList)
        })

        productViewModel.loadBusinessProducts(businessId)
    }

    override fun onItemClick(position: Int) {
        return
    }

    override fun myOnItemClick(product: Product) {
        productViewModel.product = product
        if (product.ownerId == auth.currentUser?.uid){
            findNavController().navigate(R.id.action_productGridFragment_to_productBottomModalFragment)
        }
        else{
            val dialog = ProductProfileFragment()
            dialog.show(requireActivity().supportFragmentManager, null)
        }
    }


}