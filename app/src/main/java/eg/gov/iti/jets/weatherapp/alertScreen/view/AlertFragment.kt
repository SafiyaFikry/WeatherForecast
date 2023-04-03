package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ContentInfoCompat.Flags
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelAlerts
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelFactoryAlerts
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentAlertBinding
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelFactoryHome
import eg.gov.iti.jets.weatherapp.homeScreen.viewModel.ViewModelHome
import eg.gov.iti.jets.weatherapp.model.AlertsDB
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared
import java.text.SimpleDateFormat
import java.util.*

class AlertFragment : Fragment() {
    lateinit var binding: FragmentAlertBinding
    lateinit var alertsViewModel: ViewModelAlerts
    lateinit var alertsFactory: ViewModelFactoryAlerts
    lateinit var alertsAdapter: AlertsAdapter
    lateinit var homeFactory: ViewModelFactoryHome
    lateinit var homeViewModel: ViewModelHome
    var timestamp = 0
    lateinit var dateTime: String
    var startDate = 0L
    var endDate = 0L
    val c = Calendar.getInstance()
    lateinit var address: MutableList<Address>
    lateinit var geocoder: Geocoder
    lateinit var des: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext().applicationContext)
        alertsFactory = ViewModelFactoryAlerts(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext().applicationContext)
            )
        )
        alertsViewModel = ViewModelProvider(this, alertsFactory).get(ViewModelAlerts::class.java)

        homeFactory = ViewModelFactoryHome(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext().applicationContext)
            )
        )
        homeViewModel = ViewModelProvider(this, homeFactory).get(ViewModelHome::class.java)

        alertsViewModel.alertsDB.observe(viewLifecycleOwner) { alerts ->

            binding.imageView.visibility = View.GONE
            binding.textView.visibility = View.GONE
            alertsAdapter = AlertsAdapter(alerts) {
                cancelAlarm(it.id)
                alertsViewModel.deleteAlert(it)
                alertsAdapter.notifyDataSetChanged()
            }
            binding.alertRecyclerView.adapter = alertsAdapter
            alertsAdapter.notifyDataSetChanged()

            if (alerts.size == 0) {
                binding.imageView.visibility = View.VISIBLE
                binding.textView.visibility = View.VISIBLE
            }
        }
        binding.alertFloatingActionButton.setOnClickListener {
            val notifications = shared.getBoolean("notifications", false)
            if (notifications == true) {
                showRadioConfirmationDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Enable Notifications in Settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun setDate(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()
                },

                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
    }

    fun showRadioConfirmationDialog() {
        var selectedOptionIndex = 0
        val option = arrayOf("Alert", "Notification")
        var selectedOption = option[selectedOptionIndex]
        val inflater = requireActivity().layoutInflater
        val myView = inflater.inflate(R.layout.alert_dialog, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Type of Alert?")
            .setView(myView)
        val start_ed = myView.findViewById<Button>(R.id.startDate_Btn)
        start_ed.setOnClickListener {
            setDate {
                startDate = it
                start_ed.text = SimpleDateFormat("dd/MM/YYYY hh:mm a").format(Date(startDate))
            }
        }
        val end_ed = myView.findViewById<Button>(R.id.endDate_Btn)
        end_ed.setOnClickListener {
            setDate {
                endDate = it
                end_ed.text = SimpleDateFormat("dd/MM/YYYY hh:mm a").format(Date(endDate))
            }

        }
        builder.setSingleChoiceItems(option, selectedOptionIndex) { dialog_, which ->
            selectedOptionIndex = which
            selectedOption = option[which]
        }
            .setPositiveButton("Ok") { dialog, which ->

                if (!Settings.canDrawOverlays(requireContext())) {
                    Toast.makeText(
                        requireContext(),
                        "The alarm may be not work as expected",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + requireContext().packageName)
                    )
                    someActivityResultLauncher.launch(intent)
                }
                val lat = shared.getString("lat", "33.44")
                val lon = shared.getString("lon", "-94.04")
                address = geocoder.getFromLocation(
                    lat!!.toDouble(),
                    lon!!.toDouble(),
                    10
                ) as MutableList<Address>
                des = "${address[0].adminArea} - ${address[0].countryName}"
                if (start_ed.text == "From" || end_ed.text == "To") {
                    Toast.makeText(
                        requireContext(),
                        "you forgot to enter (From) and (To) Date and Time !!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (selectedOption == "Alert") {
                        alertsViewModel.insertAlert(
                            AlertsDB(
                                countryName = des,
                                startDateTime = SimpleDateFormat("dd/MM/YYYY hh:mm a").format(
                                    Date(startDate)
                                ),
                                endDateTime = SimpleDateFormat("dd/MM/YYYY hh:mm a").format(
                                    Date(
                                        endDate
                                    )
                                ),
                                type = "alert"
                            )
                        )
                        //startAlarm(startDate, "alert",alertsViewModel.alertsDB.value!!.size+1)
                        startAlarm(
                            startDate,
                            "alert",
                            alertsViewModel.alertsDB.value!![alertsViewModel.alertsDB.value!!.size-1].id+1
                        )
                    } else {
                        alertsViewModel.insertAlert(
                            AlertsDB(
                                countryName = des,
                                startDateTime = SimpleDateFormat("dd/MM/YYYY hh:mm a").format(
                                    Date(startDate)
                                ),
                                endDateTime = SimpleDateFormat("dd/MM/YYYY hh:mm a").format(
                                    Date(
                                        endDate
                                    )
                                ),
                                type = "notification"
                            )
                        )
                        //startAlarm(startDate, "notification",alertsViewModel.alertsDB.value!!.size+1)
                        startAlarm(
                            startDate,
                            "notification",
                            alertsViewModel.alertsDB.value!![alertsViewModel.alertsDB.value!!.size - 1].id+1
                        )
                    }
                }
            }

            .create().show()
    }

    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Settings.canDrawOverlays(requireContext())) {
                Snackbar.make(
                    binding.root,
                    "The alarm may be not work as expected",
                    LENGTH_LONG
                ).show()
            }
        }

    fun startAlarm(time: Long, type: String, id: Int) {
        var alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        intent.putExtra("type", type)
        var pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(requireContext(), id, intent, PendingIntent.FLAG_IMMUTABLE)
        println("#################### start id $id")
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)

    }

    fun cancelAlarm(id: Int) {
        var alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        var pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(requireContext(), id, intent, PendingIntent.FLAG_IMMUTABLE)
        println("++++++++++++++++++ end id $id")
        alarmManager.cancel(pendingIntent)
    }


}