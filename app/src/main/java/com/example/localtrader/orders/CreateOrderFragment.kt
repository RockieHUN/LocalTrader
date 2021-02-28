package com.example.localtrader.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentCreateOrderBinding
import com.example.localtrader.viewmodels.ProductViewModel

class CreateOrderFragment : Fragment() {

    private lateinit var binding : FragmentCreateOrderBinding
    private val productViewModel : ProductViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_order, container, false)

        numberPicker()

        return binding.root
    }

    private fun numberPicker(){
        binding.productName.text = productViewModel.product.name
        binding.numberPicker.minValue = 1
        binding.numberPicker.maxValue = 20
        binding.numberPicker.dividerPadding = 10
        binding.numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            binding.sum.text = (newVal * productViewModel.product.price).toString()
        }
    }


}