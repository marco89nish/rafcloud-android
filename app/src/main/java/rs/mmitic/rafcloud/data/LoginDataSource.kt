package rs.mmitic.rafcloud.data

import rs.mmitic.rafcloud.data.model.LoggedInUser
import rs.mmitic.rafcloud.network.NetworkFactory
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
//class LoginDataSource {
//
//    suspend fun login(username: String, password: String): Result<LoggedInUser> {
//        return try {
//            NetworkFactory.saveAuth(username, password)
//            val response = NetworkFactory.userService.getUserInfo()
//
//            if (response.isSuccessful) Result.Success(response.body()!!)
//            else Result.Error(AuthFailedException())
//        } catch (e: Throwable) {
//            Result.Error(IOException("Error contacting the server", e))
//        }
//    }
//
//    fun logout() {
//        NetworkFactory.clearAuth()
//    }
//}
//
//class AuthFailedException : Exception()
//
