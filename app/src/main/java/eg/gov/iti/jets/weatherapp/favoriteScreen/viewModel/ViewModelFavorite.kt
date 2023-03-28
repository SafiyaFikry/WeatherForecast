package eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface
import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelFavorite(private val repo: RepositoryInterface): ViewModel() {
    private  var _weather: MutableLiveData<Root> = MutableLiveData<Root>()
    val weather : LiveData<Root> = _weather
    init {
        getLocalProducts()
    }
    fun deleteWeather(root:Root){
        viewModelScope.launch (Dispatchers.IO){
            repo.deleteWeather(root)
            getLocalProducts()
        }
    }
    fun getLocalProducts(){
        viewModelScope.launch {
            repo.getAllStoredWeather().collect{
                _weather.postValue(it)
            }
        }
    }
}