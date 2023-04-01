package eg.gov.iti.jets.weatherapp.homeScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface
import eg.gov.iti.jets.weatherapp.model.Root
import eg.gov.iti.jets.weatherapp.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ViewModelHome (private val repo: RepositoryInterface): ViewModel() {
    private  var _root = MutableStateFlow<ApiState>(ApiState.Loading)
    val root = _root.asStateFlow()
    private  var _retrievedRoot: MutableLiveData<Root> = MutableLiveData<Root>()
    val retrievedRoot : LiveData<Root> = _retrievedRoot

    companion object{
        var lat:Double=0.0
        var lon:Double=0.0
        var city:String=""
    }
    init {
        getStoredWeather()
    }
    fun insertWeather(root: Root,lang:String){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertWeather(root)
            getWeatherDetails(root.lat,root.lon,lang)
        }
    }
    fun getStoredWeather(){
        viewModelScope.launch (Dispatchers.IO){
             repo.getAllStoredWeather().collect{
                 _retrievedRoot.postValue(it)
             }
        }
    }
    fun getWeatherDetails(lat:Double,lon:Double,lang:String){
        viewModelScope.launch {
            repo.getWeather(lat,lon,lang)
            .catch {
                    e->_root.value=ApiState.Failure(e)
            }
            .collect{
                    data-> _root.value=ApiState.Success(data)
            }
        }
    }


}