package rs.mmitic.rafcloud.network

import retrofit2.Response
import retrofit2.http.*
import rs.mmitic.rafcloud.data.model.Machine


interface MachineService {

    @GET("machines/active")
    suspend fun getAll(): List<Machine>

    @POST("machines/create")
    suspend fun create(@Query("name") name: String?): Response<Unit>

    @POST("machines/start/uid/{uid}")
    suspend fun start(@Path("uid") uid: String): Response<Unit>

    @POST("machines/stop/uid/{uid}")
    suspend fun stop(@Path("uid") uid: String): Response<Unit>

    @POST("machines/destroy/uid/{uid}")
    suspend fun destroy(@Path("uid") uid: String): Response<Unit>

}