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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
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


const val PERMISSION_ID=44
class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var sh: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.navView.setNavigationItemSelectedListener(this)
        val toggle= ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open_nav,R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

//        if (savedInstanceState==null){
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
//                HomeFragment()
//            ).commit()
//            binding.navView.setCheckedItem(R.id.nav_home)
//        }

        sh= PreferenceManager.getDefaultSharedPreferences(this)

        if(sh.getString("location","None")=="GPS"){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

        }
        else{
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MapFragment()).commit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
                binding.toolbar.title="Home"
                /*if (sh.getString("location","None")=="GPS") {
                    getLastLocation()
                }
                else{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MapFragment()).commit()
                }*/
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




    //-------------------------------------------------------------------if we create button for set another location in map option in home fragment we gonna delete these two below functions
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add, menu)
        val shareItem: MenuItem = menu!!.findItem(R.id.add)
        val sh: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val location= sh.getString("location","Map")
        shareItem.isVisible = location=="Map"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.add) {
            //do something
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MapFragment()).commit()
            return true
        }
        return   super.onOptionsItemSelected(item)
    }



}