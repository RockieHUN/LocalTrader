package com.example.localtrader.main_screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.YesNoDialogFragment
import com.example.localtrader.databinding.FragmentFavoritesBinding
import com.example.localtrader.main_screens.adapters.FavoriteItemPagerAdapter
import com.example.localtrader.product.models.Product
import com.example.localtrader.viewmodels.FavoritesViewModel
import com.example.localtrader.viewmodels.ProductViewModel

class FavoritesFragment : Fragment(),
    FavoriteItemPagerAdapter.MyOnClickListener,
    YesNoDialogFragment.NoticeDialogListener
{

    private lateinit var binding : FragmentFavoritesBinding
    private lateinit var productForDeletetion : Product

    private val favoritesViewModel : FavoritesViewModel by activityViewModels()
    private val productViewModel : ProductViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorites, container, false)
        viewPager()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

    }

    private fun viewPager(){
        val adapter = FavoriteItemPagerAdapter( mutableListOf(), requireActivity(), this)
        binding.viewPager.adapter = adapter

        //load favorites
        favoritesViewModel.favorites.observe(viewLifecycleOwner,{ products ->
            if (products.isEmpty()){
                binding.noItemsHolder.visibility = View.VISIBLE
            }
            else{
                binding.noItemsHolder.visibility = View.GONE
            }
            adapter.updateData(products)
        })
        favoritesViewModel.loadFavorites()
    }

    private fun setUpListeners()
    {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_favoritesFragment_to_timeLineFragment)
        }
    }

    private fun showAlertDialog(){
        val dialog = YesNoDialogFragment(resources.getString(R.string.delete_favorite),this)
        dialog.show(requireActivity().supportFragmentManager, null)
    }

    override fun onFavoriteButtonClick(product: Product) {
        productForDeletetion = product
        showAlertDialog()
    }

    override fun onOrderButtonClick(product: Product) {
        productViewModel.product = product
        findNavController().navigate(R.id.action_favoritesFragment_to_createOrderFragment)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        favoritesViewModel.removeFromFavorites(productForDeletetion)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        return
    }
}