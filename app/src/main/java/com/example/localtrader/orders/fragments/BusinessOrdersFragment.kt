package com.example.localtrader.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.YesNoDialogFragment
import com.example.localtrader.databinding.FragmentBusinessOrdersBinding
import com.example.localtrader.orders.adapters.BusinessOrdersAdapter
import com.example.localtrader.orders.models.OrderRequest
import com.example.localtrader.repositories.OrdersRepository
import com.example.localtrader.utils.comparators.OrderComparator
import com.example.localtrader.utils.constants.OrderStatus
import com.example.localtrader.viewmodels.UserViewModel

class BusinessOrdersFragment : Fragment(),
    BusinessOrdersAdapter.OnItemClickListener,
    SelectDialogFragment.OnSelectedListener,
    YesNoDialogFragment.NoticeDialogListener
{
    private lateinit var binding : FragmentBusinessOrdersBinding
    private lateinit var ordersRepository: OrdersRepository
    private val userViewModel : UserViewModel by activityViewModels()
    private var orderRequest : OrderRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ordersRepository = OrdersRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_orders, container, false)
        createRecycle()
        return binding.root
    }

    private fun createRecycle(){
        val adapter = BusinessOrdersAdapter(this, listOf(), requireContext())
        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(context)
        binding.recycleView.setHasFixedSize(true)

        ordersRepository.businessOrders.observe(viewLifecycleOwner,{ orders ->

            if (orders.isEmpty()){
                binding.noOrdersHolder.visibility = View.VISIBLE
            }
            val sortedList = orders.sortedWith(OrderComparator)
            adapter.updateData(sortedList)
        })
        ordersRepository.loadBusinessOrders(userViewModel.user.value!!.businessId)
    }

    override fun onItemClick(order: OrderRequest) {
        orderRequest = order
        val yesNoDialog = YesNoDialogFragment(resources.getString(R.string.accept_order),this)
        yesNoDialog.show(requireActivity().supportFragmentManager, null)
    }

    override fun onItemLongClick(order: OrderRequest) {
        orderRequest = order
        val selectDialog = SelectDialogFragment(this)
        selectDialog.show(requireActivity().supportFragmentManager, null)
    }

    override fun onDialogItemSelected(which: Int) {

        val businessId = userViewModel.user.value!!.businessId

        if (orderRequest != null){
            var newStatus = orderRequest!!.status
            when (which){
                0 -> newStatus = OrderStatus.WORKING_ON_IT
                1 -> newStatus = OrderStatus.DONE
                2 -> {
                    ordersRepository.deleteOrder(orderRequest!!.orderRequestId, businessId)
                    return
                }
            }


            ordersRepository.updateOrderStatus(businessId, orderRequest!!.orderRequestId, newStatus)
        }

    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        if (orderRequest != null){
            val businessId = userViewModel.user.value!!.businessId
            ordersRepository.updateOrderStatus(businessId, orderRequest!!.orderRequestId, OrderStatus.ACCEPTED)
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        if (orderRequest != null){
            val businessId = userViewModel.user.value!!.businessId
            ordersRepository.updateOrderStatus(businessId, orderRequest!!.orderRequestId, OrderStatus.DECLINED)
        }
    }


}