package games.kidsand.mimikids.app.android.data.source.remote

import games.kidsand.mimikids.app.android.data.model.GuessWord
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://8fa39eb8942c.ngrok.io/api/"

private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

interface GameApiService {
    @GET("guess_things")
    fun guessThings(@Query("locale") locale: String): Call<GuessWord>
}

object GameApi {
    val retrofitService: GameApiService by lazy {
        retrofit.create(GameApiService::class.java)
    }
}