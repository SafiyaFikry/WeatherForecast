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
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelAlerts
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelFactoryAlerts
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentAlertBinding
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelFactoryHome
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelHome
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.ApiState
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class AlertFragment : Fragment() {
    lateinit var binding:FragmentAlertBinding
    lateinit var alertsViewModel: ViewModelAlerts
    lateinit var alertsFactory: ViewModelFactoryAlerts
    lateinit var alertsAdapter: AlertsAdapter
    lateinit var homeFactory: ViewModelFactoryHome
    lateinit var homeViewModel: ViewModelHome
    var dateTime="Date: "
    val c =Calendar.getInstance()
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
        alertsViewModel= ViewModelProvider(this,alertsFactory).get(ViewModelAlerts::class.java)

        homeFactory = ViewModelFactoryHome(Repository.getInstance(WeatherClient.getInstance(),
            ConcreteLocalSource(requireContext().applicationContext)
        ))
        homeViewModel = ViewModelProvider(this, homeFactory).get(ViewModelHome::class.java)

        alertsViewModel.alertsDB.observe(viewLifecycleOwner){alerts->

            binding.imageView.visibility=View.GONE
            binding.textView.visibility=View.GONE
            alertsAdapter= AlertsAdapter(alerts){
                cancelAlarm()
                alertsViewModel.deleteAlert(it)
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
            val notifications=shared.getBoolean("notifications",false)
            if (notifications==true) {
                showRadioConfirmationDialog()
            }
            else{
                Toast.makeText(requireContext(),"Enable Notifications in Settings",Toast.LENGTH_LONG).show()
            }
        }

    }
    fun openDialog():String{

        DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->

            dateTime+=dayOfMonth.toString()+"/"+month.toString()+"/"+year.toString()+"\n"

            TimePickerDialog(requireContext(),{ view, hourOfDay, minute ->
                //check time ******************
                if (hourOfDay>12) {
                    dateTime +="Time: "+ (hourOfDay-12).toString() + ":" + minute.toString()+" PM"
                }
                else{
                    dateTime +="Time: "+ (hourOfDay).toString() + ":" + minute.toString()+" AM"
                }
                c.set(Calendar.HOUR_OF_DAY,hourOfDay)
                c.set(Calendar.MINUTE,minute)
                c.set(Calendar.SECOND,0)
                println("1")
                Navigation.findNavController(requireView()).navigate(R.id.mapFragment)
                println("3")
                startAlarm(c/*,ViewModelHome.nameal*/)
                //ViewModelHome.nameal=""
                alertsViewModel.setDes("alerts")
                alertsViewModel.setDateTime(dateTime)

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
                    alertsViewModel.setType("alert")
                }
                else{
                    alertsViewModel.setType("notification")
                }
                openDialog()
            }
            .create().show()
    }
    fun startAlarm(c:Calendar/*,alertName:String*/){
        var alarmManger = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent= Intent(requireContext(),AlertReceiver::class.java)
        intent.putExtra("lat",ViewModelHome.lat.toString())
        intent.putExtra("lon",ViewModelHome.lon.toString())
        intent.putExtra("city",ViewModelHome.city)
        println("#####################################city: "+ViewModelHome.city)
        println("#####################################lat: "+ViewModelHome.lat)
        println("#####################################lon: "+ViewModelHome.lon)
        var pendingIntent:PendingIntent= PendingIntent.getBroadcast(requireContext(),1,intent,0)
        alarmManger.setExact(AlarmManager.RTC_WAKEUP,c.timeInMillis,pendingIntent)
        ViewModelHome.lat=0.0
        ViewModelHome.lon=0.0
        ViewModelHome.city=""
    }
    fun cancelAlarm(){
        var alarmManger = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent= Intent(requireContext(),AlertReceiver::class.java)
        var pendingIntent:PendingIntent= PendingIntent.getBroadcast(requireContext(),1,intent,0)
        alarmManger.cancel(pendingIntent)
    }


}