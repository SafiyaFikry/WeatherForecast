package eg.gov.iti.jets.weatherapp.database

import androidx.room.*
import eg.gov.iti.jets.weatherapp.model.Root
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("Select * From root")
    fun getStoredWeather(): Flow<Root>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(root:Root):Long

    @Update
    suspend fun updateWeather(root: Root)

    @Delete
    suspend fun deleteWeather(root: Root):Int
}