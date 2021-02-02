package com.example.localtrader.business.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class StatisticsFragment : Fragment() {

    private lateinit var binding : FragmentStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        monthlySellBarchart()
    }

    private fun monthlySellBarchart()
    {
        val months = mutableListOf("Január","Február","Március","Április")
        val barchart = binding.montlySellBarchart

        barchart.axisLeft.setDrawGridLines(false)
        barchart.axisRight.setDrawGridLines(false)
        barchart.description.isEnabled = false
        barchart.legend.isEnabled = false



        val list = mutableListOf<BarEntry>()

        list.add(BarEntry(1f,12f))
        list.add(BarEntry(2f,10f))
        list.add(BarEntry(3f,2f))
        list.add(BarEntry(4f,6f))

        barchart.xAxis.valueFormatter = IndexAxisValueFormatter(months)

        val dataset = BarDataSet(list,"Havi eladások")
        barchart.data = BarData(dataset)

    }

}