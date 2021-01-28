package rs.mmitic.rafcloud.data

import rs.mmitic.rafcloud.data.model.LoggedInUser
import rs.mmitic.rafcloud.network.NetworkFactory
import java.io.IOException

object LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    suspend fun login(username: String, password: String): Result<LoggedInUser> = try {
        NetworkFactory.saveAuth(username, password)
        val response = NetworkFactory.userService.getUserInfo()

        if (response.isSuccessful) {
            val loggedInUser: LoggedInUser = response.body()!!
            user = loggedInUser
            Result.Success(loggedInUser)
        } else
            Result.Error(AuthFailedException())
    } catch (e: Throwable) {
        Result.Error(IOException("Error contacting the server", e))
    }

    fun logout() {
        user = null
        NetworkFactory.clearAuth()
    }
}

class AuthFailedException : Exception()