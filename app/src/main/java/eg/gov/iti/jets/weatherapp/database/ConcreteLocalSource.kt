package eg.gov.iti.jets.weatherapp.database

import android.content.Context
import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context:Context):LocalSource {
    private val dao:WeatherDao by lazy {
        val db:WeatherDatabase=WeatherDatabase.getInstance(context)
        db.getWeatherDao()
    }
    override suspend fun insertWeather(root: Root) {
        dao.insertWeather(root)
    }

    override suspend fun deleteWeather(root: Root) {
        dao.deleteWeather(root)
    }

    override fun getAllStoredWeather(): Flow<Root> {
        return dao.getStoredWeather()
    }
}