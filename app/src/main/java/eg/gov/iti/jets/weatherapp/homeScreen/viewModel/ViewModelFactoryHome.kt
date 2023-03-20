package eg.gov.iti.jets.weatherapp.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface

class ViewModelFactoryHome(private val repo: RepositoryInterface ,val lat:Double,val lon:Double,val lang:String): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ViewModelHome::class.java)){
            ViewModelHome(repo,lat,lon,lang) as T
        }else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}