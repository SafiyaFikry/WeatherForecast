package eg.gov.iti.jets.weatherapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("favoritesDB")
class FavoritesDB(@PrimaryKey  val place:String, val lat:Double, val lon:Double)