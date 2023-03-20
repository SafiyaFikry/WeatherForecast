package eg.gov.iti.jets.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun  getWeather(lat:Double,lon:Double,lang:String): Flow<Root>
}