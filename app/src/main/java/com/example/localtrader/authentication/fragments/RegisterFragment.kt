package com.example.localtrader.authentication.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.authentication.Authenticator
import com.example.localtrader.authentication.models.RegistrationUser
import com.example.localtrader.authentication.models.User
import com.example.localtrader.authentication.viewmodels.AuthViewModel
import com.example.localtrader.databinding.FragmentRegisterBinding
import com.example.localtrader.utils.MySnackBar


class RegisterFragment : Fragment(),
    Authenticator.NormalAuthListener{

    private lateinit var binding : FragmentRegisterBinding
    private val authViewModel : AuthViewModel by activityViewModels()
    private lateinit var  authenticator : Authenticator

    private var pwShown = false
    private var pwAgainShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticator = Authenticator(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register,container,false)
        setUpVisuals()
        setUpListeners()
        return binding.root
    }


    private fun setUpVisuals()
    {
        binding.circularProgress.visibility = View.GONE
    }

    private fun setUpListeners()
    {

        binding.submitButton.setOnClickListener{
          registerWithLocalTrader()
        }


        binding.termsWarning.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_userAgreementsFragment)
        }

        //show passwords
        binding.showPasswordButton.setOnClickListener {
           switchFirstPasswordInput()
        }

        binding.showPasswordAgainButton.setOnClickListener {
          switchSecondPasswordInput()
        }
    }


    private fun collectData() : RegistrationUser{
        val firstname = binding.firstname.text.toString()
        val lastname = binding.lastname.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val passwordAgain = binding.passwordAgain.text.toString()

        return RegistrationUser(firstname,lastname,email,password,passwordAgain)
    }


    private fun registerWithLocalTrader()
    {
        startLoading()
        val userData = collectData()
        authenticator.registerWithLocalTrader(userData)
    }

    private fun startLoading() {
        binding.circularProgress.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.circularProgress.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }

    private fun switchFirstPasswordInput(){
        if (pwShown)
        {
            binding.password.transformationMethod = PasswordTransformationMethod()
            binding.showPasswordButton.colorFilter = null
            pwShown= false
        }
        else{
            binding.password.transformationMethod = null
            binding.showPasswordButton.setColorFilter(Color.argb(255, 2, 183, 219))
            pwShown = true
        }
    }

    private fun switchSecondPasswordInput(){
        if (pwAgainShown)
        {
            binding.passwordAgain.transformationMethod = PasswordTransformationMethod()
            binding.showPasswordAgainButton.colorFilter = null
            pwAgainShown= false
        }
        else{
            binding.passwordAgain.transformationMethod = null
            binding.showPasswordAgainButton.setColorFilter(Color.argb(255, 2, 183, 219))
            pwAgainShown = true
        }
    }

    override fun onError(msg: String) {
        MySnackBar.createSnackBar(binding.screenRoot, msg)
        stopLoading()
    }

    override fun onNormalAuthCompletion(authUser : User) {
        authViewModel.user.value = authUser
        findNavController().navigate(R.id.action_registerFragment_to_finishRegistrationFragment)
    }

}