package com.example.docstatus.data.network

import com.example.docstatus.data.model.LoginResponse
import com.example.docstatus.data.model.VerificationRequest
import com.example.docstatus.data.model.VerificationResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {

    /**
     * @param username
     * @param password
     * @return A [LoginResponse]
     */
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    /**
     * @param request [VerificationRequest]
     * @return A [VerificationResponse]
     */
    @POST("verify/check")
    suspend fun verifyDocument(@Body request: VerificationRequest): VerificationResponse

    companion object {
        const val BASE_URL = "http://193.58.121.196/api/v1/"
    }
}