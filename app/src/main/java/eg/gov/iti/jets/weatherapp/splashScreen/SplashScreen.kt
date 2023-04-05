package eg.gov.iti.jets.weatherapp.splashScreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import eg.gov.iti.jets.weatherapp.MainActivity
import eg.gov.iti.jets.weatherapp.databinding.ActivitySplashScreenBinding
import eg.gov.iti.jets.weatherapp.welcomeAndPermission.WelcomeAndPermission

lateinit var shared: SharedPreferences
class SplashScreen : AppCompatActivity() {

    lateinit var binding:ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shared = this.getSharedPreferences("eg.gov.iti.jets.weatherapp_preferences", Context.MODE_PRIVATE)
        if (shared.contains("temperature")==false) {
            val editor = shared.edit()
            editor.putString("temperature", "Celsius")
            editor.putString("location", "None")
            editor.putString("language", "English")
            editor.putString("windSpeed", "m/s")
            editor.putBoolean("notifications",false)
            editor.putString("lat", "33.44")
            editor.putString("lon", "-94.04")
            editor.commit()
        }
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (shared.getString("location", "GPS") == "None"){
                    val intent = Intent(this@SplashScreen, WelcomeAndPermission::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this@SplashScreen, MainActivity::class.java)
                    intent.putExtra("firstTime","false")
                    startActivity(intent)
                }
            }
        }, 4000)

    }

}
