package com.example.localtrader.authentication

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.utils.MySharedPref
import com.example.localtrader.authentication.Models.RegistrationUser
import com.example.localtrader.authentication.Models.User
import com.example.localtrader.databinding.FragmentRegisterBinding
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask


class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    private val userViewModel : UserViewModel by activityViewModels()

    private lateinit var data : RegistrationUser
    private var errorMessage = "none"

    private var pwShown = false
    private var pwAgainShown = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register,container,false)
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
        binding.circularProgress.visibility = View.GONE
    }

    private fun setUpListeners()
    {
        //checking input and registering
        binding.submitButton.setOnClickListener{

           startLoading()

            if (dataIsValid())
            {
                register()
            }
            else
            {
                stopLoading()
                lifecycleScope.launch {
                    animateError()
                }

            }
        }

        //navigate to login screen
        binding.toLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        //callback
        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(requireContext(), "Press again to exit", Toast.LENGTH_SHORT).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)
                callbackCounter++
            } else requireActivity().finish()
        }

        //show passwords
        binding.showPasswordButton.setOnClickListener {
            if (pwShown)
            {
                binding.password.transformationMethod = null
                pwShown= false
            }
            else{
                binding.password.transformationMethod = PasswordTransformationMethod()
                pwShown = true
            }
        }

        binding.showPasswordAgainButton.setOnClickListener {
            if (pwAgainShown)
            {
                binding.passwordAgain.transformationMethod = null
                pwAgainShown= false
            }
            else{
                binding.passwordAgain.transformationMethod = PasswordTransformationMethod()
                pwAgainShown = true
            }
        }
    }

    //Show the error with animation
    private suspend fun animateError()
    {
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


    //check if input data is valid
    private fun dataIsValid () : Boolean
    {
        //get inputData
        val firstname = binding.firstname.text.toString()
        val lastname = binding.lastname.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val passwordAgain = binding.passwordAgain.text.toString()



        //check data
        if (password != passwordAgain){
            errorMessage = "A jelszavak nem egyeznek!"
            return false
        }
        if (firstname.length < 2 || firstname.length > 40)
        {
            errorMessage = "Helytelen vezetéknév"
            return false
        }
        if (lastname.length < 2 || lastname.length > 40)
        {
            errorMessage = "Helytelen keresztnév"
            return false
        }

        //val emailRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}\$".toRegex()
        //must contain least 8 characters, 1 number, 1 upper and 1 lowercase [duplicate]
        val passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$".toRegex();


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            errorMessage = "Helytelen e-mail!"
            return false
        }

        if (!passwordRegex.matches(password))
        {
            errorMessage = "A jelszónak legalább a következőket kell tartalmaznia: 1 szám, 1 nagybetű, 1 kisbetű!"
            return false
        }

        data = RegistrationUser(firstname,lastname,email,password,passwordAgain)
        return true


    }

    //register the user and save data to sharedPref
    private fun register()
    {

        auth.createUserWithEmailAndPassword(data.email!!, data.password!!)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    //save additional info to FireStore
                    saveToFireStore()

                    //save to shared preferences, then navigate forward
                    MySharedPref.saveToSharedPref(requireContext(), data.email!!, data.password!!)
                    findNavController().navigate(R.id.action_registerFragment_to_finishRegistrationFragment)
                }
                else
                {
                    binding.submitButton.visibility = View.VISIBLE
                    binding.circularProgress.visibility = View.GONE
                    errorMessage = "A regisztráció ismeretlen okból nem sikerült. Próbálja újra később"
                }
            }
    }

    fun saveToFireStore()
    {

        //save to FireStore
        val newUser = User(data.firstname!! , data.lastname!!, data.email!!)
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .set(newUser)

        //add to viewModel
        userViewModel.user = newUser
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