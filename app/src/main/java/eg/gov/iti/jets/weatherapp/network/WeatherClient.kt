package eg.gov.iti.jets.weatherapp.network

import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherClient private constructor():RemoteSource{
    val weatherService:WeatherService by lazy {
        RetrofitHelper.retrofit.create(WeatherService::class.java)
    }

    companion object{
        private var INSTANCE:WeatherClient?=null
        fun getInstance():WeatherClient{
            return INSTANCE?: synchronized(this){
                val instance=WeatherClient()
                INSTANCE=instance
                instance
            }
        }
    }
    override suspend fun getWeatherOverNetwork(/*lat:Double,lon:Double*/): Flow<Root> {
        val responce=weatherService.getWeatherByLatAndLon(/*lat,lon*/)
        return flowOf(responce)
    }
}