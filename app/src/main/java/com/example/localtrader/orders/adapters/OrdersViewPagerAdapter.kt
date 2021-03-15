package com.example.localtrader.orders.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.localtrader.orders.fragments.BusinessOrdersFragment
import com.example.localtrader.orders.fragments.ClientOrdersFragment

class OrdersViewPagerAdapter (fragment : Fragment, itemCount : Int) : FragmentStateAdapter( fragment) {

    private val count = itemCount

    override fun getItemCount(): Int = count

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) ClientOrdersFragment()
        else BusinessOrdersFragment()
    }
}