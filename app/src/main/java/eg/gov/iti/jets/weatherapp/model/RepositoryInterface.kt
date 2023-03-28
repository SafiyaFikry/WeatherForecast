package eg.gov.iti.jets.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun  getWeather(lat:Double,lon:Double,lang:String): Flow<Root>
    fun  getAllStoredWeather(): Flow<Root>
    suspend fun  insertWeather(root:Root)
    suspend fun  deleteWeather(root:Root)
}