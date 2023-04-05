package eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import eg.gov.iti.jets.weatherapp.MainRule
import eg.gov.iti.jets.weatherapp.model.FakeRepository
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewModelFavoriteTest{
    @ExperimentalCoroutinesApi
    @get:Rule
    val main = MainRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: ViewModelFavorite
    lateinit var repo: FakeRepository

    @Before
    fun setup(){
        repo= FakeRepository()
        viewModel= ViewModelFavorite(repo)
    }
    @Test
    fun getLocalFav_listNewFavorite_getListNewFavorite(){
        //Given
        val fav1 = FavoritesDB("egypt",23.2,55.1)
        val fav2 = FavoritesDB("france",33.2,86.2)
        val fav3 = FavoritesDB("korea", 96.2,12.2)
        viewModel.addFav(fav1)
        viewModel.addFav(fav2)
        viewModel.addFav(fav3)
        var expected= listOf(fav1,fav2,fav3)

        //When
        viewModel.getLocalFav()
        val result=viewModel.favoritesDB.getOrAwaitValue{}

        //Then
        assertEquals(expected, result)
    }

    @Test
    fun addFav_newFavorite_returnNewFavorite(){
        //Given
        val fav1 = FavoritesDB("egypt",23.2,55.1)
        //When
        viewModel.addFav(fav1)
        //Then
        val result=viewModel.favoritesDB.getOrAwaitValue{}
        assertEquals(result[result.size-1].place,fav1.place)
    }

    @Test
    fun deleteFavorite_newFavorite_newFavoriteDeleted(){
        //Given new alert added
        val fav1 = FavoritesDB("egypt",23.2,55.1)
        viewModel.addFav(fav1)

        //When
        viewModel.deleteFav(fav1)

        //Then
        val result=viewModel.favoritesDB.getOrAwaitValue{}
        assertEquals(-1,result.indexOf(fav1))
    }
}