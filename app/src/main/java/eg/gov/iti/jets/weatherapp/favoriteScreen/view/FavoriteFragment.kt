package eg.gov.iti.jets.weatherapp.favoriteScreen.view

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentFavoriteBinding
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFactoryFavorites
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFavorite
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared

class FavoriteFragment : Fragment() {

    lateinit var binding:FragmentFavoriteBinding
    lateinit var viewModel:ViewModelFavorite
    lateinit var favFactory:ViewModelFactoryFavorites
    lateinit var favAdapter:FavoriteAdapter
    lateinit var favShared:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFavoriteBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favShared=requireContext().getSharedPreferences("favShared",Context.MODE_PRIVATE)
        val editor=favShared.edit()
        favFactory= ViewModelFactoryFavorites(Repository.getInstance(WeatherClient.getInstance(),
            ConcreteLocalSource(requireContext().applicationContext)
        ))
        viewModel= ViewModelProvider(this,favFactory).get(ViewModelFavorite::class.java)

        viewModel.favoritesDB.observe(viewLifecycleOwner){weather->

                binding.imageView.visibility=View.GONE
                binding.textView.visibility=View.GONE
                favAdapter= FavoriteAdapter(weather,{
                    showConfirmationDialog(it)
                },{
                    editor.putString("lat",it.lat.toString())
                    editor.putString("lon",it.lon.toString())
                    editor.commit()
                    Navigation.findNavController(requireView()).navigate(R.id.favDetailsFragment)
                })
                binding.favoriteRecycleView.adapter=favAdapter
                favAdapter.notifyDataSetChanged()

            if(weather.size==0){
                binding.imageView.visibility=View.VISIBLE
                binding.textView.visibility=View.VISIBLE
            }
        }
        binding.favoriteFloatingActionButton.setOnClickListener{
            //viewModel.setDes("fav")
            ViewModelFavorite.destination="fav"
            Navigation.findNavController(it).navigate(R.id.mapFragment)
        }
    }
    fun showConfirmationDialog(favoritesDB: FavoritesDB) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_confirmation)
            .setMessage(R.string.message)
            .setPositiveButton(R.string.okay) { dialog, which ->
                viewModel.deleteFav(favoritesDB)
                favAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton(R.string.cancel){ dialogInterface: DialogInterface, i: Int ->

            }
            .create().show()
    }
}