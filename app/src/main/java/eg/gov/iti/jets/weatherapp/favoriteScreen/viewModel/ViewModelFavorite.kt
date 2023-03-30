package eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface
import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelFavorite(private val repo: RepositoryInterface): ViewModel() {
    private  var _favoritesDB: MutableLiveData<List<FavoritesDB>> = MutableLiveData<List<FavoritesDB>>()
    val favoritesDB : LiveData<List<FavoritesDB>> = _favoritesDB
    companion object {
        var destination: String = "home"
    }
    fun setDes(des:String){
        destination=des
    }
    fun getDes():String{
        return destination
    }
    init {
        getLocalFav()
    }
    fun deleteFav(favoritesDB: FavoritesDB){
        viewModelScope.launch (Dispatchers.IO){
            repo.deleteFavoritesDB(favoritesDB)
            getLocalFav()
        }
    }
    fun addFav(favoritesDB: FavoritesDB){
        viewModelScope.launch (Dispatchers.IO){
            repo.insertFavoritesDB(favoritesDB)
            getLocalFav()
        }
    }
    fun getLocalFav(){
        viewModelScope.launch {
            repo.getAllStoredFavoritesDB().collect{
                _favoritesDB.postValue(it)
            }
        }
    }
}