package eg.gov.iti.jets.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import eg.gov.iti.jets.weatherapp.alertScreen.AlertFragment
import eg.gov.iti.jets.weatherapp.databinding.ActivityMainBinding
import eg.gov.iti.jets.weatherapp.favoriteScreen.FavoriteFragment
import eg.gov.iti.jets.weatherapp.homeScreen.view.HomeFragment
import eg.gov.iti.jets.weatherapp.homeScreen.view.MapFragment
import eg.gov.iti.jets.weatherapp.settingsScreen.SettingsFragment



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sh: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var actionBar:ActionBar
    lateinit var navController:NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout=binding.drawerLayout
        navigationView=binding.navigationView

        actionBar=supportActionBar as ActionBar
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        navController=Navigation.findNavController(this,R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener{controller, destination, arguments ->
            title = when (destination.id) {
                R.id.homeFragment -> "Home"
                R.id.alertFragment-> "Alerts"
                R.id.favoriteFragment-> "Favorites"
                R.id.settingsFragment->"Settings"
                else->"Weather Forecast"
            }
        }
        NavigationUI.setupWithNavController(navigationView,navController)
        sh= PreferenceManager.getDefaultSharedPreferences(this)

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