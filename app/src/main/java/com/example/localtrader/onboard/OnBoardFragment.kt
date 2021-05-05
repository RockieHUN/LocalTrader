package com.example.localtrader.onboard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentOnBoardBinding
import com.example.localtrader.onboard.adapters.OnBoardAdapter
import com.example.localtrader.onboard.screens.OnBoardFirstFragment
import com.example.localtrader.onboard.screens.OnBoardSecondFragment
import com.example.localtrader.onboard.screens.OnBoardThirdFragment
import java.util.*
import kotlin.concurrent.timerTask


class OnBoardFragment : Fragment(),
    OnBoardFirstFragment.nextScreenListener,
    OnBoardSecondFragment.nextScreenListener,
    OnBoardThirdFragment.nextScreenListener
{

    private lateinit var binding : FragmentOnBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_board, container, false)

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

        val fragments = arrayListOf(
            OnBoardFirstFragment(this),
            OnBoardSecondFragment(this),
            OnBoardThirdFragment(this)
        )

        binding.viewPager.isUserInputEnabled = false

        val adapter = OnBoardAdapter(fragments, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter


        return binding.root
    }

    override fun firstToSecond() {
        binding.viewPager.currentItem = 1
    }

    override fun secondToThird() {
        binding.viewPager.currentItem = 2
    }

    override fun thirdToRegister() {
        val sharedPref = requireActivity().getSharedPreferences("onBoard", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("played",true).apply()
        findNavController().navigate(R.id.action_onBoardFragment_to_registerFragment)
    }


}