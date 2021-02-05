package com.example.localtrader.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.Utils.MySharedPref
import com.example.localtrader.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
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
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.toLogin.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.submitButton.setOnClickListener {
            login()
        }
    }


    private fun login()
    {

        binding.circularProgress.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isEmpty() || password.isEmpty())
        {
            binding.circularProgress.visibility = View.GONE
            binding.submitButton.visibility = View.VISIBLE

            lifecycleScope.launch{
                animateError("Kérjük, hogy töltsön ki minden mezőt!")
            }
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task->

                if (task.isSuccessful) {
                    MySharedPref.saveToSharedPref(requireContext(), email, password)
                    findNavController().navigate(R.id.action_loginFragment_to_timeLineFragment)
                }
                else{

                    binding.circularProgress.visibility = View.GONE
                    binding.submitButton.visibility = View.VISIBLE
                    lifecycleScope.launch{
                        animateError("Hibás e-mail vagy jelszó!")
                    }
                }
            }
    }

    //Show the error with animation
    private suspend fun animateError(errorMessage : String)
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

}