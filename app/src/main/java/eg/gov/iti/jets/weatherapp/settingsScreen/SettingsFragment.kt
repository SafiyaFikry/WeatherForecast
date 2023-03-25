package eg.gov.iti.jets.weatherapp.settingsScreen

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.navigation.Navigation
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.FragmentSettingsBinding
import eg.gov.iti.jets.weatherapp.homeScreen.view.MapFragment

class SettingsFragment : Fragment() {
    lateinit var binding:FragmentSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager?.beginTransaction()?.replace(R.id.nav_host_fragment,MySettingsFragment())?.commit()//nav_host_fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }
    class MySettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_settings, rootKey)
            val mapPreference=findPreference<ListPreference>("location")
                mapPreference?.setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener{preference,newValue->
                    val radio=newValue.toString()
                    if (radio=="Map"){
                        Navigation.findNavController(requireView()).navigate(R.id.mapFragment)
                    }
                    true
                })
        }
    }
}

