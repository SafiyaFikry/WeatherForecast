package eg.gov.iti.jets.weatherapp.model

class Alerts (
    val sender_name:String,
    val event:String,
    val start:Int,
    val end:Int,
    val description:String,
    val tags:ArrayList<String>
    )