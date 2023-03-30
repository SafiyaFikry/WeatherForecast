package eg.gov.iti.jets.weatherapp.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eg.gov.iti.jets.weatherapp.model.AlertsDB
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertWeather(root:Root)
    suspend fun deleteWeather(root:Root)
    fun getAllStoredWeather(): Flow<Root>

    suspend fun insertFavoritesDB(favoritesDB: FavoritesDB)
    suspend fun deleteFavoritesDB(favoritesDB: FavoritesDB)
    fun getAllStoredFavoritesDB():Flow<List<FavoritesDB>>

   fun getStoredAlertsDB(): Flow<List<AlertsDB>>
    suspend fun insertAlertsDB(alertsDB: AlertsDB)
    suspend fun deleteAlertsDB(alertsDB: AlertsDB)

}