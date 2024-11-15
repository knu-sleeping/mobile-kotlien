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
import kotlin.reflect.KProperty1


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

    private val _apiResult = MutableStateFlow(false)
    val apiResult: StateFlow<Boolean> = this._apiResult.asStateFlow()

    private val _pwVerifyApiResult = MutableStateFlow(false)
    val pwVerifyApiResult: StateFlow<Boolean> = this._pwVerifyApiResult.asStateFlow()

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

    fun updateUserInfo(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // UserRepository의 updateUserInfo 호출
                val (isSuccess, message) = userRepository.updateUserInfo(user)
                if (isSuccess) {
                    _apiResult.value = true
                    _userInfo.value = user // 업데이트 후 새로운 사용자 정보 설정
                    Log.d("UserViewModel", "유저 정보 수정 성공: $message")
                } else {
                    _apiResult.value = false
                    _error.value = message ?: "유저 정보 수정 실패"
                }
            } catch (e: SessionExpiredException) {
                _apiResult.value = false
                Log.d("UserViewModel", "SessionExpiredException!!")
                authRepository.logout()
                sharedSessionManager.triggerSessionExpiredAlert()
                _navigateToLogin.emit(Unit)
            } catch (e: Exception) {
                _apiResult.value = false
                _error.value = e.message ?: "유저 정보 수정 중 오류가 발생했습니다."
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
                    _apiResult.value = true
                    Log.d("UserViewModel", "비밀번호 변경 성공: $message")
                } else {
                    _error.value = "비밀번호 변경 실패: $message"
                }
            } catch (e: SessionExpiredException) {
                _apiResult.value = false
                Log.d("UserViewModel", "SessionExpiredException!!")
                authRepository.logout()
                sharedSessionManager.triggerSessionExpiredAlert()
                _navigateToLogin.emit(Unit)
            } catch (e: Exception) {
                _apiResult.value = false
                _error.value = e.message ?: "비밀번호 변경 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun passwordVerify(password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // UserRepository의 verifyPassword 호출
                val (isSuccess, message) = userRepository.verifyPassword(password)
                if (isSuccess) {
                    Log.d("UserViewModel", "비밀번호 검증 성공: $message")
                    _pwVerifyApiResult.value = true
                } else {
                    _error.value = message ?: "비밀번호가 일치하지 않습니다."
                }
            } catch (e: SessionExpiredException) {
                Log.d("UserViewModel", "SessionExpiredException!!")
                authRepository.logout()
                sharedSessionManager.triggerSessionExpiredAlert()
                _navigateToLogin.emit(Unit)
            } catch (e: Exception) {
                _error.value = e.message ?: "비밀번호 검증 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun deleteAccount() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // UserRepository의 deleteAccount 호출
                val (isSuccess, message) = userRepository.deleteAccount()

                if (isSuccess) {
                    _apiResult.value = true
                    Log.d("UserViewModel", "회원탈퇴 성공: $message")
                    authRepository.logout()
                    _navigateToLogin.emit(Unit)
                } else {
                    _apiResult.value = false
                    _error.value = message ?: "회원탈퇴 실패"
                }
            } catch (e: SessionExpiredException) {
                _apiResult.value = false
                Log.d("UserViewModel", "SessionExpiredException!!")
                authRepository.logout()
                sharedSessionManager.triggerSessionExpiredAlert()
                _navigateToLogin.emit(Unit)
            } catch (e: Exception) {
                _apiResult.value = false
                _error.value = e.message ?: "회원탈퇴 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun clearApiResult() {
        this._apiResult.value = false
        this._pwVerifyApiResult.value = false
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
}
