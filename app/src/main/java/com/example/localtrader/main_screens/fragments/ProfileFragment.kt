package com.example.localtrader.main_screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.business.models.CreationalBusiness
import com.example.localtrader.databinding.FragmentProfileBinding
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.CreateBusinessViewModel
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

    private lateinit var uid : String

    private val userViewModel : UserViewModel by activityViewModels()
    private val creationViewModel : CreateBusinessViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        fireStore = Firebase.firestore
        storage = Firebase.storage

        uid = auth.currentUser!!.uid
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container, false)
        loadProfileData()
        hasBusiness()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()
    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser == null)
        {
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }


    override fun onPause() {
        super.onPause()
        userViewModel.removeBusinessObservers(viewLifecycleOwner)
    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    private fun setUpListeners()
    {
        binding.businessSection.setOnClickListener {
            val businessId = userViewModel.user.value?.businessId
            if (businessId == "" || businessId == null)
            {
                findNavController().navigate(R.id.action_profileFragment_to_createBusinessFirstFragment)
            }
            else{
                businessViewModel.businessId = businessId
                businessViewModel.businessOwner = auth.currentUser!!.uid
                findNavController().navigate(R.id.action_profileFragment_to_businessProfileFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_profileFragment_to_timeLineFragment)
        }

        binding.logoutSection.setOnClickListener {
            logout()
        }

        binding.bugReportSection.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_bugReportFragment)
        }
    }

    private fun hasBusiness() {
        val businessId = userViewModel.user.value?.businessId
        if (businessId != "") {
            binding.businessSectionText.text = resources.getText(R.string.my_business)
        }
        else {
            binding.businessSectionText.text = resources.getText(R.string.create_business)
        }
    }

    private fun loadProfileData()
    {
        uid = auth.currentUser!!.uid

        //load user information
        val user = userViewModel.user.value
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
            binding.userEmail.text = user.email
        }

        //load profile image
        if (userViewModel.downloadUri.value != null)
        {
            Glide.with(requireContext())
                .load(userViewModel.downloadUri.value)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .centerCrop()
                .into(binding.profilePicture)
        }
    }


    private fun logout()
    {
        creationViewModel.business = CreationalBusiness()
        auth.signOut()
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

}