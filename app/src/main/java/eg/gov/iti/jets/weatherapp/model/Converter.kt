package eg.gov.iti.jets.weatherapp.model

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converter {
    @TypeConverter
    fun fromCurrentToString(current: Current):String{
        return Gson().toJson(current)
    }
    @TypeConverter
    fun fromStringToCurrent(string: String):Current{
        return Gson().fromJson(string,Current::class.java)
    }
    @TypeConverter
    fun fromListDailyToString(list: List<Daily>):String{
        return Gson().toJson(list)
    }
    @TypeConverter
    fun fromStringToListDaily(string: String):List<Daily>{
        return Gson().fromJson(string,Array<Daily>::class.java).toList()
    }
    @TypeConverter
    fun fromListHourlyToString(list: List<Hourly>):String{
        return Gson().toJson(list)
    }
    @TypeConverter
    fun fromStringToListHourly(string: String):List<Hourly>{
        return Gson().fromJson(string,Array<Hourly>::class.java).toList()
    }
    @TypeConverter
    fun fromListWeatherToString(list: List<Weather>):String{
        return Gson().toJson(list)
    }
    @TypeConverter
    fun fromStringToListWeather(string: String):List<Weather>{
        return Gson().fromJson(string,Array<Weather>::class.java).toList()
    }
}