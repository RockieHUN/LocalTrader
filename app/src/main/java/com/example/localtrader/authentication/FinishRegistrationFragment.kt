package com.example.localtrader.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.Utils.ImageUtils
import com.example.localtrader.databinding.FragmentFinishRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask

class FinishRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentFinishRegistrationBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private var isImageSelected = false
    private lateinit var profileImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        auth = Firebase.auth
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
            profileImageUri = imageUri
            isImageSelected = true
            binding.submitButton.text = "Befejezés"
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val reference = storage.reference
            val path = reference.child("Users/${auth.uid}/profilePicture")

            /*path.putFile(imageUri).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_finishRegistrationFragment_to_timeLineFragment)
                }
                else{
                    lifecycleScope.launch{
                        animateError("Nem sikerült feltölteni a képek. Próbálja újra később")
                    }
                    binding.submitButton.text = "Kihagyás"
                }
            } */

            val resizedImage: MutableLiveData<ByteArray> = MutableLiveData()

            resizedImage.observe(viewLifecycleOwner, { byteArray ->
                path.putBytes(byteArray).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_finishRegistrationFragment_to_timeLineFragment)
                    } else {
                        lifecycleScope.launch {
                            animateError("Nem sikerült feltölteni a képek. Próbálja újra később")
                        }
                        binding.submitButton.text = "Kihagyás"
                        stopLoading()
                    }
                }
            })

            lifecycleScope.launch {
                resizedImage.value = ImageUtils.convertProfileImage(requireActivity(), imageUri)
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

    private fun startLoading() {
        binding.circularProgress.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.circularProgress.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }
}