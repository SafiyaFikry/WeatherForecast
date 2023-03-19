package eg.gov.iti.jets.weatherapp.network

import eg.gov.iti.jets.weatherapp.model.Root
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall?lat=33.44&lon=-94.04&appid=c76b13508b00d4f6977a17052745ff03")
    suspend fun getWeatherByLatAndLon(/*@Query("lat") lat: Double,@Query("lon") lon: Double,*/): Root
}

object RetrofitHelper{
    private const val baseUrl:String = "https://api.openweathermap.org/data/2.5/"
    val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
        baseUrl).build()
}
//https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&appid=c76b13508b00d4f6977a17052745ff03