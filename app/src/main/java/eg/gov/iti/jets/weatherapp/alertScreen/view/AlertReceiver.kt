package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.media.AudioManager
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared
import kotlinx.coroutines.runBlocking


class AlertReceiver:BroadcastReceiver()  {
    lateinit var address:MutableList<Address>
    lateinit var geocoder: Geocoder
    lateinit var des:String
    override fun onReceive(context: Context, intent: Intent) {

        val lat= shared.getString("lat","33.44")
        val lon= shared.getString("lon","-94.04")
        val lang= shared.getString("language","English")
        geocoder= Geocoder(context)
        address = geocoder.getFromLocation(lat!!.toDouble(),lon!!.toDouble(), 10) as MutableList<Address>
        des = "${address[0].adminArea} - ${address[0].countryName}"
        runBlocking {
            Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(context)).getWeather(lat.toDouble(),lon.toDouble(),lang!!).collect{root->
                if(root.alerts==null||root.alerts.isEmpty()){
                    if (intent.extras?.getString("type")=="alert") {
                        val uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        val r = RingtoneManager.getRingtone(context, uri)
                        r.play()
                        val notification = NotificationHelper(context)
                        var nb: NotificationCompat.Builder = notification.channelNotification
                        nb.setContentTitle("Alert!")
                        nb.setContentText("Everything is okay in " + des)
                        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        notification.manager?.notify(1, nb.build())
                    }
                    else{
                        val uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(context, uri)
                        r.play()
                        val notification = NotificationHelper(context)
                        var nb: NotificationCompat.Builder = notification.channelNotification
                        nb.setContentTitle("Notification!")
                        nb.setContentText("Everything is okay in " + des)
                        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        notification.manager?.notify(1, nb.build())
                    }
                }
                else{
                    if (intent.extras?.getString("type")=="alert") {
                        val notification = NotificationHelper(context)
                        var nb: NotificationCompat.Builder = notification.channelNotification
                        nb.setContentTitle("Alert!")
                        nb.setContentText("There is ${root.alerts[0].event} in $des")
                        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        notification.manager?.notify(1, nb.build())
                    }else{
                        val notification = NotificationHelper(context)
                        var nb: NotificationCompat.Builder = notification.channelNotification
                        nb.setContentTitle("Notification!")
                        nb.setContentText("There is ${root.alerts[0].event} in $des")
                        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        notification.manager?.notify(1, nb.build())
                    }
                }
            }
        }
    }

}