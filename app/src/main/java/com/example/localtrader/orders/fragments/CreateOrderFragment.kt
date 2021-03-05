package com.example.localtrader.orders.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.YesNoDialogFragment
import com.example.localtrader.databinding.FragmentCreateOrderBinding
import com.example.localtrader.orders.models.OrderRequest
import com.example.localtrader.services.notifications.NotificationRepository
import com.example.localtrader.services.notifications.models.NotificationData
import com.example.localtrader.services.notifications.models.PushNotification
import com.example.localtrader.utils.date.MyDateTime
import com.example.localtrader.viewmodels.ProductViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class CreateOrderFragment : Fragment(),
    YesNoDialogFragment.NoticeDialogListener {

    private lateinit var binding : FragmentCreateOrderBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    private val productViewModel : ProductViewModel by activityViewModels()
    private val userViewModel : UserViewModel by activityViewModels()

    private lateinit var notificationRepository : NotificationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        firestore = Firebase.firestore
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_order, container, false)
        setUpVisuals()
        setData()
        numberPicker()
        setUpListeners()

        return binding.root
    }

    private fun setUpVisuals(){
        binding.circularProgress.visibility = View.GONE
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    private fun setUpListeners(){
        binding.submitButton.setOnClickListener {
            val dialog = YesNoDialogFragment(resources.getString(R.string.create_order_verification),this)
            dialog.show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun numberPicker(){

        binding.numberPicker.minValue = 1
        binding.numberPicker.maxValue = 20
        binding.numberPicker.dividerPadding = 10
        binding.numberPicker.setOnValueChangedListener { _, _, newVal ->
            binding.sum.text = round((newVal * productViewModel.product.price)).toString()
        }
    }

    private fun setData(){
        val product = productViewModel.product
        binding.productName.text = product.name
        binding.sum.text = round(product.price).toString()

        storage.reference.child("products/${product.productId}/image").downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(requireActivity())
                    .load(uri)
                    .centerCrop()
                    .into(binding.productImage)
            }
    }

    private fun round(number: Double) : Double{
        val integer = (number * 100).toInt()
        return integer.toDouble()/100
    }

    private fun createOrder() : OrderRequest{
        val user = userViewModel.user.value
        val product = productViewModel.product

        return OrderRequest(
            businessId = product.businessId,
            clientId = auth.currentUser!!.uid,
            clientFirstName = user!!.firstname,
            clientLastName = user.lastname,
            productId = product.productId,
            productName = product.name,
            sum = round((binding.numberPicker.value * productViewModel.product.price)),
            count = binding.numberPicker.value,
            additionalComment = binding.additionalComment.text.toString(),
            date = MyDateTime.getCurrentTime()
        )
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        startLoading()
        sendNotification()
        val orderRequest = createOrder()

        firestore.collection("orderRequests")
            .add(orderRequest)
            .addOnSuccessListener { documentReference ->

                firestore.collection("orderRequests")
                    .document(documentReference.id)
                    .update("orderRequestId", documentReference.id)
                    .addOnSuccessListener {
                        stopLoading()
                        findNavController().navigate(R.id.action_createOrderFragment_to_businessOrdersFragment)
                    }
                    .addOnFailureListener { e->
                        Firebase.crashlytics.log(e.toString())
                        stopLoading()
                    }
            }
            .addOnFailureListener {  e ->
                Firebase.crashlytics.log(e.toString())
                stopLoading()
            }
        stopLoading()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        return
    }

    private fun startLoading() {
        binding.circularProgress.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.circularProgress.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }

    private fun sendNotification(){

        notificationRepository = NotificationRepository()

        val title ="test title"
        val message = "test message"
        val TOPIC = "/topics/myTopic"

        val notification = PushNotification(
            NotificationData(title,message),
            TOPIC
        )

        notificationRepository.sendNotification(notification)

    }


}