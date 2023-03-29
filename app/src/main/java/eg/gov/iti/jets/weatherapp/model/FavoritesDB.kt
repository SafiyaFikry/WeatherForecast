package eg.gov.iti.jets.weatherapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("favoritesDB")
class FavoritesDB(@PrimaryKey @ColumnInfo val place:String,@ColumnInfo val lat:Double,@ColumnInfo val lon:Double)