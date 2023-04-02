package eg.gov.iti.jets.weatherapp.database

import eg.gov.iti.jets.weatherapp.model.AlertsDB
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource:LocalSource {
    val list:MutableList<Root> = arrayListOf()
    val alertList:MutableList<AlertsDB> = arrayListOf()
    val favList:MutableList<FavoritesDB> = arrayListOf()
    override suspend fun insertWeather(root: Root) {
        list.add(root)
    }

    override suspend fun deleteWeather(root: Root) {
        TODO("Not yet implemented")
    }

    override fun getAllStoredWeather(): Flow<Root> {
        return flowOf( list[0])
    }

    override suspend fun insertFavoritesDB(favoritesDB: FavoritesDB) {
        favList.add(favoritesDB)
    }

    override suspend fun deleteFavoritesDB(favoritesDB: FavoritesDB) {
        favList.remove(favoritesDB)
    }

    override fun getAllStoredFavoritesDB(): Flow<List<FavoritesDB>> {
        return flowOf(favList)
    }

    override fun getStoredAlertsDB(): Flow<List<AlertsDB>> {
        return flowOf(alertList)
    }

    override suspend fun insertAlertsDB(alertsDB: AlertsDB) {
        alertList.add(alertsDB)
    }

    override suspend fun deleteAlertsDB(alertsDB: AlertsDB) {
        alertList.remove(alertsDB)
    }
}