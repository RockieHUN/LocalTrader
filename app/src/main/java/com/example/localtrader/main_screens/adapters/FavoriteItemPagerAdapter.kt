package com.example.localtrader.main_screens.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.localtrader.main_screens.fragments.FavoriteItemFragment
import com.example.localtrader.product.models.Product

class FavoriteItemPagerAdapter(fragment : Fragment, private val items : List<Product> ) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return FavoriteItemFragment(items[position])
    }
}