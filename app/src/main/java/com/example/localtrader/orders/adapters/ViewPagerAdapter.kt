package com.example.localtrader.orders.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.localtrader.orders.fragments.BusinessOrdersFragment
import com.example.localtrader.orders.fragments.ClientOrdersFragment

class ViewPagerAdapter (fragment : Fragment) : FragmentStateAdapter( fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) ClientOrdersFragment()
        else BusinessOrdersFragment()
    }

}