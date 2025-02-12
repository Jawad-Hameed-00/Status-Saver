package com.jawadjatoi.statussaver.viewmodels.factories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jawadjatoi.statussaver.data.StatusRepo
import com.jawadjatoi.statussaver.viewmodels.StatusViewModel

class StatusViewModelFactory(private val repo: StatusRepo):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return StatusViewModel(repo) as T
    }
}