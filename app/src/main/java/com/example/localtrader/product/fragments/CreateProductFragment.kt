package com.example.localtrader.product.fragments

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentCreateProductBinding
import com.example.localtrader.product.models.Product
import com.example.localtrader.utils.ImageUtils
import com.example.localtrader.utils.MySnackBar
import com.example.localtrader.utils.constants.ImageSize
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateProductFragment : Fragment() {

    private lateinit var binding : FragmentCreateProductBinding

    private val userViewModel : UserViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    private  var g_imageUri : Uri? = null
    private var g_imageBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_product, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()
    }

    private fun setUpVisuals()
    {
        binding.priceInput.setText(R.string.product_starter_price)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        binding.circularProgress.visibility = View.GONE
    }

    private fun setUpListeners(){
        binding.takePicture.setOnClickListener {
            takePicture()
        }
        binding.selectPicture.setOnClickListener {
            pickImageFromGallery()
        }

        binding.submitButton.setOnClickListener {
            saveToDatabase()
        }
    }

    private fun takePicture(){

        if (requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, 1)
            } catch (e: ActivityNotFoundException) {
                animateError("Ismeretlen hiba merült fel. Próbálja úrja később");
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.productImage.setImageBitmap(imageBitmap)
            g_imageUri = null
            g_imageBitmap = imageBitmap
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data!!
            binding.productImage.setImageURI(imageUri)
            g_imageBitmap = null
            g_imageUri = imageUri
        }
    }

    private fun saveToDatabase(){
        startLoading()

        val productName = binding.nameInput.text.toString()
        val productDescription = binding.productDescription.text.toString()
        val stringPrice = binding.priceInput.text.toString()

        var price = 0.0

        if (stringPrice.isNotEmpty()){
            try{
                price = stringPrice.toDouble()
            }
            catch (e : Error){
                animateError("Helytelen ár!")
                return
            }
        }


        if (dataIsValid(productName,productDescription)){
           val product = Product(
               ownerId = auth.currentUser!!.uid,
               businessId = userViewModel.user.value!!.businessId,
               name = productName,
               description = productDescription,
               price = price,
               businessName = businessViewModel.business.value!!.name
           )

            firestore.collection("products")
                .add(product)
                .addOnSuccessListener { documentReference ->

                    firestore.collection("products")
                        .document(documentReference.id)
                        .update("productId", documentReference.id)
                        .addOnSuccessListener {

                            val reference = storage.reference
                            val path = reference.child("products/${documentReference.id}/image")

                            val resizedImage: MutableLiveData<ByteArray> = MutableLiveData()

                            resizedImage.observe(viewLifecycleOwner, { byteArray ->
                                path.putBytes(byteArray)
                                    .addOnSuccessListener {
                                        stopLoading()
                                        findNavController().navigate(R.id.action_createProductFragment_to_businessProfileFragment)
                                    }
                                    .addOnFailureListener{ e->
                                        Firebase.crashlytics.log("$e")
                                        stopLoading()
                                    }
                            })

                            //resize the bitmap
                            if (g_imageBitmap != null){
                                //resize image
                                lifecycleScope.launch {
                                    resizedImage.value = ImageUtils.resizeImageBitmapTo(
                                        requireActivity(),
                                        g_imageBitmap!!,
                                        ImageSize.PRODUCT_IMAGE_SIZE
                                    )
                                }
                            }

                            //resize the uri
                            else{
                                lifecycleScope.launch(Dispatchers.IO) {
                                    resizedImage.postValue(ImageUtils.resizeImageUriTo(
                                        requireActivity(),
                                        g_imageUri!!,
                                        ImageSize.PRODUCT_IMAGE_SIZE
                                    ))
                                }
                            }
                        }
                            //add product id
                        .addOnFailureListener {  e->
                            stopLoading()
                            Firebase.crashlytics.log("$e")
                        }
                }
                    //saving product to firebase
                .addOnFailureListener { e->
                    stopLoading()
                    Firebase.crashlytics.log("$e")
                }

        }
    }

    private fun dataIsValid(productName : String, productDescription : String) : Boolean{
        if (g_imageUri == null && g_imageBitmap == null){
            stopLoading()
            animateError("A kép feltöltése kötelező!")
            return false
        }

        if (productName.length < 3){
            stopLoading()
            animateError("A termék nevének legalább 3 karakterből kell állnia!")
            return false
        }

        if (productDescription.length < 30){
            stopLoading()
            animateError("A termék leírásának legalább 30 karakterből kell állnia!")
            return false
        }
        return true
    }

    private fun animateError(msg : String){
        lifecycleScope.launch {
            MySnackBar.createSnackBar(binding.screenRoot, msg)
        }
    }

    private fun startLoading() {
        binding.circularProgress.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.circularProgress.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }
}