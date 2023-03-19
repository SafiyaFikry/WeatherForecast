package eg.gov.iti.jets.weatherapp.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    init {
        getLocalProducts()
    }
    /*fun addProduct(root: Root){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertProduct(product)
            getLocalProducts()
        }
    }*/
    fun getLocalProducts(/*lat:Double,lon:Double*/){
        viewModelScope.launch {
            repo.getWeather(/*lat,lon*/)
                .catch {
                        e->_root.value=ApiState.Failure(e)
                }
                .collect{
                        data-> _root.value=ApiState.Success(data)
                }
        }
    }

}