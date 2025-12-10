package com.example.docstatus.data.repository

import com.example.docstatus.data.network.NetworkClient

/**
 * A repository that handles authentication-related data operations.
 *
 * This class abstracts the data source for login operations, delegating the actual
 * network call to the [NetworkClient].
 */
class LoginRepository {

    /**
     * Performs a login request via the network client.
     *
     * @param username The user's username.
     * @param password The user's password.
     * @return A [LoginResponse] on success.
     */
    suspend fun login(username: String, password: String) = NetworkClient.api.login(username, password)

}