package eg.gov.iti.jets.weatherapp.favoriteScreen.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.FragmentFavoriteBinding
import eg.gov.iti.jets.weatherapp.databinding.FragmentHomeBinding

class FavoriteFragment : Fragment() {

    lateinit var binding:FragmentFavoriteBinding
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
        binding.favoriteFloatingActionButton.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.mapFragment)
        }

    }
}