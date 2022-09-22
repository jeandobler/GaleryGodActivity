package com.dobler.galerygodactivity.viewmodel

import com.dobler.galerygodactivity.model.FindResult

sealed class MainViewModelResults {

    data class error(val message: String) : MainViewModelResults()
    data class success(val body: FindResult) : MainViewModelResults()

}
