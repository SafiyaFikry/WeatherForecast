package eg.gov.iti.jets.weatherapp.splashScreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eg.gov.iti.jets.weatherapp.MainActivity
import eg.gov.iti.jets.weatherapp.databinding.ActivitySplashScreenBinding
import eg.gov.iti.jets.weatherapp.homeScreen.view.HomeFragment
import eg.gov.iti.jets.weatherapp.homeScreen.view.MapFragment


class SplashScreen : AppCompatActivity() {
    lateinit var binding:ActivitySplashScreenBinding
    /*lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var sh: SharedPreferences
    lateinit var editor:SharedPreferences.Editor*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
       /* mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        sh= PreferenceManager.getDefaultSharedPreferences(this)
*/
        Handler().postDelayed(object : Runnable {
            override fun run() {
                val intent=Intent(this@SplashScreen,MainActivity::class.java)
                startActivity(intent)

            }
        }, 4000)

    }

}
