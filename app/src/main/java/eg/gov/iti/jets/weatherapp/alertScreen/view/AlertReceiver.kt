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
    lateinit var viewModel:ViewModelHome
    override fun onReceive(context: Context, intent: Intent) {
        val repo= Repository.getInstance(WeatherClient.getInstance(),
            ConcreteLocalSource(context))
        runBlocking {
            val result=repo.getWeather(
                intent.extras?.getString("lat")!!.toDouble(),
                intent.extras?.getString("lon")!!.toDouble(),
                "en"
            )
            result.collectLatest {
                if (it.alerts==null||it.alerts.isEmpty()) {
                    val notification = NotificationHelper(context)
                    var nb: NotificationCompat.Builder = notification.channelNotification
                    nb.setContentText("Everything is okay in "+intent.extras?.getString("city"))
                    notification.manager?.notify(1, nb.build())
                }
                else{
                    val notification = NotificationHelper(context)
                    var nb: NotificationCompat.Builder = notification.channelNotification
                    nb.setContentText(intent.extras?.getString("city")+": "+it.alerts.get(0).description)
                    notification.manager?.notify(1, nb.build())
                }
            }
        }
    }
}