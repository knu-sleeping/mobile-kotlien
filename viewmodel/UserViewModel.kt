package com.dacslab.android.sleeping.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.network.SessionExpiredException
import com.dacslab.android.sleeping.model.repository.AuthRepository
import com.dacslab.android.sleeping.model.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
open class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val sharedSessionManager: SharedSessionManager
) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    private val _pwChangeResult = MutableStateFlow(false)
    val pwChangeResult: StateFlow<Boolean> = _pwChangeResult.asStateFlow()

    private val _navigateToLogin = MutableSharedFlow<Unit>(replay = 0)
    val navigateToLogin = _navigateToLogin.asSharedFlow()


    fun getUserInfo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // UserRepository의 getUserInfo 호출
                val result = userRepository.getUserInfo()

                _userInfo.value = result.second           // User 정보 (성공 시)
            } catch (e: SessionExpiredException) {
                Log.d("UserViewModel", "SessionExpiredException!!")
                authRepository.logout()
                sharedSessionManager.triggerSessionExpiredAlert()
                _navigateToLogin.emit(Unit)
            } catch (e: Exception) {
                _error.value = e.message ?: "사용자 정보를 가져오는 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun passwordChange(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // UserRepository의 passwordChange 호출
                val (isSuccess, message) = userRepository.passwordChange(currentPassword, newPassword)

                if (isSuccess) {
                    _pwChangeResult.value = true
                    Log.d("UserViewModel", "비밀번호 변경 성공: $message")
                } else {
                    _error.value = "비밀번호 변경 실패: $message"
                }
            } catch (e: SessionExpiredException) {
                _pwChangeResult.value = false
                Log.d("UserViewModel", "SessionExpiredException!!")
                authRepository.logout()
                sharedSessionManager.triggerSessionExpiredAlert()
                _navigateToLogin.emit(Unit)
            } catch (e: Exception) {
                _pwChangeResult.value = false
                _error.value = e.message ?: "비밀번호 변경 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearPwChangeResult() {
        _pwChangeResult.value = false
    }

    fun setError(message: String) {
        _error.value = message
    }


    fun clearError() {
        _error.value = null
    }
}
