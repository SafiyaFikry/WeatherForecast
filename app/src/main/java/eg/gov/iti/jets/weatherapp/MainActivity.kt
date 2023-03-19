package eg.gov.iti.jets.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import eg.gov.iti.jets.weatherapp.alertScreen.AlertFragment
import eg.gov.iti.jets.weatherapp.databinding.ActivityMainBinding
import eg.gov.iti.jets.weatherapp.favoriteScreen.FavoriteFragment
import eg.gov.iti.jets.weatherapp.homeScreen.view.HomeFragment
import eg.gov.iti.jets.weatherapp.settingsScreen.SettingsFragment

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
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

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home->supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeFragment()
            ).commit()
            R.id.nav_favorites->supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FavoriteFragment()).commit()
            R.id.nav_alert->supportFragmentManager.beginTransaction().replace(R.id.fragment_container,AlertFragment()).commit()
            R.id.nav_settings->supportFragmentManager.beginTransaction().replace(R.id.fragment_container,SettingsFragment()).commit()
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
        if (true) {
            shareItem.isVisible = true
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