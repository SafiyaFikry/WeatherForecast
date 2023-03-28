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
    @ColumnInfo
    val id :Int,
    @ColumnInfo
    val current: Current,
    @ColumnInfo
    val daily: List<Daily>,
    @ColumnInfo
    val hourly: List<Hourly>,
    @ColumnInfo
    val lat: Double,
    @ColumnInfo
    val lon: Double,
    @ColumnInfo
    val timezone: String,
    @ColumnInfo
    val timezone_offset: Int
)