package eg.gov.iti.jets.weatherapp.model

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.annotation.Excludes
import com.google.gson.annotations.Expose

@Entity(tableName = "root")
data class Root(
    @PrimaryKey
    var id :Int,
    var current: Current,
    var daily: List<Daily>,
    var hourly: List<Hourly>,
    var lat: Double,
    var lon: Double,
    var timezone: String,
    var timezone_offset: Int,
    var alerts:List<Alerts>
)