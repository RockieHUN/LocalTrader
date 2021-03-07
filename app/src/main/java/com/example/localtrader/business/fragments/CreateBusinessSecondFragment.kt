package com.example.localtrader.business.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.business.models.Business
import com.example.localtrader.databinding.FragmentCreateBusinessSecondBinding
import com.example.localtrader.utils.*
import com.example.localtrader.utils.constants.ImageSize
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.CreateBusinessViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch


class CreateBusinessSecondFragment : Fragment() {

    private lateinit var binding: FragmentCreateBusinessSecondBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val creationViewModel: CreateBusinessViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()

    private lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        database = Firebase.firestore
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_business_second,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpVisuals()
        setUpListeners()

    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser == null) {
            findNavController().navigate(R.id.action_createBusinessSecondFragment_to_loginFragment)
        } else {
            uid = auth.currentUser!!.uid
        }

        val user = userViewModel.user.value
        if (user != null) {
            binding.emailInput.setText(user.email)
            binding.telephoneInput.setText(creationViewModel.business.telephone)
            binding.locationInput.setText(creationViewModel.business.location)
            binding.facebookInput.setText(creationViewModel.business.facebook_link)
            binding.instagramInput.setText(creationViewModel.business.instagram_link)
        }
    }


    private fun setUpVisuals() {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        binding.circularProgress.visibility = View.GONE
    }

    private fun setUpListeners() {
        binding.submitButton.setOnClickListener {
            submitData()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            creationViewModel.business.telephone = binding.telephoneInput.text.toString()
            creationViewModel.business.location = binding.locationInput.text.toString()
            creationViewModel.business.facebook_link = binding.facebookInput.text.toString()
            creationViewModel.business.instagram_link = binding.instagramInput.text.toString()
            findNavController().navigate(R.id.action_createBusinessSecondFragment_to_createBusinessFirstFragment)
        }
    }

    private fun submitData() {
        val email = binding.emailInput.text.toString()
        val telephone = binding.telephoneInput.text.toString()
        val location = binding.locationInput.text.toString()
        val facebook = binding.facebookInput.text.toString()
        val instagram = binding.instagramInput.text.toString()

        val error = validateData(email, telephone, location)

        when (error) {
            1 -> showErrorMessage("Helytelen e-mail!!")
            2 -> showErrorMessage("Helytelen telefonszám!")
            3 -> showErrorMessage(" Helytelen település!")
            0 -> {
                creationViewModel.business.email = email
                creationViewModel.business.telephone = telephone
                creationViewModel.business.location = location
                creationViewModel.business.facebook_link = facebook
                creationViewModel.business.instagram_link = instagram

                val business = creationViewModel.convert(uid)
                //userViewModel.userBusiness.value = business
                saveDataToDatabase(business)
            }
        }
    }

    private fun validateData(email: String, telephone: String, location: String): Int {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) 1
        else if (!Patterns.PHONE.matcher(telephone).matches()) 2
        else if (location.isEmpty()) 3
        else 0
    }

    private fun showErrorMessage(msg: String) {
        lifecycleScope.launch {
            Animations.animateError(binding.errorMessageView, msg)
        }
    }

    private fun saveDataToDatabase(business: Business) {
        startLoading()

        //save business data to firestore
        database.collection("businesses")
            .add(business)
            .addOnSuccessListener { documentReference ->

                //set businessId to owner
                database.collection("users")
                    .document(uid)
                    .update("businessId", documentReference.id)
                    .addOnSuccessListener {

                        //save businessId to viewModel
                        userViewModel.user.value!!.businessId = documentReference.id

                        //add businessId field to business
                        database.collection("businesses")
                            .document(documentReference.id)
                            .update("businessId", documentReference.id)
                            .addOnSuccessListener {

                                //save logo to storage
                                val reference = storage.reference
                                val path =
                                    reference.child("businesses/${documentReference.id}/logo")

                                val resizedImage: MutableLiveData<ByteArray> = MutableLiveData()

                                resizedImage.observe(viewLifecycleOwner, { byteArray ->
                                    path.putBytes(byteArray).addOnSuccessListener {
                                        stopLoading()

                                        //pass data to business viewModel
                                        businessViewModel.businessId = documentReference.id
                                        businessViewModel.businessOwner = auth.currentUser!!.uid
                                        findNavController().navigate(R.id.action_createBusinessSecondFragment_to_businessProfileFragment)
                                    }
                                        .addOnFailureListener { e ->
                                            Firebase.crashlytics.log("$e")
                                            stopLoading()
                                            findNavController().navigate(R.id.action_createBusinessSecondFragment_to_businessProfileFragment)
                                        }
                                })

                                //resize image
                                lifecycleScope.launch {
                                    resizedImage.value = ImageUtils.resizeImageUriTo(
                                        requireActivity(),
                                        creationViewModel.business.imageUri!!,
                                        ImageSize.BUSINESS_PROFILE_SIZE
                                    )
                                }
                            }
                            .addOnFailureListener { e->
                                //if setting the businessId filed to business fails
                                Firebase.crashlytics.log("$e")
                            }
                    }
                    .addOnFailureListener { e ->
                        //if setting businessId to owner fails
                        Firebase.crashlytics.log("$e ${documentReference.id}")
                    }

            }
            .addOnFailureListener { e ->
                //if setting business fails
                Firebase.crashlytics.log("$e $business")
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

    /* private fun startAutocompleteActivity()
     {
         Places.initialize(requireContext(), Secrets.placesKey)
         Places.createClient(requireContext())

         val fields = listOf(Place.Field.ID, Place.Field.NAME)
         val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
             .build(requireContext())
         startActivityForResult(intent, 1)
     }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         if (requestCode == 1) {
             when (resultCode) {
                 Activity.RESULT_OK -> {
                     data?.let {
                         val place = Autocomplete.getPlaceFromIntent(data)
                         Log.d("*********", "Place: ${place.name}, ${place.id}")
                         binding.locationInput.text = place.name
                     }
                 }
                 AutocompleteActivity.RESULT_ERROR -> {
                     Log.d("********","Error")
                 }
                 Activity.RESULT_CANCELED -> {
                     Log.d("******","CANCELLLED")
                 }
             }
             return
         }
         super.onActivityResult(requestCode, resultCode, data)
     }*/


}