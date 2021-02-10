package com.example.localtrader.main_screens.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.utils.MySharedPref
import com.example.localtrader.databinding.FragmentProfileBinding
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage


    private val userViewModel : UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        fireStore = Firebase.firestore
        storage = Firebase.storage
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container, false)

        loadProfileData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser == null)
        {
            MySharedPref.clearSharedPref(requireContext())
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }


    private fun setUpListeners()
    {
        binding.myBusinessButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_createBusinessFirstFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_profileFragment_to_timeLineFragment)
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun loadProfileData()
    {
        val uid = auth.currentUser!!.uid

        val user = userViewModel.user
        if  (user == null)
        {
           fireStore.collection("users")
               .document(uid)
               .get()
               .addOnSuccessListener { document ->
                   binding.userName.text = "${document["firstname"] as String} ${document["lastname"] as String}"
                   binding.userEmail.text = document["email"] as String
               }
        }
        else
        {
            binding.userName.text = "${user?.firstname} ${user?.lastname}"
            binding.userEmail.text = user?.email
        }





        //load profile image
        val imageReference = storage.reference.child("users/${uid}/profilePicture")

        imageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(requireContext())
                .load(uri)
                .centerCrop()
                .into(binding.profilePicture)
        }

       /* imageReference.getBytes(ONE_MEGABYTE)
            .addOnSuccessListener{ byteArray ->
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                binding.profilePicture.setImageBitmap(bitmap)
        }*/
    }

    private fun logout()
    {
        MySharedPref.clearSharedPref(requireContext())

        auth.signOut()
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

}