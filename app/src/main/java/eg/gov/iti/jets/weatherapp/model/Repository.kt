package eg.gov.iti.jets.weatherapp.model

import eg.gov.iti.jets.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(var remoteSource: RemoteSource):RepositoryInterface{
    companion object{
        private var INSTANCE: Repository?=null
        fun getInstance(remoteSource: RemoteSource): Repository {
            return INSTANCE?: synchronized(this){
                val instance= Repository(remoteSource)
                INSTANCE=instance
                instance
            }
        }
    }

    override suspend fun getWeather(/*lat: Double, lon: Double*/): Flow<Root> {
        return remoteSource.getWeatherOverNetwork(/*lat,lon*/)
    }


}