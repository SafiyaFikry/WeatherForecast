package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.location.Address
import android.location.Geocoder
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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

        runBlocking{
            Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(context))
                .getWeather(lat.toDouble(), lon.toDouble(), lang!!).collect { root ->
                if (root.alerts == null || root.alerts.isEmpty()) {
                    if (intent.extras?.getString("type") == "alert") {
                        createAlarm(context, "Everything is okay in " + des)
                        val notification = NotificationHelper(context)
                        var nb: NotificationCompat.Builder = notification.channelNotification
                        nb.setContentTitle("Alert!")
                        nb.setContentText("Everything is okay in " + des)
                        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        notification.manager?.notify(1, nb.build())
                    } else if (intent.extras?.getString("type") == "notification"){
                        val uri =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(context, uri)
                        r.play()
                        val notification = NotificationHelper(context)
                        var nb: NotificationCompat.Builder = notification.channelNotification
                        nb.setContentTitle("Notification!")
                        nb.setContentText("Everything is okay in " + des)
                        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        notification.manager?.notify(1, nb.build())
                    }
                } else {
                    if (intent.extras?.getString("type") == "alert") {
                        createAlarm(context, "There is ${root.alerts[0].event} in $des")
                        val notification = NotificationHelper(context)
                        var nb: NotificationCompat.Builder = notification.channelNotification
                        nb.setContentTitle("Alert!")
                        nb.setContentText("There is ${root.alerts[0].event} in $des")
                        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        notification.manager?.notify(1, nb.build())
                    } else {
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
    val LAYOUT_FLAG =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
    private fun createAlarm(context: Context, message: String) {
        val mediaPlayer = MediaPlayer.create(context,R.raw.music)
        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_dialog, null, false)
        val dismissBtn = view.findViewById<Button>(R.id.button_dismiss)
        val textView = view.findViewById<TextView>(R.id.textViewMessage)
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG ,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.CENTER
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.let {
            it.addView(view, layoutParams)
        }
        view.visibility = VISIBLE
        textView.text = message
        mediaPlayer.start()
        mediaPlayer.isLooping = true
        dismissBtn.setOnClickListener {
            mediaPlayer?.release()
            windowManager.removeView(view)
        }
    }
}