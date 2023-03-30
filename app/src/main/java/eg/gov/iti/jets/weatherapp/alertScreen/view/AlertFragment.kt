package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelAlerts
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelFactoryAlerts
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentAlertBinding
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import java.util.Calendar

class AlertFragment : Fragment() {
    lateinit var binding:FragmentAlertBinding
    lateinit var viewModel: ViewModelAlerts
    lateinit var alertsFactory: ViewModelFactoryAlerts
    lateinit var alertsAdapter: AlertsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAlertBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertsFactory= ViewModelFactoryAlerts(
            Repository.getInstance(
                WeatherClient.getInstance(),
            ConcreteLocalSource(requireContext().applicationContext)
        ))
        viewModel= ViewModelProvider(this,alertsFactory).get(ViewModelAlerts::class.java)

        viewModel.alertsDB.observe(viewLifecycleOwner){alerts->

            binding.imageView.visibility=View.GONE
            binding.textView.visibility=View.GONE
            alertsAdapter= AlertsAdapter(alerts){
                viewModel.deleteAlert(it)
                alertsAdapter.notifyDataSetChanged()
            }
            binding.alertRecyclerView.adapter=alertsAdapter
            alertsAdapter.notifyDataSetChanged()

            if(alerts.size==0){
                binding.imageView.visibility=View.VISIBLE
                binding.textView.visibility=View.VISIBLE
            }
        }
        binding.alertFloatingActionButton.setOnClickListener{
            //check on notification is enabled or not
            showRadioConfirmationDialog()
        }
    }
    fun openDialog():String{
        var dateTime="Date: "
        DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->

            dateTime+=dayOfMonth.toString()+"/"+month.toString()+"/"+year.toString()+"\n"

            TimePickerDialog(requireContext(),{ view, hourOfDay, minute ->

                if (hourOfDay>=12) {
                    dateTime +="Time: "+ hourOfDay.toString() + ":" + minute.toString()+" PM"
                }
                else{
                    dateTime +="Time: "+ hourOfDay.toString() + ":" + minute.toString()+" AM"
                }
                val c =Calendar.getInstance()
                c.set(Calendar.HOUR_OF_DAY,hourOfDay)
                c.set(Calendar.MINUTE,minute)
                c.set(Calendar.SECOND,0)
                startAlarm(c)
                viewModel.setDes("alerts")
                viewModel.setDateTime(dateTime)
                Navigation.findNavController(requireView()).navigate(R.id.mapFragment)
            },Calendar.getInstance().get(Calendar.HOUR),Calendar.getInstance().get(Calendar.MINUTE),false).show()

       },Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()

        return dateTime
    }

    fun showRadioConfirmationDialog() {
        var selectedOptionIndex= 0
        val option = arrayOf("Alert", "Notification")
        var selectedOption = option[selectedOptionIndex]
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Type of Alert?")
            .setSingleChoiceItems(option, selectedOptionIndex) { dialog_, which ->
                selectedOptionIndex = which
                selectedOption = option[which]
            }
            .setPositiveButton("Ok") { dialog, which ->
                if(selectedOption=="Alert"){
                    viewModel.setType("alert")
                }
                else{
                    viewModel.setType("notification")
                }
                openDialog()
            }
            .create().show()
    }
    fun startAlarm(c:Calendar){
        var alarmManger = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent= Intent(requireContext(),AlertReceiver::class.java)
        var pendingIntent:PendingIntent= PendingIntent.getBroadcast(requireContext(),1,intent,0)

        alarmManger.setExact(AlarmManager.RTC_WAKEUP,c.timeInMillis,pendingIntent)
    }
    fun cancelAlarm(){
        var alarmManger = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent= Intent(requireContext(),AlertReceiver::class.java)
        var pendingIntent:PendingIntent= PendingIntent.getBroadcast(requireContext(),1,intent,0)
        alarmManger.cancel(pendingIntent)
    }

}