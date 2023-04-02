package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.AlertsItemBinding
import eg.gov.iti.jets.weatherapp.model.AlertsDB

class AlertsAdapter (private var myAlertsDB:List<AlertsDB>, val onClick:(AlertsDB)->Unit) : RecyclerView.Adapter<AlertsAdapter.ViewHolder>(){
    lateinit var binding: AlertsItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertsItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.alertPlaceTextView.text=myAlertsDB[position].countryName
        holder.binding.startdateTimeTextView.text=myAlertsDB[position].startDateTime
        holder.binding.endDateTimeTextView.text=myAlertsDB[position].endDateTime
        holder.binding.alertDeleteBtnImageButton.setOnClickListener {
            onClick(myAlertsDB[position])
        }
        if (myAlertsDB[position].type=="alert"){
            holder.binding.image.setImageResource(R.drawable.baseline_warning_24)
        }else{
            holder.binding.image.setImageResource(R.drawable.baseline_notifications_24)
        }
    }
    override fun getItemCount(): Int {
        return myAlertsDB.size
    }
    class ViewHolder(var binding: AlertsItemBinding) : RecyclerView.ViewHolder(binding.root)
}