package com.example.localtrader.product.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentProductGridBinding
import com.example.localtrader.product.adapters.ProductGridRecycleAdapter
import com.example.localtrader.product.models.Product
import com.example.localtrader.utils.MySnackBar
import com.example.localtrader.utils.comparators.DateComparator
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.NavigationViewModel
import com.example.localtrader.viewmodels.ProductViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException



class ProductGridFragment : Fragment(),
    ProductGridRecycleAdapter.OnItemClickListener,
    ProductBottomModalFragment.OnModalButtonClickListener {

    private lateinit var binding: FragmentProductGridBinding
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    private val productViewModel: ProductViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationViewModel.origin = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_grid, container, false)
        setRecycle()
        return binding.root
    }


    private fun setRecycle() {

        val businessId = businessViewModel.businessId
        val adapter = ProductGridRecycleAdapter(this, requireActivity(), listOf())
        binding.recycleView.adapter = adapter
        val gridLayout = GridLayoutManager(context, 2)
        binding.recycleView.layoutManager = gridLayout
        binding.recycleView.setHasFixedSize(true)

        productViewModel.businessProducts.observe(viewLifecycleOwner, object : Observer<List<Product>>{

            override fun onChanged(products: List<Product>?) {
                if (products == null) {
                    productViewModel.businessProducts.removeObserver(this)
                    return
                }

                if (products.isNotEmpty()){
                    binding.productGridPlaceholder.visibility = View.GONE
                }
                val sortedList = products.sortedWith(DateComparator)
                adapter.updateData(sortedList)
            }
        })

        productViewModel.loadBusinessProducts(businessId)
    }


    override fun myOnItemClick(product: Product) {
        productViewModel.product = product
        if (product.ownerId == auth.currentUser?.uid) {
            ProductBottomModalFragment(this).show(requireActivity().supportFragmentManager, "Yo")
        } else {
            findNavController().navigate(R.id.action_productGridFragment_to_productProfileFragment)
        }
    }

    override fun onViewProductClick(window: ProductBottomModalFragment) {
        window.dismiss()
        findNavController().navigate(R.id.action_productGridFragment_to_productProfileFragment)
    }

    override fun onShareProductClick(window: ProductBottomModalFragment) {
        window.dismiss()

        val imageUri : MutableLiveData<Uri?> = MutableLiveData()

        imageUri.observe(viewLifecycleOwner,object : Observer<Uri?>{
            override fun onChanged(uri: Uri?) {

                if (uri == null){
                    MySnackBar.createSnackBar(binding.screenRoot, resources.getString(R.string.error_picture_share))
                }
                else{
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/*"
                    }
                    startActivity(Intent.createChooser(sendIntent, null))
                }

                imageUri.removeObserver(this)
            }
        })
        cacheImageAndRetrieveUri(imageUri)
    }

    private fun cacheImageAndRetrieveUri(imageUri : MutableLiveData<Uri?>) {

        val productId = productViewModel.product.productId

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val localFile = File.createTempFile("images",".png")
                    //download image to path
                    storage.reference.child("products/$productId/PRODUCT_IMAGE_1920")
                        .getFile(localFile)
                        .addOnSuccessListener {
                            val uri = FileProvider.getUriForFile(requireContext(), "com.mydomain.fileprovider", localFile)
                            imageUri.postValue(uri)
                        }
                        .addOnFailureListener {
                            imageUri.postValue(null)
                        }
            } catch (e: IOException) {
                imageUri.postValue(null)
            }
        }

    }

}