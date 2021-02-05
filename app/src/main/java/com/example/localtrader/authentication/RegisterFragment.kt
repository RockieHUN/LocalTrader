package com.example.localtrader.authentication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.localtrader.Utils.MySharedPref
import com.example.localtrader.authentication.Models.RegistrationUser
import com.example.localtrader.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask


class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var auth : FirebaseAuth

    private val registrationViewModel : RegistrationViewModel by activityViewModels()

    private lateinit var data : RegistrationUser
    private var errorMessage = "none";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
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
        //navigation
        binding.submitButton.setOnClickListener{

            binding.submitButton.visibility = View.GONE
            binding.circularProgress.visibility = View.VISIBLE

            if (dataIsValid())
            {
                register()
            }
            else
            {
                binding.submitButton.visibility = View.VISIBLE
                binding.circularProgress.visibility = View.GONE
                lifecycleScope.launch {
                    animateError()
                }

            }
        }

        binding.toLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

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
        if (firstname.length < 2 || firstname.length > 20)
        {
            errorMessage = "Helytelen vezetéknév"
            return false
        }
        if (lastname.length < 2 || lastname.length > 20)
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



}