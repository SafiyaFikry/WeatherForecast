package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.DailyForecastBinding
import eg.gov.iti.jets.weatherapp.model.Daily
import eg.gov.iti.jets.weatherapp.model.Root
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DailyForecastAdapter (private var daily:List<Daily>,var root:Root, context: Context) : RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>(){
    private var mContext: Context
    lateinit var binding: DailyForecastBinding
    init {
        mContext = context
    }
    fun setList(list:List<Daily>){
        daily=list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DailyForecastBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position+1<daily.size) {
            val sh: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val temperature= sh.getString("temperature","Celsius")
            Glide.with(mContext)
                .load("https://openweathermap.org/img/wn/" + daily[position+1].weather[0].icon + "@2x.png")
                .apply(
                    RequestOptions()
                        .override(150, 150)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground)
                )
                .into(holder.binding.dayIconImageView)
            val long = (daily[position+1].dt + root.timezone_offset - 7200).toLong() * 1000
            val date = Date(long)
            val format = SimpleDateFormat("EEE, dd")
            holder.binding.dayTextView.text = format.format(date)
            if(temperature=="Celsius") {
                holder.binding.dayTempTextView.text =
                    daily[position].temp.min.roundToInt().toString() + "/" + daily[position+1].temp.max.roundToInt()
                        .toString() + " °C"
            }
            else if (temperature=="Fahrenheit"){
                holder.binding.dayTempTextView.text =
                    convertFromCelsiusToFahrenheit(daily[position].temp.min).roundToInt().toString() + "/" +convertFromCelsiusToFahrenheit( daily[position+1].temp.max).roundToInt()
                        .toString() + " °F"
            }
            else{
                holder.binding.dayTempTextView.text =
                    convertFromCelsiusToKelvin(daily[position].temp.min).roundToInt().toString() + "/" +convertFromCelsiusToKelvin( daily[position+1].temp.max).roundToInt()
                        .toString() + " °K"
            }
            holder.binding.dayStatusTextView.text = daily[position+1].weather[0].description
//        holder.binding.dayIconImageView.setImageResource(daily[position].thumbnail)

        }
    }

    override fun getItemCount(): Int {
        return daily.size-1
    }

    class ViewHolder(var binding: DailyForecastBinding) : RecyclerView.ViewHolder(binding.root)

    fun convertFromCelsiusToFahrenheit(cel:Double):Double=((cel * (9.0/5)) + 32)
    fun convertFromCelsiusToKelvin(cel:Double):Double=(cel + 273.15)
}