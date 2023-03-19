package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.HourlyForecastBinding
import eg.gov.iti.jets.weatherapp.model.Hourly
import eg.gov.iti.jets.weatherapp.model.Root
import java.text.SimpleDateFormat
import java.util.*

class HourlyForecastAdapter (private var hours:List<Hourly>, var root: Root, context: Context) : RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>(){
    private var mContext: Context
    lateinit var binding: HourlyForecastBinding
    init {
        mContext = context
    }
    fun setList(list:List<Hourly>){
        hours=list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyForecastBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mContext)
            .load("https://openweathermap.org/img/wn/"+hours[position].weather[0].icon+"@2x.png")
            .apply(
                RequestOptions()
                    .override(150, 150)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
            )
            .into(holder.binding.timeIconImageView)
        val long =(hours[position].dt+root.timezone_offset-7200).toLong()*1000
        val date = Date(long)
        val format = SimpleDateFormat("hh:mm a")
        holder.binding.timeTextView.text=format.format(date)
        holder.binding.timeTempTextView.text=hours[position].temp.toInt().toString()+" °C"
        //holder.binding.timeIconImageView.setImageResource(hours[position].thumbnail)

    }

    override fun getItemCount(): Int {
        return hours.size
    }

    class ViewHolder(var binding: HourlyForecastBinding) : RecyclerView.ViewHolder(binding.root)
}