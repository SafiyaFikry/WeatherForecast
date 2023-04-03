package eg.gov.iti.jets.weatherapp.network

import eg.gov.iti.jets.weatherapp.model.Root
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface WeatherService {
    @GET("onecall?exclude=minutely&units=metric&appid=c76b13508b00d4f6977a17052745ff03")
    suspend fun getWeatherByLatAndLon(@Query("lat") lat: Double,@Query("lon") lon: Double,@Query("lang") lang:String): Root
}

object RetrofitHelper{
    //var client=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(15,TimeUnit.SECONDS).writeTimeout(15,TimeUnit.SECONDS)
    private const val baseUrl:String = "https://api.openweathermap.org/data/2.5/"
    val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
        baseUrl).build()
}
//https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&appid=c76b13508b00d4f6977a17052745ff03