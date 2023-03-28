package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentHomeBinding
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelFactoryHome
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelHome
import eg.gov.iti.jets.weatherapp.mFusedLocationClient
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.ApiState
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*
import kotlin.math.roundToInt
class HomeFragment : Fragment() {

    lateinit var viewModelHome: ViewModelHome
    lateinit var viewModelFactoryHome:ViewModelFactoryHome
    lateinit var dailyForecastAdapter:DailyForecastAdapter
    lateinit var hourlyForecastAdapter:HourlyForecastAdapter
    lateinit var binding:FragmentHomeBinding
    lateinit var address:MutableList<Address>
    lateinit var geocoder: Geocoder
    lateinit var des:String
   // lateinit var shared:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addAnotherLocationBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.mapFragment)
        }
        geocoder= Geocoder(requireContext().applicationContext)
       // shared= PreferenceManager.getDefaultSharedPreferences(requireContext())
        val location= shared.getString("location","GPS")
        val language= shared.getString("language","English")
        val temperature= shared.getString("temperature","Celsius")
        val windSpeed= shared.getString("windSpeed","m/s")

        val lat=shared.getString("lat","33.44")
        val lon=shared.getString("lon","-94.04")

        val lang=if (language=="English"){
            "en"
        }else{
            "ar"
        }
        if(checkForInternet(requireContext().applicationContext)) {
            viewModelFactoryHome = ViewModelFactoryHome(Repository.getInstance(WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext().applicationContext)
            ))
            viewModelHome = ViewModelProvider(this, viewModelFactoryHome).get(ViewModelHome::class.java)
            viewModelHome.getWeatherDetails(lat!!.toDouble(),lon!!.toDouble(),lang)

            lifecycleScope.launch {
                viewModelHome.root.collectLatest { root ->
                    when (root) {
                        is ApiState.Loading -> {
                            setLoadingStatus()
                        }
                        is ApiState.Success -> {
                            viewModelHome.addWeather(root.data,lang)
                            setSuccessStatus()
                            if(location=="Map"){
                                binding.addAnotherLocationBtn.visibility=View.VISIBLE
                            }
                            else{
                                binding.addAnotherLocationBtn.visibility=View.GONE
                            }
                            hourlyForecastAdapter = HourlyForecastAdapter(root.data.hourly, root.data, requireContext().applicationContext)
                            binding.hourlyRecycleview.adapter = hourlyForecastAdapter
                            hourlyForecastAdapter.notifyDataSetChanged()

                            dailyForecastAdapter = DailyForecastAdapter(root.data.daily, root.data, requireContext().applicationContext)
                            binding.dailyRecycleview.adapter = dailyForecastAdapter
                            dailyForecastAdapter.notifyDataSetChanged()

                            Glide.with(requireActivity().applicationContext)
                                .load("https://openweathermap.org/img/wn/" + root.data.current.weather[0].icon + "@2x.png")
                                .apply(
                                    RequestOptions()
                                        .override(150, 150)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_foreground)
                                )
                                .into(binding.placeWeatherImageView)

                            val long = (root.data.current.dt + root.data.timezone_offset - 7200).toLong() * 1000
                            val date = Date(long)
                            val format = SimpleDateFormat("EEE, dd MMM")
                            binding.dateTimeTextView.text = format.format(date)

                            address = geocoder.getFromLocation(root.data.lat, root.data.lon, 10) as MutableList<Address>
                            des = "${address[0].subAdminArea}\n${address[0].adminArea}\n${address[0].countryName}"
                            binding.placeTextView.text = des

                            if (temperature == "Celsius") {
                                binding.weatherTempTextView.text =
                                    root.data.current.temp.roundToInt().toString() + " °C"
                            } else if (temperature == "Fahrenheit") {
                                binding.weatherTempTextView.text =
                                    convertFromCelsiusToFahrenheit(root.data.current.temp).roundToInt()
                                        .toString() + " °F"
                            } else {
                                binding.weatherTempTextView.text =
                                    convertFromCelsiusToKelvin(root.data.current.temp).roundToInt()
                                        .toString() + " °K"
                            }

                            binding.weatherStatusTextView.text =
                                root.data.current.weather[0].description
                            binding.pressureTextView.text =
                                root.data.current.pressure.toString() + " hpa"
                            binding.humidityTextView.text =
                                root.data.current.humidity.toString() + " %"
                            if (windSpeed == "m/s") {
                                binding.windTextView.text =
                                    root.data.current.wind_speed.roundToInt().toString() + " m/s"
                            } else {
                                binding.windTextView.text =
                                    convertFromMPSToMPH(root.data.current.wind_speed).roundToInt()
                                        .toString() + " mph"
                            }

                            binding.cloudTextView.text = root.data.current.clouds.toString() + " %"
                            binding.ultravioletTextView.text = root.data.current.uvi.toString()
                            binding.visibilityTextView.text = root.data.current.visibility.toString() + " m"

                        }
                        else -> {
                           setFailureStatus()
                        }
                    }
                }
            }
        }
        else{
            Toast.makeText(requireContext().applicationContext,"Turn on WIFI", Toast.LENGTH_SHORT).show()
            val intent= Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
    }
    fun convertFromCelsiusToFahrenheit(cel:Double):Double=((cel * (9.0/5)) + 32)
    fun convertFromCelsiusToKelvin(cel:Double):Double=(cel + 273.15)
    fun convertFromMPSToMPH(mps:Double):Double=(mps * 2.237)

    fun setLoadingStatus(){
        binding.progressBar.visibility = View.VISIBLE
        binding.cardView.visibility = View.INVISIBLE
        binding.dailyRecycleview.visibility = View.INVISIBLE
        binding.hourlyRecycleview.visibility = View.INVISIBLE
        binding.textView19.visibility = View.INVISIBLE
        binding.textView20.visibility = View.INVISIBLE
        binding.addAnotherLocationBtn.visibility = View.INVISIBLE
    }
    fun setSuccessStatus(){
        binding.progressBar.visibility = View.GONE
        binding.cardView.visibility = View.VISIBLE
        binding.dailyRecycleview.visibility = View.VISIBLE
        binding.hourlyRecycleview.visibility = View.VISIBLE
        binding.textView19.visibility = View.VISIBLE
        binding.textView20.visibility = View.VISIBLE
    }
    fun setFailureStatus(){
        binding.progressBar.visibility = View.GONE
        binding.cardView.visibility = View.INVISIBLE
        binding.dailyRecycleview.visibility = View.INVISIBLE
        binding.hourlyRecycleview.visibility = View.INVISIBLE
        binding.textView19.visibility = View.INVISIBLE
        binding.textView20.visibility = View.INVISIBLE
        binding.addAnotherLocationBtn.visibility = View.INVISIBLE
        Toast.makeText(
            requireContext().applicationContext,
            "Check your connection",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}