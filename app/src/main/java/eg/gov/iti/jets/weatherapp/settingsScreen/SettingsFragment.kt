package eg.gov.iti.jets.weatherapp.settingsScreen

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import eg.gov.iti.jets.weatherapp.R

class SettingsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager?.beginTransaction()?.replace(R.id.fragment_container,MySettingsFragment())?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    class MySettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            //setPreferencesFromResource(R.xml.pref_settings, rootKey)
            addPreferencesFromResource(R.xml.pref_settings)
            val sh:SharedPreferences=PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
            val isChecked= sh.getBoolean("notifications",false)
            if(isChecked){
                Toast.makeText(context,"on",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"off",Toast.LENGTH_SHORT).show()
            }
            val languagePreference: ListPreference? = findPreference("language")
            languagePreference?.summaryProvider =
                Preference.SummaryProvider<ListPreference> { preference ->
                    val text = preference.value
                    if (text=="Arabic") {
                        "Arabic"
                    } else {
                        "English"
                    }
                }
            val locationPreference: ListPreference? = findPreference("location")
            locationPreference?.summaryProvider =
                Preference.SummaryProvider<ListPreference> { preference ->
                    val text = preference.value
                    if (text=="GPS") {
                        "GPS"
                    } else {
                        "Map"
                    }
                }
            val tempPreference: ListPreference? = findPreference("temperature")
            tempPreference?.summaryProvider =
                Preference.SummaryProvider<ListPreference> { preference ->
                    val text = preference.value
                    if (text=="Celsius") {
                        "Celsius"
                    }
                    else if (text=="Kelvin") {
                        "Kelvin"
                    } else {
                        "Fahrenheit"
                    }
                }
            val windPreference: ListPreference? = findPreference("windSpeed")
            windPreference?.summaryProvider =
                Preference.SummaryProvider<ListPreference> { preference ->
                    val text = preference.value
                    if (text=="m/s") {
                        "m/s"
                    } else {
                        "mph"
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}

