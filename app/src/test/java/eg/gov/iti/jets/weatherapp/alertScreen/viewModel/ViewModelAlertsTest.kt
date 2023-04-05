package eg.gov.iti.jets.weatherapp.alertScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import eg.gov.iti.jets.weatherapp.MainRule
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelHome
import eg.gov.iti.jets.weatherapp.model.AlertsDB
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
class ViewModelAlertsTest{
    @ExperimentalCoroutinesApi
    @get:Rule
    val main = MainRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: ViewModelAlerts
    lateinit var repo: FakeRepository

    @Before
    fun setup(){
        repo= FakeRepository()
        viewModel= ViewModelAlerts(repo)
    }
    @Test
    fun getLocalAlerts_listNewAlerts_getListNewAlerts(){
        //Given
        val alert1 = AlertsDB(countryName = "egypt", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "alert")
        val alert2 = AlertsDB(countryName = "france", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "alert")
        val alert3 = AlertsDB(countryName = "korea", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "notification")
        viewModel.insertAlert(alert1)
        viewModel.insertAlert(alert2)
        viewModel.insertAlert(alert3)
        var expected= listOf(alert1,alert2,alert3)

        //When
        viewModel.getLocalAlerts()
        val result=viewModel.alertsDB.getOrAwaitValue{}

        //Then
        assertEquals(expected, result)
    }

    @Test
    fun insertAlert_newAlert_returnNewAlert(){
        //Given
        val alert = AlertsDB(countryName = "egypt", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "alert")
        //When
        viewModel.insertAlert(alert)
        //Then
        val result=viewModel.alertsDB.getOrAwaitValue{}
        assertEquals(result[result.size-1].countryName,alert.countryName)
    }

    @Test
    fun deleteAlert_newAlert_newAlertDeleted(){
        //Given new alert added
        val alert = AlertsDB(countryName = "egypt", startDateTime = "2/4/2023", endDateTime = "4/4/2023", type = "alert")
        viewModel.insertAlert(alert)

        //When
        viewModel.deleteAlert(alert)

        //Then
        val result=viewModel.alertsDB.getOrAwaitValue{}
        assertEquals(-1,result.indexOf(alert))
    }
}