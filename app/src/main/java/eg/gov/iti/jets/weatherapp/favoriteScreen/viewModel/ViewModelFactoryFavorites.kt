package eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface

class ViewModelFactoryFavorites(private val repo: RepositoryInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ViewModelFavorite::class.java)){
            ViewModelFavorite(repo) as T
        }else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}