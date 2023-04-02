package eg.gov.iti.jets.weatherapp.favoriteScreen.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentFavoriteBinding
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFactoryFavorites
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFavorite
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.WeatherClient

class FavoriteFragment : Fragment() {

    lateinit var binding:FragmentFavoriteBinding
    lateinit var viewModel:ViewModelFavorite
    lateinit var favFactory:ViewModelFactoryFavorites
    lateinit var favAdapter:FavoriteAdapter
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
        favFactory= ViewModelFactoryFavorites(Repository.getInstance(WeatherClient.getInstance(),
            ConcreteLocalSource(requireContext().applicationContext)
        ))
        viewModel= ViewModelProvider(this,favFactory).get(ViewModelFavorite::class.java)

        viewModel.favoritesDB.observe(viewLifecycleOwner){weather->

                binding.imageView.visibility=View.GONE
                binding.textView.visibility=View.GONE
                favAdapter= FavoriteAdapter(weather,{
                    viewModel.deleteFav(it)
                    favAdapter.notifyDataSetChanged()
                },{


                })
                binding.favoriteRecycleView.adapter=favAdapter
                favAdapter.notifyDataSetChanged()

            if(weather.size==0){
                binding.imageView.visibility=View.VISIBLE
                binding.textView.visibility=View.VISIBLE
            }
        }
        binding.favoriteFloatingActionButton.setOnClickListener{
            viewModel.setDes("fav")
            Navigation.findNavController(it).navigate(R.id.mapFragment)
        }
    }
}