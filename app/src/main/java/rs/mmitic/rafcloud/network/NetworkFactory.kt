package rs.mmitic.rafcloud.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import rs.mmitic.rafcloud.BuildConfig

val BASE_URL = "http://ec2-13-58-10-146.us-east-2.compute.amazonaws.com:8080"

object NetworkFactory {

    private val retrofit: Retrofit = prepareClient()

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
    val machineService: MachineService by lazy {
        retrofit.create(MachineService::class.java)
    }

    private var authToken: String = ""

    fun saveAuth(username: String, password: String) {
        authToken = Credentials.basic(username, password)
    }

    fun clearAuth() {
        authToken = ""
    }

    private fun prepareClient(): Retrofit {
        val authInterceptor = Interceptor { chain ->
            val newRequest = chain
                .request()
                .newBuilder()
                .addHeader("Authorization", authToken)
                .build()

            chain.proceed(newRequest)
        }

        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) Level.BODY else Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(LocalDateAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}

class LocalDateAdapter {
    @ToJson
    fun toJson(value: LocalDate): String {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(value)
    }

    @FromJson
    fun fromJson(value: String): LocalDate {
        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
    }
}
