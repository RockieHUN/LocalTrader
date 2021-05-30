package com.example.localtrader.authentication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.authentication.AuthActivity
import com.example.localtrader.authentication.models.User
import com.example.localtrader.authentication.viewmodels.AuthViewModel
import com.example.localtrader.databinding.FragmentLoginBinding
import com.example.localtrader.utils.MySnackBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private val auth = Firebase.auth
    private lateinit var binding: FragmentLoginBinding
    private val authViewModel : AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()
    }

    private fun setUpVisuals() {
        binding.circularProgress.visibility = View.GONE
    }

    private fun setUpListeners() {

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.toLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.submitButton.setOnClickListener {
            login()
        }
    }


    private fun login() {

        startLoading()

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            stopLoading()

            lifecycleScope.launch {
                MySnackBar.createSnackBar(binding.screenRoot,"Kérjük, hogy töltsön ki minden mezőt!")
            }
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    authViewModel.user.observe(viewLifecycleOwner, object : Observer<User> {
                        override fun onChanged(t: User?) {
                            (requireActivity() as AuthActivity).startMainActivity(t!!)
                            authViewModel.user.removeObserver(this)
                        }

                    })
                    authViewModel.loadUser()

                } else {

                    stopLoading()
                    lifecycleScope.launch {
                        MySnackBar.createSnackBar(binding.screenRoot,"Hibás e-mail vagy jelszó!")
                    }
                }
            }
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