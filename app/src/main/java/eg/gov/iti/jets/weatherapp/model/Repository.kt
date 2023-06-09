package eg.gov.iti.jets.weatherapp.model

import eg.gov.iti.jets.weatherapp.database.LocalSource
import eg.gov.iti.jets.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(var remoteSource: RemoteSource,var localSource: LocalSource):RepositoryInterface{
    companion object{
        private var INSTANCE: Repository?=null
        fun getInstance(remoteSource: RemoteSource,localSource: LocalSource): Repository {
            return INSTANCE?: synchronized(this){
                val instance= Repository(remoteSource, localSource)
                INSTANCE=instance
                instance
            }
        }
    }

    override suspend fun getWeather(lat: Double, lon: Double,lang:String): Flow<Root> {
        return remoteSource.getWeatherOverNetwork(lat,lon,lang)
    }

    override fun getAllStoredWeather(): Flow<Root> {
        return localSource.getAllStoredWeather()
    }



    override suspend fun insertWeather(root: Root) {
        localSource.insertWeather(root)
    }

    override suspend fun deleteWeather(root: Root) {
        localSource.deleteWeather(root)
    }

    override fun getAllStoredFavoritesDB(): Flow<List<FavoritesDB>> {
        return localSource.getAllStoredFavoritesDB()
    }

    override suspend fun insertFavoritesDB(favoritesDB: FavoritesDB) {
        localSource.insertFavoritesDB(favoritesDB)
    }

    override suspend fun deleteFavoritesDB(favoritesDB: FavoritesDB) {
        localSource.deleteFavoritesDB(favoritesDB)
    }

    override fun getAllStoredAlertsDB(): Flow<List<AlertsDB>> {
        return localSource.getStoredAlertsDB()
    }

    override suspend fun insertAlertsDB(alertsDB: AlertsDB) {
        localSource.insertAlertsDB(alertsDB)
    }

    override suspend fun deleteAlertsDB(alertsDB: AlertsDB) {
        localSource.deleteAlertsDB(alertsDB)
    }


}