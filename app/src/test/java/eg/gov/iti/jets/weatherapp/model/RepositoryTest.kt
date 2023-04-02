package eg.gov.iti.jets.weatherapp.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eg.gov.iti.jets.weatherapp.MainRule
import eg.gov.iti.jets.weatherapp.database.FakeLocalSource
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class RepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val main = MainRule()

    lateinit var localSource: FakeLocalSource
    lateinit var repository: Repository

    @Before
    fun setup() {
        localSource = FakeLocalSource()
        repository = Repository.getInstance(WeatherClient.getInstance(), localSource)
    }

    @Test
    fun getStoredWeather_newRoot_getnewRoot() = main.runBlockingTest {
        //Given
        val root = Root(
            id = 1, lat = 33.44, lon = -94.04, timezone = "united states", timezone_offset = 1000,
            current = Current(
                dt = 1646318689,
                sunrise = 112233563,
                sunset = 65245632,
                temp = 235.0,
                feels_like = 322.1,
                pressure = 2154,
                humidity = 67,
                dew_point = 22.0,
                clouds = 45,
                uvi = 2.5,
                visibility = 55,
                weather = emptyList(),
                wind_deg = 55,
                wind_speed = 5.5
            ),
            daily = emptyList(), hourly = emptyList(), alerts = emptyList()
        )
        repository.insertWeather(root)
        //When
        var result: Root? = null

        repository.getAllStoredWeather().collect {
            result = it
        }

        //Then
        assertEquals(root, result)
    }


    @Test
    fun getStoredFavoritesDB_listNewFavorite_getListNewFavorite() = main.runBlockingTest {
        //Given
        val fav1 = FavoritesDB("egypt", 23.2, 55.1)
        val fav2 = FavoritesDB("france", 33.2, 86.2)
        val fav3 = FavoritesDB("korea", 96.2, 12.2)
        repository.insertFavoritesDB(fav1)
        repository.insertFavoritesDB(fav2)
        repository.insertFavoritesDB(fav3)
        var expected = listOf(fav1, fav2, fav3)

        //When
        var result: List<FavoritesDB>? = null

        repository.getAllStoredFavoritesDB().collect {
            result = it
        }

        //Then
        assertEquals(expected[0].place, result!![0].place)

    }

    @Test
    fun insertFavoritesDB_newFavorite_returnNewFavorite() = main.runBlockingTest {

        //Given
        val fav1 = FavoritesDB("egypt", 23.2, 55.1)
        //When
        repository.insertFavoritesDB(fav1)
        var result: List<FavoritesDB>? = null

        repository.getAllStoredFavoritesDB().collect {
            result = it
        }

        //Then
        assertEquals(result!![result!!.size - 1].place, fav1.place)
    }

    @Test
    fun deleteFavoritesDB_newFavorite_newFavoriteDeleted() = main.runBlockingTest {
        //Given new alert added
        val fav1 = FavoritesDB("egypt", 23.2, 55.1)
        repository.insertFavoritesDB(fav1)

        //When
        repository.deleteFavoritesDB(fav1)

        //Then
        var result: List<FavoritesDB>? = null

        repository.getAllStoredFavoritesDB().collect {
            result = it
        }

        assertEquals(-1, result!!.indexOf(fav1))
    }

    @Test
    fun getStoredAlertsDB_listNewAlerts_getListNewAlerts() = main.runBlockingTest {
        //Given
        val alert1 = AlertsDB(
            id = 1,
            countryName = "egypt",
            startDateTime = "2/4/2023",
            endDateTime = "4/4/2023",
            type = "alert"
        )
        val alert2 = AlertsDB(
            id = 2,
            countryName = "korea",
            startDateTime = "2/4/2023",
            endDateTime = "4/4/2023",
            type = "notification"
        )
        repository.insertAlertsDB(alert1)
        repository.insertAlertsDB(alert2)
        var expected = listOf(alert1, alert2)

        //When
        var result: List<AlertsDB>? = null

        repository.getAllStoredAlertsDB().collect {
            result = it
        }


        //Then
        assertEquals(expected, result)
    }


    @Test
    fun deleteAlertsDB_newAlert_newAlertDeleted() = main.runBlockingTest {
        //Given new alert added
        val alert = AlertsDB(
            id = 0,
            countryName = "egypt",
            startDateTime = "2/4/2023",
            endDateTime = "4/4/2023",
            type = "alert"
        )
        repository.insertAlertsDB(alert)

        //When
        repository.deleteAlertsDB(alert)

        //Then
        var result: List<AlertsDB>? = null

        repository.getAllStoredAlertsDB().collect {
            result = it
        }

        assertEquals(-1, result!!.indexOf(alert))
    }

    @Test
    fun insertAlertsDB_newAlert_returnNewAlert() = main.runBlockingTest {
        //Given
        val alert = AlertsDB(
            id = 0,
            countryName = "egypt",
            startDateTime = "2/4/2023",
            endDateTime = "4/4/2023",
            type = "alert"
        )
        //When
        repository.insertAlertsDB(alert)
        //Then
        var result: List<AlertsDB>? = null

            repository.getAllStoredAlertsDB().collect {
                result = it
            }

        assertEquals(result!![result!!.size - 1].countryName, alert.countryName)
    }
}