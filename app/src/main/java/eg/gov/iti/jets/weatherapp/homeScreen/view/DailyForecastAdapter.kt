package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.DailyForecastBinding
import eg.gov.iti.jets.weatherapp.homeScreen.Day
import eg.gov.iti.jets.weatherapp.model.Daily
import eg.gov.iti.jets.weatherapp.model.Root
import java.util.*

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
        Glide.with(mContext)
            .load(daily[position].weather[0].icon)
            .apply(
                RequestOptions()
                    .override(150, 150)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
            )
            .into(holder.binding.dayIconImageView)
        val long =(daily[position].dt+root.timezone_offset-7200).toLong()*1000
        val date = Date(long).toString()
        holder.binding.dayTextView.text=date
        holder.binding.dayTempTextView.text=daily[position].temp.toString()
        holder.binding.dayStatusTextView.text=daily[position].weather[0].description
//        holder.binding.dayIconImageView.setImageResource(daily[position].thumbnail)

    }

    override fun getItemCount(): Int {
        return daily.size
    }

    class ViewHolder(var binding: DailyForecastBinding) : RecyclerView.ViewHolder(binding.root)
}