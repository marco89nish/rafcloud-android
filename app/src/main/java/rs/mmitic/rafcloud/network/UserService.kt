package rs.mmitic.rafcloud.network

import retrofit2.Response
import retrofit2.http.GET
import rs.mmitic.rafcloud.data.model.LoggedInUser


interface UserService {

    @GET("user")
    suspend fun getUserInfo(): Response<LoggedInUser>

}