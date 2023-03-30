package eg.gov.iti.jets.weatherapp.favoriteScreen.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.databinding.FavoriteItemBinding
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.Root

class FavoriteAdapter (private var myFavoritesDB:List<FavoritesDB>, val onClick:(FavoritesDB)->Unit) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>(){
    lateinit var binding: FavoriteItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavoriteItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.favLocationTextView.text=myFavoritesDB[position].place
        holder.binding.favLocationDeleteBtnImageButton.setOnClickListener {
            onClick(myFavoritesDB[position])
        }
        holder.binding.favItemLayout.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.homeFragment)
        }
    }
    override fun getItemCount(): Int {
        return myFavoritesDB.size
    }
    class ViewHolder(var binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)
}