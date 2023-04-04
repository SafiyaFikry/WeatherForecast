package eg.gov.iti.jets.weatherapp.welcomeAndPermission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eg.gov.iti.jets.weatherapp.MainActivity
import eg.gov.iti.jets.weatherapp.databinding.ActivityWelcomeAndPermissionBinding
import eg.gov.iti.jets.weatherapp.splashScreen.shared

class WelcomeAndPermission : AppCompatActivity() {
    lateinit var binding:ActivityWelcomeAndPermissionBinding
    val PERMISSION_ID=55
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWelcomeAndPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
         mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        binding.enablePermissionBtn.setOnClickListener {
            getLastLocation()
            binding.enablePermissionBtn.isEnabled=false
            binding.enablePermissionBtn.setBackgroundColor(Color.GRAY)
        }
        binding.letsGoBtn.setOnClickListener {
            if(checkPermissions()) {
                showRadioConfirmationDialog()
                requestNewLocationData()
            }else{
                Toast.makeText(this@WelcomeAndPermission,"Enable permission first!!",Toast.LENGTH_LONG).show()
            }
        }
    }
    fun showRadioConfirmationDialog() {
        var selectedOptionIndex= 0
        val option = arrayOf("GPS", "Map")
        var selectedOption = option[selectedOptionIndex]
        val editor=shared.edit()
        MaterialAlertDialogBuilder(this)
            .setTitle("Get Location by ?")
            .setSingleChoiceItems(option, selectedOptionIndex) { dialog_, which ->
                selectedOptionIndex = which
                selectedOption = option[which]
            }
            .setPositiveButton("Ok") { dialog, which ->
                if(selectedOption=="GPS"){
                    editor.putString("location","GPS")
                    editor.commit()
                    val intent=Intent(this@WelcomeAndPermission, MainActivity::class.java)
                    intent.putExtra("firstTime","true")
                    startActivity(intent)
                }
                else{
                    editor.putString("location","Map")
                    editor.commit()
                    val intent=Intent(this@WelcomeAndPermission, MainActivity::class.java)
                    intent.putExtra("firstTime","true")
                    startActivity(intent)
                }
                Toast.makeText(this, "$selectedOption Selected", Toast.LENGTH_SHORT)
                .show()
            }
            .create().show()
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
    private fun requestNewLocationData() {
        val mLocationRequest= LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(0)
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLoactionCallback, Looper.myLooper())

    }
    private  val mLoactionCallback: LocationCallback =object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location =locationResult.lastLocation
            val editor= shared.edit()
            editor.putString("lat",mLastLocation.latitude.toString())
            editor.putString("lon",mLastLocation.longitude.toString())
            editor.commit()
        }
    }
}