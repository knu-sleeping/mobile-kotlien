package com.dacslab.android.sleeping.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.network.SessionExpiredException
import com.dacslab.android.sleeping.model.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val sharedSessionManager: SharedSessionManager
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult.asStateFlow()

    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> = _registerResult.asStateFlow()


    fun login(userId: String, userPw: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = authRepository.login(userId, userPw)
                _loginResult.value = result.first  // Boolean 값 (성공 여부) 저장
                _error.value = if (result.first) null else result.second  // 실패 시 오류 메시지 저장

            } catch (e: Exception) {
                _error.value = e.message ?: "로그인 중 오류가 발생했습니다"
                _loginResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = authRepository.register(user)
                _registerResult.value = result.first  // Boolean 값 (성공 여부) 저장
                _error.value = if (result.first) null else result.second  // 실패 시 오류 메시지 저장

            } catch (e: Exception) {
                _error.value = e.message ?: "회원가입 중 오류가 발생했습니다"
                _registerResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                authRepository.logout()
                _loginResult.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "로그아웃 중 오류가 발생했습니다"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun checkToken() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                authRepository.checkToken()

            } catch (e: SessionExpiredException) {
                Log.d("AuthViewModel", "SessionExpiredException during token check")
                authRepository.logout()
                sharedSessionManager.triggerSessionExpiredAlert()
            } catch (e: Exception) {
                // 기타 예외 발생 시 오류 메시지를 설정
                _error.value = e.message ?: "토큰 유효성 검사 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun isAuthenticated(): Boolean {
        return authRepository.isUserAuthenticated()
    }


    fun clearLoginResult() {
        _loginResult.value = null
    }

    fun clearRegisterResult() {
        _registerResult.value = null
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
}