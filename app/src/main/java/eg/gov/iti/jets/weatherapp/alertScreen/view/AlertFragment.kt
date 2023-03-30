package eg.gov.iti.jets.weatherapp.alertScreen.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelAlerts
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelFactoryAlerts
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentAlertBinding
import eg.gov.iti.jets.weatherapp.databinding.FragmentFavoriteBinding
import eg.gov.iti.jets.weatherapp.favoriteScreen.view.FavoriteAdapter
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFactoryFavorites
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFavorite
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
            openDialog()
        }
    }
    fun openDialog():String{
        var dateTime=""
        DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->

            dateTime=dayOfMonth.toString()+"."+month.toString()+"."+year.toString()+"\n"

            TimePickerDialog(requireContext(),{ view, hourOfDay, minute ->

                dateTime+=hourOfDay.toString()+":"+minute.toString()
                viewModel.setDes("alerts")
                viewModel.setDateTime(dateTime)
                Navigation.findNavController(requireView()).navigate(R.id.mapFragment)
            },Calendar.getInstance().get(Calendar.HOUR),Calendar.getInstance().get(Calendar.MINUTE),true).show()

       },Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()

        return dateTime
    }

}