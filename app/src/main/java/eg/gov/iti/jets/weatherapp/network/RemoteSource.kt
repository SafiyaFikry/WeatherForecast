package eg.gov.iti.jets.weatherapp.network

import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.flow.Flow

interface RemoteSource {
    suspend fun getWeatherOverNetwork(lat:Double,lon:Double,lang:String): Flow<Root>
}