package eg.gov.iti.jets.weatherapp.database

import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertWeather(root:Root)
    suspend fun deleteWeather(root:Root)
    fun getAllStoredWeather(): Flow<Root>
}