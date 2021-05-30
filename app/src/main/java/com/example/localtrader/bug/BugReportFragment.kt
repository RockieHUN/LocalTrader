package com.example.localtrader.bug

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.localtrader.R
import com.example.localtrader.bug.models.BugReport
import com.example.localtrader.databinding.FragmentBugReportBinding
import com.example.localtrader.utils.MySnackBar
import com.example.localtrader.utils.imageUtils.FirebaseImageUploader
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class BugReportFragment : Fragment() {

    private lateinit var binding : FragmentBugReportBinding
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val userViewModel : UserViewModel by activityViewModels()

    private var imageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bug_report,container,false)
        setUpVisuals()
        setSpinner()
        setUpListeners()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            binding.bugImage.setImageURI(imageUri)
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 2)
    }

    private fun setUpListeners(){
        binding.selectPicture.setOnClickListener {
            pickImageFromGallery()
        }

        binding.submitButton.setOnClickListener {
            sendErrorReport()
        }
    }

    private fun setUpVisuals(){
        binding.circularProgress.visibility = View.GONE
    }

    private fun startLoading() {
        binding.circularProgress.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.circularProgress.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }

    private fun setSpinner()
    {
        val spinner = binding.categories
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.bugReport_category_list,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun sendErrorReport(){
        startLoading()

        val bugReport = generateBugReport()

        if (bugReport != null){
            firestore.collection("bugReports")
                .add(bugReport)
                .addOnSuccessListener { documentReference ->

                    firestore.collection("bugReports")
                        .document(documentReference.id)
                        .update("reportId", documentReference.id)
                        .addOnSuccessListener {

                            if (imageUri != null){
                                val uploadPath = "bugReports/${documentReference.id}"
                                val uploader = FirebaseImageUploader.Builder()
                                    .withActivity(requireActivity())
                                    .withLifecycle(viewLifecycleOwner)
                                    .toPath(uploadPath)
                                    .imageUri(imageUri!!)
                                    .imageType(FirebaseImageUploader.BUG_REPORT_IMAGE)
                                    .build()

                                uploader.isCompleted.observe(viewLifecycleOwner, object : Observer<Boolean>{
                                    override fun onChanged( isCompleted : Boolean?) {
                                        if (isCompleted == null) return

                                        if (isCompleted){
                                            MySnackBar.createSnackBar(binding.screenRoot,resources.getString(R.string.thanks_the_report))
                                        }
                                        else{
                                            MySnackBar.createSnackBar(binding.screenRoot, resources.getString(R.string.error_failed_picture_upload))
                                        }

                                        stopLoading()
                                        uploader.isCompleted.removeObserver(this)
                                    }
                                })

                                lifecycleScope.launch {
                                    uploader.uploadAll()
                                }
                            }
                            else{
                                stopLoading()
                                MySnackBar.createSnackBar(binding.screenRoot,resources.getString(R.string.thanks_the_report))
                            }
                        }
                        .addOnFailureListener {
                            stopLoading()
                            showErrorToUser()
                        }
                }
                .addOnFailureListener {
                    stopLoading()
                    showErrorToUser()
                }
        }
        else{
            stopLoading()
        }
    }

    private fun generateBugReport() : BugReport?{

        val category = binding.categories.selectedItem.toString()
        val description = binding.bugDescription.text.toString()

        if (description.isNotEmpty()){
            return try{
                val bugReport = BugReport(
                    userId = auth.currentUser!!.uid,
                    userFirstName = userViewModel.user.value!!.firstname,
                    userLastName = userViewModel.user.value!!.lastname,
                    userEmail = userViewModel.user.value!!.email,
                    category = category,
                    description = description
                )
                bugReport
            } catch (e : Exception) {
                Firebase.crashlytics.log(e.toString())
                null
            }
        }
        else{
            MySnackBar.createSnackBar(binding.screenRoot,resources.getString(R.string.error_missing_description))
            return null
        }
    }

    private fun showErrorToUser(){
        val error = resources.getString(R.string.error_unknown)
        MySnackBar.createSnackBar(binding.screenRoot,error)
    }


}