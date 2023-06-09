package eg.gov.iti.jets.weatherapp.settingsScreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.FragmentSettingsBinding
import java.util.*

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
        @SuppressLint("SuspiciousIndentation")
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
            val langPreference=findPreference<ListPreference>("language")
            langPreference?.setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener{preference,newValue->
                val radio=newValue.toString()
                if (radio=="English"){
                    SettingsFragment().setLanguage(requireContext(),"en")
                    activity?.recreate()
                }
                else if(radio=="Arabic"){
                    SettingsFragment().setLanguage(requireContext(),"ar")
                    activity?.recreate()
                }
                true
            })
        }
    }
    fun setLanguage(context: Context, lang:String)
    {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resource = context.resources
        val config = resource.configuration
        config.setLocale(locale)
        resource.updateConfiguration(config, resource.displayMetrics)
    }
}

