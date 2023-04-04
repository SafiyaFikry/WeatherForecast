package eg.gov.iti.jets.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import eg.gov.iti.jets.weatherapp.databinding.ActivityMainBinding
import eg.gov.iti.jets.weatherapp.splashScreen.shared
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.homeScreen.view.MapFragment
import java.util.*

const val PERMISSION_ID=55
lateinit var mFusedLocationClient: FusedLocationProviderClient
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var actionBar:ActionBar
    lateinit var navController:NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        drawerLayout=binding.drawerLayout
        navigationView=binding.navigationView

        actionBar=supportActionBar as ActionBar
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        navController=Navigation.findNavController(this,R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener{ controller, destination, arguments ->
            title = when (destination.id) {
                R.id.homeFragment ->{
                    if(shared.getString("location","GPS")=="GPS") {
                        println("here--------------------------------")
                        getLastLocation()
                    }
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    actionBar.setDisplayShowHomeEnabled(true)
                    getResources().getString(R.string.home);
                }
                R.id.alertFragment-> {
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    actionBar.setDisplayShowHomeEnabled(true)
                    getResources().getString(R.string.alert)
                };
                R.id.favoriteFragment-> {
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    actionBar.setDisplayShowHomeEnabled(true)
                    getResources().getString(R.string.favorite);
                }
                R.id.settingsFragment-> {
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    actionBar.setDisplayShowHomeEnabled(true)
                    getResources().getString(R.string.settings);
                }
                R.id.mapFragment-> {
                    actionBar.setDisplayHomeAsUpEnabled(false)
                    actionBar.setDisplayShowHomeEnabled(false)
                    getResources().getString(R.string.Map)
                }
                R.id.favDetailsFragment-> {
                    actionBar.setDisplayHomeAsUpEnabled(false)
                    actionBar.setDisplayShowHomeEnabled(false)
                    getResources().getString(R.string.favorite)
                }
                else->getResources().getString(R.string.app_name);
            }
        }
        NavigationUI.setupWithNavController(navigationView,navController)

        if(shared.getString("location","GPS")=="Map"&&intent.extras?.getString("firstTime")=="true") {
            navController.navigate(R.id.mapFragment)
        }
        else if(shared.getString("location","GPS")=="GPS") {
            getLastLocation()
            requestNewLocationData()
        }
    }


    override fun onResume() {
        super.onResume()
        if(shared.getString("location","GPS")=="GPS") {
            getLastLocation()
        }
    }

    override fun onStart() {
        super.onStart()
        if(shared.getString("location","GPS")=="GPS") {
            getLastLocation()
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer((GravityCompat.START))
            }else{
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return   super.onOptionsItemSelected(item)
    }
}



