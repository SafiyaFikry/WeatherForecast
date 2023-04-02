package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelHome
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.model.RepositoryInterface
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking

class AlertReceiver:BroadcastReceiver()  {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.extras?.getString("alertName")=="empty") {
            val notification = NotificationHelper(context)
            var nb: NotificationCompat.Builder = notification.channelNotification
            nb.setContentText("Everything is okay in "+intent.extras?.getString("city"))
            notification.manager?.notify(1, nb.build())
        }
        else{
            val notification = NotificationHelper(context)
            var nb: NotificationCompat.Builder = notification.channelNotification
            nb.setContentText(intent.extras?.getString("city")+": "+intent.extras?.getString("alertName"))
            notification.manager?.notify(1, nb.build())
        }
    }

}