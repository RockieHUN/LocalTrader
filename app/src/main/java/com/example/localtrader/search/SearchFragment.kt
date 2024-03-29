package com.example.localtrader.search

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentSearchBinding
import com.example.localtrader.search.adapters.SearchAdapter
import com.example.localtrader.viewmodels.BusinessViewModel

class SearchFragment : Fragment(),
SearchView.OnQueryTextListener,
SearchAdapter.onItemClickListener{

    private lateinit var binding : FragmentSearchBinding
    private val searchViewModel : SearchViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        setUpVisuals()
        setUpListeners()
        startSearchEvent()
        return binding.root
    }


    private fun startSearchEvent(){
        binding.searchView.requestFocus()
        showKeyboard()
        createRecycle()

        binding.searchView.isSubmitButtonEnabled = false
        binding.searchView.setOnQueryTextListener(this)

    }

    private fun setUpVisuals(){
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    private fun setUpListeners(){
        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.action_searchFragment_to_feedFragment)
        }
    }

    private fun createRecycle(){
        val adapter = SearchAdapter(this)
        binding.recycleView.adapter = adapter
        val verticalLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleView.layoutManager = verticalLayout
        binding.recycleView.setHasFixedSize(true)
        searchViewModel.results.observe(viewLifecycleOwner,{ results ->
            adapter.updateData(results)
        })
    }

    private fun showKeyboard(){
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchViewModel.search(newText, requireContext())
        }
        return true
    }

    override fun onItemClicked(id : String) {
        businessViewModel.businessId = id
        businessViewModel.originFragment = 4
        findNavController().navigate(R.id.action_searchFragment_to_businessProfileFragment)
    }
}