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

const val PERMISSION_ID=44
class SplashScreen : AppCompatActivity() {
    lateinit var binding:ActivitySplashScreenBinding
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var sh: SharedPreferences
    lateinit var editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        sh= PreferenceManager.getDefaultSharedPreferences(this)

        Handler().postDelayed(object : Runnable {
            override fun run() {
                if(sh.getString("location","None")=="None"){
                    editor=sh.edit()
                    editor.putString("lat","33.44")
                    editor.commit()
                    editor.putString("lon","-94.04")
                    editor.commit()
                    showRadioConfirmationDialog()
                }
                else{
                    val intent=Intent(this@SplashScreen,MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }, 4000)

    }
    private fun checkPermissions():Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
        )== PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
                )== PackageManager.PERMISSION_GRANTED
    }
    private  fun requestPermissions(){
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }
    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)

    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                requestNewLocationData()
            }
            else{
                Toast.makeText(this,"Turn on location", Toast.LENGTH_SHORT).show()
                val intent= Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else{
            requestPermissions()
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest= LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(0)
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLoactionCallback, Looper.myLooper())

    }

    private  val mLoactionCallback: LocationCallback =object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location =locationResult.lastLocation
            editor.putString("lat",mLastLocation.latitude.toString())
            editor.commit()
            editor.putString("lon",mLastLocation.longitude.toString())
            editor.commit()
        }
    }
    fun showRadioConfirmationDialog() {
        var selectedOptionIndex= 0
        val option = arrayOf("GPS", "Map")
        var selectedOption = option[selectedOptionIndex]
        MaterialAlertDialogBuilder(this)
            .setTitle("Get Location by ?")
            .setSingleChoiceItems(option, selectedOptionIndex) { dialog_, which ->
                selectedOptionIndex = which
                selectedOption = option[which]
            }
            .setPositiveButton("Ok") { dialog, which ->
                Toast.makeText(this, "$selectedOption Selected", Toast.LENGTH_SHORT)
                    .show()
                if(selectedOption=="GPS"){
                    editor.putString("location","GPS")
                    editor.commit()
                    getLastLocation()
                    val intent=Intent(this@SplashScreen,MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    editor.putString("location","Map")
                    editor.commit()
                    val intent=Intent(this@SplashScreen,MainActivity::class.java)
                    startActivity(intent)
                }
            }
            .create().show()
    }

}
