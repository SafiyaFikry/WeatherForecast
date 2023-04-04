package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.*
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentHomeBinding
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelFactoryHome
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelHome
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.model.Root
import eg.gov.iti.jets.weatherapp.network.ApiState
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addAnotherLocationBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.mapFragment)
        }
        geocoder= Geocoder(requireContext().applicationContext)

        val location= shared.getString("location","GPS")
        val language= shared.getString("language","English")
        val temperature= shared.getString("temperature","Celsius")
        val windSpeed= shared.getString("windSpeed","m/s")

        viewModelFactoryHome = ViewModelFactoryHome(Repository.getInstance(WeatherClient.getInstance(),
            ConcreteLocalSource(requireContext().applicationContext)
        ))
        viewModelHome = ViewModelProvider(this, viewModelFactoryHome).get(ViewModelHome::class.java)

        val lat=shared.getString("lat","33.44")
        val lon=shared.getString("lon","-94.04")
        println("++++++++++++ lat : "+lat)
        println("++++++++++++ lon : "+lon)
        val lang=if (language=="English"){
           // setLanguage(requireContext(),"en")
            "en"

        }else{
           // setLanguage(requireContext(),"ar")
            "ar"
        }

        if(checkForInternet(requireContext().applicationContext)) {
            println("############ lat : "+lat)
            println("############ lon : "+lon)
            viewModelHome.getWeatherDetails(lat!!.toDouble(),lon!!.toDouble(),lang)
            lifecycleScope.launch {
                viewModelHome.root.collectLatest { root ->
                    when (root) {
                        is ApiState.Loading -> {
                            setLoadingStatus()
                        }
                        is ApiState.Success -> {
                            if(root.data.alerts==null){
                                root.data.alerts= emptyList()
                            }
                            viewModelHome.insertWeather(root.data,lang)
                            setSuccessStatus(location,temperature,windSpeed,root.data)
                        }
                        else -> {
                           setFailureStatus()
                        }
                    }
                }
            }
        }
        else{
            viewModelHome.retrievedRoot.observe(viewLifecycleOwner){ root->
                if(root!=null){
                    setSuccessStatus(location,temperature,windSpeed,root)
                }
            }
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
    fun setSuccessStatus(location:String?,temperature:String?,windSpeed:String?,root: Root){
        binding.progressBar.visibility = View.GONE
        binding.cardView.visibility = View.VISIBLE
        binding.dailyRecycleview.visibility = View.VISIBLE
        binding.hourlyRecycleview.visibility = View.VISIBLE
        binding.textView19.visibility = View.VISIBLE
        binding.textView20.visibility = View.VISIBLE

        if(location=="Map"){
            binding.addAnotherLocationBtn.visibility=View.VISIBLE
        }
        else{
            binding.addAnotherLocationBtn.visibility=View.GONE
        }
        hourlyForecastAdapter = HourlyForecastAdapter(root.hourly, root, requireContext().applicationContext)
        binding.hourlyRecycleview.adapter = hourlyForecastAdapter
        hourlyForecastAdapter.notifyDataSetChanged()

        dailyForecastAdapter = DailyForecastAdapter(root.daily, root, requireContext().applicationContext)
        binding.dailyRecycleview.adapter = dailyForecastAdapter
        dailyForecastAdapter.notifyDataSetChanged()

        Glide.with(requireActivity().applicationContext)
            .load("https://openweathermap.org/img/wn/" + root.current.weather[0].icon + "@2x.png")
            .apply(
                RequestOptions()
                    .override(150, 150)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
            )
            .into(binding.placeWeatherImageView)

        val long = (root.current.dt + root.timezone_offset - 7200).toLong() * 1000
        val date = Date(long)
        val format = SimpleDateFormat("EEE, dd MMM")
        binding.dateTimeTextView.text = format.format(date)

        address = geocoder.getFromLocation(root.lat, root.lon, 10) as MutableList<Address>
        des = "${address[0].subAdminArea}\n${address[0].adminArea}\n${address[0].countryName}"
        binding.placeTextView.text = des

        if (temperature == "Celsius") {
            binding.weatherTempTextView.text =
                root.current.temp.roundToInt().toString() + " °C"
        } else if (temperature == "Fahrenheit") {
            binding.weatherTempTextView.text =
                convertFromCelsiusToFahrenheit(root.current.temp).roundToInt()
                    .toString() + " °F"
        } else {
            binding.weatherTempTextView.text =
                convertFromCelsiusToKelvin(root.current.temp).roundToInt()
                    .toString() + " °K"
        }

        binding.weatherStatusTextView.text =
            root.current.weather[0].description
        binding.pressureTextView.text =
            root.current.pressure.toString() + " hpa"
        binding.humidityTextView.text =
            root.current.humidity.toString() + " %"
        if (windSpeed == "m/s") {
            binding.windTextView.text =
                root.current.wind_speed.roundToInt().toString() + " m/s"
        } else {
            binding.windTextView.text =
                convertFromMPSToMPH(root.current.wind_speed).roundToInt()
                    .toString() + " mph"
        }

        binding.cloudTextView.text = root.current.clouds.toString() + " %"
        binding.ultravioletTextView.text = root.current.uvi.toString()
        binding.visibilityTextView.text = root.current.visibility.toString() + " m"

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