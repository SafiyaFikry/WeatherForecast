package eg.gov.iti.jets.weatherapp.homeScreen.viewModel

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import eg.gov.iti.jets.weatherapp.MainActivity
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface
import eg.gov.iti.jets.weatherapp.model.Root
import eg.gov.iti.jets.weatherapp.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ViewModelHome (private val repo: RepositoryInterface,val lat:Double,val lon:Double,val lang:String): ViewModel() {
    private  var _root = MutableStateFlow<ApiState>(ApiState.Loading)
    val root = _root.asStateFlow()
    init {
        getLocalProducts(lat,lon,lang)
    }
    /*fun addProduct(root: Root){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertProduct(product)
            getLocalProducts()
        }
    }*/
    fun getLocalProducts(lat:Double,lon:Double,lang:String){
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