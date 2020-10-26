package games.kidsand.mimikids.app.android.data.source.remote

import games.kidsand.mimikids.app.android.BuildConfig.BASE_SERVER_URL
import games.kidsand.mimikids.app.android.data.model.GuessWord
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

private val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor(logging)

private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_SERVER_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient.build())
        .build()

interface GameApiService {
    @GET("guess/{category}")
    fun guess(
            @Path("category") category: String,
            @Query("locale") locale: String
    ): Call<GuessWord>
}

object GameApi {
    val retrofitService: GameApiService by lazy {
        retrofit.create(GameApiService::class.java)
    }
}