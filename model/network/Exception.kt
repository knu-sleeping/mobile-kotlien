package com.dacslab.android.sleeping.model.network

import java.io.IOException

class SessionExpiredException(message: String) : IOException(message)

/* -> SessionExpiredException EX USAGE.
        fun performNetworkRequest() {
    viewModelScope.launch {
        try {
            // 네트워크 요청 호출
            authRepository.someNetworkRequest()
        } catch (e: SessionExpiredException) {
            // 세션 만료 예외가 발생하면 로그인 화면으로 네비게이션
            _navigateToLogin.postValue(true)
        } catch (e: Exception) {
            // 기타 오류 처리
            Log.e("AuthViewModel", "Network request failed", e)
        }
    }
}
 */