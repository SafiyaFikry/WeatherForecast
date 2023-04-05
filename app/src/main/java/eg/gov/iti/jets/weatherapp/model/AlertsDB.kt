package eg.gov.iti.jets.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("alertsDB")
data class AlertsDB(@PrimaryKey(autoGenerate = true) var id:Int=0,var countryName:String,var startDateTime:String,var endDateTime:String,var type:String)
