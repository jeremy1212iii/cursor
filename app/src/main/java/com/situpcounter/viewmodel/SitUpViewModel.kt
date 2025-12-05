package com.situpcounter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for Sit-Up Counter
 * Manages UI state and business logic
 */
class SitUpViewModel : ViewModel() {
    
    private val _count = MutableLiveData<Int>(0)
    val count: LiveData<Int> = _count
    
    private val _isCounting = MutableLiveData<Boolean>(false)
    val isCounting: LiveData<Boolean> = _isCounting
    
    private val _statusText = MutableLiveData<String>("Position yourself in front of the camera")
    val statusText: LiveData<String> = _statusText
    
    fun updateCount(newCount: Int) {
        _count.value = newCount
    }
    
    fun startCounting() {
        _isCounting.value = true
        _statusText.value = "Counting sit-ups..."
    }
    
    fun pauseCounting() {
        _isCounting.value = false
        _statusText.value = "Paused"
    }
    
    fun reset() {
        _count.value = 0
        _isCounting.value = false
        _statusText.value = "Position yourself in front of the camera"
    }
}
