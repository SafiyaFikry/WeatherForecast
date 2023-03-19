package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.location.Address
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.FragmentHomeBinding
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelFactoryHome
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelHome
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.ApiState
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*

class HomeFragment : Fragment() {

    lateinit var viewModelHome: ViewModelHome
    lateinit var viewModelFactoryHome:ViewModelFactoryHome
    lateinit var dailyForecastAdapter:DailyForecastAdapter
    lateinit var hourlyForecastAdapter:HourlyForecastAdapter
    lateinit var binding:FragmentHomeBinding
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
        viewModelFactoryHome= ViewModelFactoryHome(Repository.getInstance(WeatherClient.getInstance()))
        viewModelHome=ViewModelProvider(this,viewModelFactoryHome).get(ViewModelHome::class.java)
        lifecycleScope.launch {
            viewModelHome.root.collectLatest { root ->
                when(root){
                    is ApiState.Loading->{
                        binding.progressBar.visibility= View.VISIBLE
                        binding.cardView.visibility=View.GONE
                        binding.dailyRecycleview.visibility=View.GONE
                        binding.hourlyRecycleview.visibility=View.GONE
                        binding.textView19.visibility=View.GONE
                        binding.textView20.visibility=View.GONE

                    }
                    is ApiState.Success->{
                        binding.progressBar.visibility= View.GONE
                        binding.cardView.visibility=View.VISIBLE
                        binding.dailyRecycleview.visibility=View.VISIBLE
                        binding.hourlyRecycleview.visibility=View.VISIBLE
                        binding.textView19.visibility=View.VISIBLE
                        binding.textView20.visibility=View.VISIBLE
                        hourlyForecastAdapter = HourlyForecastAdapter(root.data.hourly, root.data, requireContext().applicationContext)
                        binding.hourlyRecycleview.adapter =hourlyForecastAdapter
                        hourlyForecastAdapter.notifyDataSetChanged()

                        dailyForecastAdapter = DailyForecastAdapter(root.data.daily,root.data, requireContext().applicationContext)
                        binding.dailyRecycleview.adapter =dailyForecastAdapter
                        dailyForecastAdapter.notifyDataSetChanged()

                        Glide.with(requireActivity().applicationContext)
                            .load("https://openweathermap.org/img/wn/"+root.data.current.weather[0].icon+"@2x.png")
                            .apply(
                                RequestOptions()
                                    .override(150, 150)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_foreground)
                            )
                            .into(binding.placeWeatherImageView)
                        val long =(root.data.current.dt+root.data.timezone_offset-7200).toLong()*1000
                        val date = Date(long)
                        val format = SimpleDateFormat("EEE, dd MMM")
                        binding.dateTimeTextView.text=format.format(date)
                        binding.placeTextView.text=root.data.timezone
                        binding.weatherTempTextView.text=root.data.current.temp.toInt().toString()+" °C"
                        binding.weatherStatusTextView.text=root.data.current.weather[0].description
                        binding.pressureTextView.text=root.data.current.pressure.toString()+" hpa"
                        binding.humidityTextView.text=root.data.current.humidity.toString()+" %"
                        binding.windTextView.text=root.data.current.wind_speed.toString()+" m/s"
                        binding.cloudTextView.text=root.data.current.clouds.toString()+" %"
                        binding.ultravioletTextView.text=root.data.current.uvi.toString()
                        binding.visibilityTextView.text=root.data.current.visibility.toString()+" m"

                    }
                    else->{
                        binding.progressBar.visibility= View.GONE
                        Toast.makeText(requireContext().applicationContext,"Check your connection", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
    fun convertFromCelsiusToFahrenheit(cel:Double):Double=((cel * (9.0/5)) + 32)
    fun convertFromCelsiusToKelvin(cel:Double):Double=(cel + 273.15)

}