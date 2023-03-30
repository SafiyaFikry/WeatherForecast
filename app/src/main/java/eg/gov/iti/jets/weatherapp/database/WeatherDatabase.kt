package eg.gov.iti.jets.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eg.gov.iti.jets.weatherapp.model.AlertsDB
import eg.gov.iti.jets.weatherapp.model.Converter
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.Root

@Database(entities = arrayOf(Root::class,FavoritesDB::class, AlertsDB::class), version = 6)
@TypeConverters(Converter::class)
abstract class WeatherDatabase:RoomDatabase() {
    abstract fun getWeatherDao():WeatherDao
    companion object{
        @Volatile
        private var INSTANCE:WeatherDatabase? = null
        fun getInstance(context: Context):WeatherDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,WeatherDatabase::class.java,"root").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}