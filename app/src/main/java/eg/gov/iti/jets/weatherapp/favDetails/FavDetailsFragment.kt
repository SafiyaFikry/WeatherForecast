package eg.gov.iti.jets.weatherapp.favDetails
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentFavDetailsBinding
import eg.gov.iti.jets.weatherapp.databinding.FragmentHomeBinding
import eg.gov.iti.jets.weatherapp.homeScreen.view.DailyForecastAdapter
import eg.gov.iti.jets.weatherapp.homeScreen.view.HourlyForecastAdapter
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
class FavDetailsFragment : Fragment() {

    lateinit var viewModelHome: ViewModelHome
    lateinit var viewModelFactoryHome:ViewModelFactoryHome
    lateinit var dailyForecastAdapter: DailyForecastAdapter
    lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    lateinit var binding:FragmentFavDetailsBinding
    lateinit var address:MutableList<Address>
    lateinit var geocoder: Geocoder
    lateinit var des:String
    lateinit var favShared: SharedPreferences
    // lateinit var shared:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFavDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favShared=requireContext().getSharedPreferences("favShared",Context.MODE_PRIVATE)
        val editor=favShared.edit()
        binding.faddAnotherLocationBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.mapFragment)
        }
        geocoder= Geocoder(requireContext().applicationContext)
        // shared= PreferenceManager.getDefaultSharedPreferences(requireContext())
        val location= shared.getString("location","GPS")
        val language= shared.getString("language","English")
        val temperature= shared.getString("temperature","Celsius")
        val windSpeed= shared.getString("windSpeed","m/s")

        val lat=favShared.getString("lat","33.44")
        val lon=favShared.getString("lon","-94.04")

        val lang=if (language=="English"){
            "en"
        }else{
            "ar"
        }
        viewModelFactoryHome = ViewModelFactoryHome(Repository.getInstance(WeatherClient.getInstance(),
            ConcreteLocalSource(requireContext().applicationContext)
        ))
        viewModelHome = ViewModelProvider(this, viewModelFactoryHome).get(ViewModelHome::class.java)
        if(checkForInternet(requireContext().applicationContext)) {
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
            /* Toast.makeText(requireContext().applicationContext,"Turn on WIFI", Toast.LENGTH_SHORT).show()
             val intent= Intent(Settings.ACTION_WIFI_SETTINGS)
             startActivity(intent)*/
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
        binding.fprogressBar.visibility = View.VISIBLE
        binding.fcardView.visibility = View.INVISIBLE
        binding.fdailyRecycleview.visibility = View.INVISIBLE
        binding.fhourlyRecycleview.visibility = View.INVISIBLE
        binding.ftextView19.visibility = View.INVISIBLE
        binding.ftextView20.visibility = View.INVISIBLE
        binding.faddAnotherLocationBtn.visibility = View.INVISIBLE
    }
    fun setSuccessStatus(location:String?,temperature:String?,windSpeed:String?,root: Root){
        binding.fprogressBar.visibility = View.GONE
        binding.fcardView.visibility = View.VISIBLE
        binding.fdailyRecycleview.visibility = View.VISIBLE
        binding.fhourlyRecycleview.visibility = View.VISIBLE
        binding.ftextView19.visibility = View.VISIBLE
        binding.ftextView20.visibility = View.VISIBLE

        if(location=="Map"){
            binding.faddAnotherLocationBtn.visibility=View.VISIBLE
        }
        else{
            binding.faddAnotherLocationBtn.visibility=View.GONE
        }
        hourlyForecastAdapter = HourlyForecastAdapter(root.hourly, root, requireContext().applicationContext)
        binding.fhourlyRecycleview.adapter = hourlyForecastAdapter
        hourlyForecastAdapter.notifyDataSetChanged()

        dailyForecastAdapter = DailyForecastAdapter(root.daily, root, requireContext().applicationContext)
        binding.fdailyRecycleview.adapter = dailyForecastAdapter
        dailyForecastAdapter.notifyDataSetChanged()

        Glide.with(requireActivity().applicationContext)
            .load("https://openweathermap.org/img/wn/" + root.current.weather[0].icon + "@2x.png")
            .apply(
                RequestOptions()
                    .override(150, 150)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
            )
            .into(binding.fplaceWeatherImageView)

        val long = (root.current.dt + root.timezone_offset - 7200).toLong() * 1000
        val date = Date(long)
        val format = SimpleDateFormat("EEE, dd MMM")
        binding.fdateTimeTextView.text = format.format(date)

        address = geocoder.getFromLocation(root.lat, root.lon, 10) as MutableList<Address>
        des = "${address[0].subAdminArea}\n${address[0].adminArea}\n${address[0].countryName}"
        binding.fplaceTextView.text = des

        if (temperature == "Celsius") {
            binding.fweatherTempTextView.text =
                root.current.temp.roundToInt().toString() + " °C"
        } else if (temperature == "Fahrenheit") {
            binding.fweatherTempTextView.text =
                convertFromCelsiusToFahrenheit(root.current.temp).roundToInt()
                    .toString() + " °F"
        } else {
            binding.fweatherTempTextView.text =
                convertFromCelsiusToKelvin(root.current.temp).roundToInt()
                    .toString() + " °K"
        }

        binding.fweatherStatusTextView.text =
            root.current.weather[0].description
        binding.fpressureTextView.text =
            root.current.pressure.toString() + " hpa"
        binding.fhumidityTextView.text =
            root.current.humidity.toString() + " %"
        if (windSpeed == "m/s") {
            binding.fwindTextView.text =
                root.current.wind_speed.roundToInt().toString() + " m/s"
        } else {
            binding.fwindTextView.text =
                convertFromMPSToMPH(root.current.wind_speed).roundToInt()
                    .toString() + " mph"
        }

        binding.fcloudTextView.text = root.current.clouds.toString() + " %"
        binding.fultravioletTextView.text = root.current.uvi.toString()
        binding.fvisibilityTextView.text = root.current.visibility.toString() + " m"

    }
    fun setFailureStatus(){
        binding.fprogressBar.visibility = View.GONE
        binding.fcardView.visibility = View.INVISIBLE
        binding.fdailyRecycleview.visibility = View.INVISIBLE
        binding.fhourlyRecycleview.visibility = View.INVISIBLE
        binding.ftextView19.visibility = View.INVISIBLE
        binding.ftextView20.visibility = View.INVISIBLE
        binding.faddAnotherLocationBtn.visibility = View.INVISIBLE
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