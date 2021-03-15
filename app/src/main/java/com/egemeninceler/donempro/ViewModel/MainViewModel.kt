package com.egemeninceler.donempro.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    val alive: MutableLiveData<Boolean> by lazy() {
        MutableLiveData<Boolean>()
    }

    fun setTrue() {
        alive.value = true
    }
    fun setFalse() {
        alive.value = false

    }

}