package com.dacslab.android.sleeping.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSessionManager @Inject constructor() {
    private val _showSessionExpiredAlert = MutableStateFlow(false)
    val showSessionExpiredAlert: StateFlow<Boolean> = _showSessionExpiredAlert

    fun triggerSessionExpiredAlert() {
        _showSessionExpiredAlert.value = true
    }

    fun resetSessionExpiredAlert() {
        _showSessionExpiredAlert.value = false
    }
}
