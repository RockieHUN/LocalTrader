package com.example.localtrader.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.example.localtrader.utils.ImageUtils
import com.example.localtrader.databinding.FragmentFinishRegistrationBinding
import com.example.localtrader.utils.constants.ImageSize
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FinishRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentFinishRegistrationBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore

    private var isImageSelected = false
    private lateinit var profileImageUri: Uri

    private val userViewModel : UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_finish_registration,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()
        setName()
    }




    private fun setUpVisuals() {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        binding.circularProgress.visibility = View.GONE

    }

    private fun setUpListeners() {
        binding.submitButton.setOnClickListener {
            startLoading()

            if (!isImageSelected) {
                findNavController().navigate(R.id.action_finishRegistrationFragment_to_timeLineFragment)
            } else {
                uploadImage(profileImageUri)
                firestore.collection("users")
                    .document("users")
                    .update("profileImage","users/${auth.uid}/profilePicture")
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_finishRegistrationFragment_to_timeLineFragment)
        }

        binding.profilePicture.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data!!
            binding.profilePicture.setImageURI(imageUri)

            //save uri to a class variable
            profileImageUri = imageUri
            isImageSelected = true
            binding.submitButton.text = "Befejezés"
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val reference = storage.reference
            val path = reference.child("users/${auth.uid}/profilePicture")

            val resizedImage: MutableLiveData<ByteArray> = MutableLiveData()

            //after the image is resized, upload to storage
            resizedImage.observe(viewLifecycleOwner, { byteArray ->

                path.putBytes(byteArray).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        userViewModel.getDownloadUri(currentUser.uid)
                        findNavController().navigate(R.id.action_finishRegistrationFragment_to_timeLineFragment)
                    } else {
                        lifecycleScope.launch {
                            animateError("Nem sikerült feltölteni a képet. Próbálja újra később")
                        }
                        binding.submitButton.text = "Kihagyás"
                        stopLoading()
                    }
                }
            })

            //resize image and put to a LiveData variable
            lifecycleScope.launch(Dispatchers.IO) {
                resizedImage.postValue(ImageUtils.resizeImageUriTo(requireActivity(), imageUri, ImageSize.USER_PROFILE_SIZE))
            }

        }
    }

    private suspend fun animateError(errorMessage: String) {
        binding.errorMessage.text = errorMessage
        val view = binding.errorMessageView

        view.animate()
            .translationYBy(200f)
            .duration = 400L

        delay(4000)
        view.animate()
            .translationYBy(-200f)
            .duration = 400L

    }

    private fun setName()
    {
        binding.firstName.text = userViewModel.user.value!!.firstname
        binding.lastName.text = userViewModel.user.value!!.lastname
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