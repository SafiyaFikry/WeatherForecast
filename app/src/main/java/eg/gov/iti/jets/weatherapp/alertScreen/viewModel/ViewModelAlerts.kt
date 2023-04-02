package eg.gov.iti.jets.weatherapp.alertScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.weatherapp.model.AlertsDB
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelAlerts(private val repo: RepositoryInterface): ViewModel() {
    private  var _alertsDB: MutableLiveData<List<AlertsDB>> = MutableLiveData<List<AlertsDB>>()
    val alertsDB : LiveData<List<AlertsDB>> = _alertsDB
    companion object {
        var destination: String = "home"
      //  var dateTime: String = ""
    }
    fun setDes(des:String){
        destination=des
    }
    fun getDes():String{
        return destination
    }
//    fun setDateTime(dt:String){
//        dateTime=dt
//    }
//    fun getDateTime():String{
//        return dateTime
//    }
    init {
        getLocalAlerts()
    }
    fun deleteAlert(alertsDB: AlertsDB){
        viewModelScope.launch (Dispatchers.IO){
            repo.deleteAlertsDB(alertsDB)
            getLocalAlerts()
        }
    }
    fun insertAlert(alertsDB: AlertsDB){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertAlertsDB(alertsDB)
            getLocalAlerts()
        }
    }
    fun getLocalAlerts(){
        viewModelScope.launch {
            repo.getAllStoredAlertsDB().collect{
                _alertsDB.postValue(it)
            }
        }
    }
}