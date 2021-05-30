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
import com.bumptech.glide.Glide
import com.example.localtrader.MainActivity
import com.example.localtrader.R
import com.example.localtrader.YesNoDialogFragment
import com.example.localtrader.databinding.FragmentProfileBinding
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProfileFragment : Fragment(),
        YesNoDialogFragment.NoticeDialogListener
{

    private lateinit var binding : FragmentProfileBinding
    private val auth = Firebase.auth
    private val  storage = Firebase.storage

    private val userViewModel : UserViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()

    private val profileImagePath ="users/${auth.currentUser!!.uid}/profilePicture/PROFILE_IMAGE_1080"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container, false)
        showUserData()
        hasBusiness()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()
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

        binding.favoritesSection.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_favoritesFragment)
        }

        binding.logoutSection.setOnClickListener {
            val dialog = YesNoDialogFragment(resources.getString(R.string.logout_confirmation),this)
            dialog.show(requireActivity().supportFragmentManager, null)
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


    private fun showUserData(){
        val user = userViewModel.user.value ?: return

        storage.reference.child(profileImagePath).downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .centerCrop()
                    .into(binding.profilePicture)
            }

        val text = "${user.firstname} ${user.lastname}"
        binding.userName.text = text
        binding.userEmail.text = user.email
    }


    private fun logout()
    {
        (requireActivity() as MainActivity).finishActivity()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        logout()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        return
    }

}