package com.example.localtrader.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.localtrader.repositories.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataStoreViewModel(application : Application) : AndroidViewModel(application) {

    private val repository = DataStoreRepository(application)

    val locationDialogIsShown = repository.locationNoticeIsShowed().asLiveData()

    fun locationNotificationShowed()
    {
        viewModelScope.launch (Dispatchers.IO ){
            repository.locationNoticeShowed()
        }
    }
}