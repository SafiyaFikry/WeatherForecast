package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlertReceiver:BroadcastReceiver()  {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notification:NotificationHelper= NotificationHelper(context)
        var nb :NotificationCompat.Builder=notification.channelNotification
        notification.manager?.notify(1,nb.build())
    }
}