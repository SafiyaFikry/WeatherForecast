package eg.gov.iti.jets.weatherapp.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.weatherapp.MainRule
import eg.gov.iti.jets.weatherapp.model.AlertsDB
import eg.gov.iti.jets.weatherapp.model.Current
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DaoText {

    @get:Rule
    val main = MainRule()
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var  database: WeatherDatabase

    @Before
    fun  createDataBase(){
        database = Room .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),WeatherDatabase::class.java).build()
    }

    @After
    fun closeDataBase()=database.close()

    @Test
    fun getStoredWeather_newRoot_getnewRoot()=main.runBlockingTest{
        //Given
        val root = Root(id=1, lat = 33.44, lon = -94.04, timezone = "united states", timezone_offset = 1000,
            current = Current(dt = 1646318689, sunrise = 112233563, sunset = 65245632, temp = 235.0, feels_like = 322.1, pressure = 2154, humidity =67, dew_point = 22.0, clouds = 45, uvi = 2.5, visibility = 55, weather = emptyList(), wind_deg = 55, wind_speed = 5.5),
            daily = emptyList(), hourly = emptyList(), alerts = emptyList()
        )
        database.getWeatherDao().insertWeather(root)
        //When
        var result:Root?=null
        val job=launch {
            database.getWeatherDao().getStoredWeather().collect {
                result = it
            }
        }
        job.cancel()
        //Then
        assertEquals(root,result)
    }


    @Test
    fun getStoredFavoritesDB_listNewFavorite_getListNewFavorite()=main.runBlockingTest{
        //Given
        val fav1 = FavoritesDB("egypt",23.2,55.1)
        val fav2 = FavoritesDB("france",33.2,86.2)
        val fav3 = FavoritesDB("korea", 96.2,12.2)
        database.getWeatherDao().insertFavoritesDB(fav1)
        database.getWeatherDao().insertFavoritesDB(fav2)
        database.getWeatherDao().insertFavoritesDB(fav3)
        var expected= listOf(fav1,fav2,fav3)

        //When
        var result:List<FavoritesDB>?=null
        val job=launch {
            database.getWeatherDao().getStoredFavoritesDB().collect {
                result = it
            }
        }
        job.cancel()
        //Then
       assertEquals(expected[0].lat.toString(), result!![0].lat.toString())

    }

    @Test
    fun insertFavoritesDB_newFavorite_returnNewFavorite()=main.runBlockingTest{

        //Given
        val fav1 = FavoritesDB("egypt",23.2,55.1)
        //When
        database.getWeatherDao().insertFavoritesDB(fav1)
        var result:List<FavoritesDB>?=null
        val job=launch {
            database.getWeatherDao().getStoredFavoritesDB().collect {
                result = it
            }
        }
        job.cancel()
        //Then
        assertEquals(result!![result!!.size-1].place,fav1.place)
    }

    @Test
    fun deleteFavoritesDB_newFavorite_newFavoriteDeleted()=main.runBlockingTest{
        //Given new alert added
        val fav1 = FavoritesDB("egypt",23.2,55.1)
        database.getWeatherDao().insertFavoritesDB(fav1)

        //When
        database.getWeatherDao().deleteFavoritesDB(fav1)

        //Then
        var result:List<FavoritesDB>?=null
        val job=launch {
            database.getWeatherDao().getStoredFavoritesDB().collect {
                result = it
            }
        }
        job.cancel()
        assertEquals(-1,result!!.indexOf(fav1))
    }

    @Test
    fun getStoredAlertsDB_listNewAlerts_getListNewAlerts()=main.runBlockingTest{
        //Given
        val alert1 = AlertsDB(id=1, countryName = "egypt", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "alert")
        val alert2 = AlertsDB(id=2, countryName = "korea", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "notification")
        database.getWeatherDao().insertAlertsDB(alert1)
        database.getWeatherDao().insertAlertsDB(alert2)
        var expected= listOf(alert1,alert2)

        //When
        var result:List<AlertsDB>?=null
        val job=launch {
            database.getWeatherDao().getStoredAlertsDB().collect {
                result = it
            }
        }
        job.cancel()

        //Then
        assertEquals(expected, result)
    }

    @Test
    fun insertAlertsDB_newAlert_returnNewAlert()=main.runBlockingTest{
        //Given
        val alert = AlertsDB(id=0,countryName = "egypt", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "alert")
        //When
        database.getWeatherDao().insertAlertsDB(alert)
        //Then
        var result:List<AlertsDB>?=null
        val job=launch {
            database.getWeatherDao().getStoredAlertsDB().collect {
                result = it
            }
        }
        job.cancel()
        assertEquals(result!![result!!.size-1].countryName,alert.countryName)
    }

    @Test
    fun deleteAlertsDB_newAlert_newAlertDeleted()=main.runBlockingTest{
        //Given new alert added
        val alert = AlertsDB(id = 0, countryName = "egypt", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "alert")
        database.getWeatherDao().insertAlertsDB(alert)

        //When
        database.getWeatherDao().deleteAlertsDB(alert)

        //Then
        var result:List<AlertsDB>?=null
        val job=launch {
            database.getWeatherDao().getStoredAlertsDB().collect {
                result = it
            }
        }
        job.cancel()
        assertEquals(-1,result!!.indexOf(alert))
    }
}