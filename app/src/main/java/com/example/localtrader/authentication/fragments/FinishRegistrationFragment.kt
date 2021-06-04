package com.example.localtrader.authentication.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.authentication.AuthActivity
import com.example.localtrader.authentication.viewmodels.AuthViewModel
import com.example.localtrader.databinding.FragmentFinishRegistrationBinding
import com.example.localtrader.utils.MySnackBar
import com.example.localtrader.utils.imageUtils.FirebaseImageUploader
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FinishRegistrationFragment : Fragment()
{
    private val normalAuthType = 1
    private val googleAuthType = 2

    private lateinit var binding: FragmentFinishRegistrationBinding
    private lateinit var activity : AuthActivity

    private var isImageSelected = false
    private var authType = normalAuthType
    private var profileImageUri: Uri? = null

    private val auth = Firebase.auth
    private val authViewModel : AuthViewModel by activityViewModels()
    private val uploadPath = "users/${auth.currentUser!!.uid}/profilePicture"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as AuthActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finish_registration, container, false)
        setUpVisuals()
        setUpListeners()
        setUserData()
        return binding.root
    }


    private fun setUpVisuals() {
        binding.circularProgress.visibility = View.GONE
    }

    private fun setUpListeners() {
        binding.submitButton.setOnClickListener {
            uploadImage()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity.startMainActivity(authViewModel.user.value!!)
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
            authType = normalAuthType
            binding.submitButton.text = resources.getString(R.string.done)
        }
    }

    private fun createUriUploader(): FirebaseImageUploader {
        return FirebaseImageUploader.Builder()
            .withActivity(requireActivity())
            .withLifecycle(viewLifecycleOwner)
            .toPath(uploadPath)
            .imageUri(profileImageUri!!)
            .imageType(FirebaseImageUploader.PROFILE_IMAGE)
            .build()
    }

    private fun createUrlUploader(): FirebaseImageUploader {
        val path = replaceUri(authViewModel.googleProfileUri.toString())

        return FirebaseImageUploader.Builder()
            .withActivity(requireActivity())
            .withLifecycle(viewLifecycleOwner)
            .toPath(uploadPath)
            .imageUrl(path)
            .imageType(FirebaseImageUploader.PROFILE_IMAGE)
            .build()
    }

    private fun uploadImage() {
        startLoading()

        if (!isImageSelected) {
            activity.startMainActivity(authViewModel.user.value!!)
        } else {

            val uploader = when (authType){
                normalAuthType ->{
                    createUriUploader()
                }
                googleAuthType ->{
                    createUrlUploader()
                }
                else ->{
                    createUriUploader()
                }
            }

            //observe once the object's isCompleted variable
            uploader.isCompleted.observe(viewLifecycleOwner, object : Observer<Boolean>{
                override fun onChanged(isCompleted: Boolean?) {
                    if (isCompleted == null) return
                    if (isCompleted) {
                        activity.startMainActivity(authViewModel.user.value!!)
                    }
                    else {
                        isImageSelected = false
                        MySnackBar.createSnackBar(binding.screenRoot, resources.getString(R.string.error_failed_picture_upload))
                        binding.submitButton.text = resources.getString(R.string.skip)
                        stopLoading()
                    }

                    uploader.isCompleted.removeObserver(this)
                }
            })

            //start uploading
            lifecycleScope.launch {
                uploader.uploadAll()
            }
        }
    }

    private fun setUserData()
    {
        binding.firstName.text = authViewModel.user.value!!.firstname
        binding.lastName.text = authViewModel.user.value!!.lastname

        if (authViewModel.googleProfileUri != null){
            val path = replaceUri(authViewModel.googleProfileUri.toString())

            isImageSelected = true
            authType = googleAuthType
            profileImageUri = authViewModel.googleProfileUri!!
            binding.submitButton.text = resources.getString(R.string.done)

            Glide.with(activity)
                .load(path)
                .centerCrop()
                .into(binding.profilePicture)
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

    private fun replaceUri(string : String) : String{

        val regex = Regex("=s\\d+")

        return if (string.contains(regex)) string.replace(regex, "=s1080")
        else string.plus("=s1080-c")

    }
}