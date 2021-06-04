package com.example.localtrader.authentication.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.authentication.AuthActivity
import com.example.localtrader.authentication.Authenticator
import com.example.localtrader.authentication.models.User
import com.example.localtrader.authentication.viewmodels.AuthViewModel
import com.example.localtrader.databinding.FragmentSelectAuthMethodBinding
import com.example.localtrader.utils.MySnackBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import java.util.*
import kotlin.concurrent.timerTask

class SelectAuthMethodFragment : Fragment(),
    Authenticator.GoogleAuthListener{

    private val authViewModel : AuthViewModel by activityViewModels()
    private lateinit var binding : FragmentSelectAuthMethodBinding
    private lateinit var  authenticator : Authenticator
    private val googleAuthRequestCode = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticator = Authenticator(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_auth_method, container, false)
        setUpListeners()
        return binding.root
    }

    private fun setUpListeners(){
        binding.registerSection.setOnClickListener{
            findNavController().navigate(R.id.action_selectAuthMethodFragment_to_registerFragment)
        }

        binding.loginSection.setOnClickListener {
            findNavController().navigate(R.id.action_selectAuthMethodFragment_to_loginFragment)
        }

        binding.googleSignInButton.setOnClickListener {
            registerWithGoogle()
        }

        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(requireContext(), resources.getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)
                callbackCounter++
            } else requireActivity().finish()
        }
    }

    private fun registerWithGoogle(){
        authenticator.startGoogleAuthActivity(requireActivity())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googleAuthRequestCode){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                authenticator.loginWithGoogle(account)
            }
            catch (e : ApiException){
                Log.d("GoogleSignIn", e.toString())
            }
        }
    }

    override fun onError(msg: String) {
        MySnackBar.createSnackBar(binding.screenRoot, msg)
    }

    override fun onGoogleRegisterCompletion(authUser: User, googleProfileUri : Uri?) {
        authViewModel.user.value = authUser
        authViewModel.googleProfileUri = googleProfileUri
        findNavController().navigate(R.id.action_selectAuthMethodFragment_to_finishRegistrationFragment)
    }

    override fun onGoogleLoginCompletion(authUser: User) {
        val activity = requireActivity() as AuthActivity
        activity.startMainActivity(authUser)
    }

    override fun onGoogleAuthActivityStart(intent : Intent) {
        startActivityForResult(intent, googleAuthRequestCode)
    }
}