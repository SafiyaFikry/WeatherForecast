package eg.gov.iti.jets.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import eg.gov.iti.jets.weatherapp.alertScreen.AlertFragment
import eg.gov.iti.jets.weatherapp.databinding.ActivityMainBinding
import eg.gov.iti.jets.weatherapp.favoriteScreen.FavoriteFragment
import eg.gov.iti.jets.weatherapp.homeScreen.view.HomeFragment
import eg.gov.iti.jets.weatherapp.settingsScreen.SettingsFragment



const val PERMISSION_ID=44
class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var sh:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.navView.setNavigationItemSelectedListener(this)
        val toggle= ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open_nav,R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState==null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeFragment()
            ).commit()
            binding.navView.setCheckedItem(R.id.nav_home)
        }
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        sh=PreferenceManager.getDefaultSharedPreferences(this)
        if (sh.getString("location","Map")=="GPS") {
            getLastLocation()
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
            PERMISSION_ID)
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
                Toast.makeText(this,"Turn on location", Toast.LENGTH_LONG).show()
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
            val editor=sh.edit()
            editor.putString("lat",mLastLocation.latitude.toString())
            editor.putString("lon",mLastLocation.longitude.toString())
            editor.commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
                binding.toolbar.title="Home"
            }
            R.id.nav_favorites->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FavoriteFragment()).commit()
                binding.toolbar.title="Favorites"
            }
            R.id.nav_alert->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,AlertFragment()).commit()
                binding.toolbar.title="Alerts"
            }
            R.id.nav_settings->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,SettingsFragment()).commit()
                binding.toolbar.title="Settings"
            }
            else->{}
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add, menu)
        val shareItem: MenuItem = menu!!.findItem(R.id.add)
        val sh: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val location= sh.getString("location","Map")
        if (location=="Map") {
            shareItem.isVisible = true
        }
        else{
            shareItem.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.add) {
            //do something
            return true
        }
        return   super.onOptionsItemSelected(item)
    }

}