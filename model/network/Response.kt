package com.dacslab.android.sleeping.model.network

import com.dacslab.android.sleeping.model.User
import com.dacslab.android.sleeping.model.source.local.Token


abstract class BaseResponse {
    abstract val isSuccess: Boolean
    abstract val message: String?
    abstract val successData: Any?
    abstract val failData: Any?
}

data class RegisterResponse(
    override val isSuccess: Boolean,
    override val message: String? = null,
    override val successData: Any? = null,
    override val failData: Any? = null
) : BaseResponse()


data class LoginResponse(
    override val isSuccess: Boolean,
    override val message: String? = null,
    private val cookies: Token? = null,
    override val failData: Any? = null
) : BaseResponse() {
    override val successData: Token? = cookies // successData를 cookies에 매핑
}

data class RefreshResponse(
    val is401 : Boolean = false,
    override val isSuccess: Boolean,
    override val message: String? = null,
    val cookies: Token? = null,
    override val failData: Any? = null,
) : BaseResponse() {
    override val successData: Token? = cookies
}

// UserInfoResponse
data class UserInfoResponse(
    override val isSuccess: Boolean,
    override val message: String? = null,
    val userInfo: User? = null,
    override val failData: Any? = null
) : BaseResponse() {
    override val successData: User? = userInfo
}

data class BaseResponseImpl(
    override val isSuccess: Boolean,
    override val message: String? = null,
    override val successData: Any? = null,
    override val failData: Any? = null
) : BaseResponse()