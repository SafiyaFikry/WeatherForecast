package eg.gov.iti.jets.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun  getWeather(lat:Double,lon:Double,lang:String): Flow<Root>
    fun  getAllStoredWeather(): Flow<Root>
    suspend fun  insertWeather(root:Root)
    suspend fun  deleteWeather(root:Root)

    fun  getAllStoredFavoritesDB(): Flow<List<FavoritesDB>>
    suspend fun  insertFavoritesDB(favoritesDB: FavoritesDB)
    suspend fun  deleteFavoritesDB(favoritesDB: FavoritesDB)

    fun getAllStoredAlertsDB(): Flow<List<AlertsDB>>
    suspend fun insertAlertsDB(alertsDB: AlertsDB)
    suspend fun deleteAlertsDB(alertsDB: AlertsDB)
}