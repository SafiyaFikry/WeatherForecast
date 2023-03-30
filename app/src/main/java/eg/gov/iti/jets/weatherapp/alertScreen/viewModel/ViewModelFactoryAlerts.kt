package eg.gov.iti.jets.weatherapp.alertScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFavorite
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface

class ViewModelFactoryAlerts(private val repo: RepositoryInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ViewModelAlerts::class.java)){
            ViewModelAlerts(repo) as T
        }else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}