package com.example.localtrader.business.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentCreateBusinessFirstBinding
import com.example.localtrader.utils.Animations
import com.example.localtrader.viewmodels.CreateBusinessViewModel
import kotlinx.coroutines.launch

class CreateBusinessFirstFragment : Fragment() {

    private lateinit var binding : FragmentCreateBusinessFirstBinding
    private val creationViewModel : CreateBusinessViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_business_first, container, false)
        setSpinner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()
    }

    override fun onResume() {
        super.onResume()

        val uri = creationViewModel.business.imageUri
        if (uri != null)
        {
            binding.businessProfilePicture.setImageURI(uri)
        }
    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE

    }

    private fun setUpListeners()
    {
        binding.nextButton.setOnClickListener {
            submitData()
        }

        binding.businessProfilePicture.setOnClickListener {
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
            binding.businessProfilePicture.setImageURI(imageUri)
            creationViewModel.business.imageUri = imageUri
        }
    }

    private fun submitData()
    {
        val name = binding.businessName.text.toString()
        val category = binding.categories.selectedItem.toString()
        val description = binding.businessDescription.text.toString()

        val error = detectError(name, category, description)

        when (error){
            0 -> {
                creationViewModel.business.name = name
                creationViewModel.business.category = category
                creationViewModel.business.description = description
                findNavController().navigate(R.id.action_createBusinessFirstFragment_to_createBusinessSecondFragment)
            }
            1 -> showErrorMessage("A logó feltöltése kötelező!")
            2 -> showErrorMessage("A vállalkozás nevének legalább 3 karakterből kell állnia!")
            3 -> showErrorMessage("A vállalkozás leírása kötelező!")
        }
    }

    private fun showErrorMessage(msg : String)
    {
        lifecycleScope.launch{
            Animations.animateError(binding.errorMessageView, msg)
        }

    }

    private fun detectError(name : String, category : String, description: String) : Int
    {
        return if (creationViewModel.business.imageUri == null) 1
        else if (name.length < 3) 2
        else if (description.length < 1 ) 3
        else 0
    }

    private fun setSpinner()
    {
        val spinner = binding.categories
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_list,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

}