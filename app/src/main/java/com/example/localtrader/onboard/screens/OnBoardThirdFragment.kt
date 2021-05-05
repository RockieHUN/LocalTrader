package com.example.localtrader.onboard.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentOnBoardThirdBinding


class OnBoardThirdFragment(
    val listener : nextScreenListener
) : Fragment() {

    private lateinit var binding : FragmentOnBoardThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_board_third, container, false)

        binding.nextButton.setOnClickListener {
            listener.thirdToRegister()
        }

        return binding.root
    }

    interface nextScreenListener{
        fun thirdToRegister()
    }

}