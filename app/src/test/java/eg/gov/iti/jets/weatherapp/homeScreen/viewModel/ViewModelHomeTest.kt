package eg.gov.iti.jets.weatherapp.homeScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import eg.gov.iti.jets.weatherapp.MainRule
import eg.gov.iti.jets.weatherapp.model.Current
import eg.gov.iti.jets.weatherapp.model.FakeRepository
import eg.gov.iti.jets.weatherapp.model.Root
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewModelHomeTest{
    @ExperimentalCoroutinesApi
    @get:Rule
    val main = MainRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: ViewModelHome
    lateinit var repo: FakeRepository

    @Before
    fun setup(){
        repo=FakeRepository()
        viewModel= ViewModelHome(repo)
    }
    @Test
    fun getStoredWeather_newRoot_getnewRoot(){
        val root = Root(id=1, lat = 33.44, lon = -94.04, timezone = "united states", timezone_offset = 1000,
            current = Current(dt = 1646318689, sunrise = 112233563, sunset = 65245632, temp = 235.0, feels_like = 322.1, pressure = 2154, humidity =67, dew_point = 22.0, clouds = 45, uvi = 2.5, visibility = 55, weather = emptyList(), wind_deg = 55, wind_speed = 5.5),
        daily = emptyList(), hourly = emptyList(), alerts = emptyList()
        )
        viewModel.insertWeather(root,"en")
        viewModel.getStoredWeather()
        val result=viewModel.retrievedRoot.getOrAwaitValue{}
        assertEquals(result,root)
    }
}