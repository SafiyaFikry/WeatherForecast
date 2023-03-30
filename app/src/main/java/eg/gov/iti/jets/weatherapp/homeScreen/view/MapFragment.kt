package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import eg.gov.iti.jets.weatherapp.R
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelAlerts
import eg.gov.iti.jets.weatherapp.alertScreen.viewModel.ViewModelFactoryAlerts
import eg.gov.iti.jets.weatherapp.database.ConcreteLocalSource
import eg.gov.iti.jets.weatherapp.databinding.FragmentMapBinding
import eg.gov.iti.jets.weatherapp.favoriteScreen.view.FavoriteAdapter
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFactoryFavorites
import eg.gov.iti.jets.weatherapp.favoriteScreen.viewModel.ViewModelFavorite
import eg.gov.iti.jets.weatherapp.model.AlertsDB
import eg.gov.iti.jets.weatherapp.model.FavoritesDB
import eg.gov.iti.jets.weatherapp.model.Repository
import eg.gov.iti.jets.weatherapp.network.WeatherClient
import eg.gov.iti.jets.weatherapp.splashScreen.shared

class MapFragment : Fragment() {

    lateinit var binding:FragmentMapBinding
    lateinit var myPoint:Point
    lateinit var destination: String
    lateinit var favFactory: ViewModelFactoryFavorites
    lateinit var viewModelFav:ViewModelFavorite
    lateinit var alertsFactory: ViewModelFactoryAlerts
    lateinit var viewModelAlerts:ViewModelAlerts
    lateinit var address:MutableList<Address>
    lateinit var geocoder: Geocoder
    lateinit var des:String
    //lateinit var shared: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMapBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        destination=""
        favFactory= ViewModelFactoryFavorites(
            Repository.getInstance(
                WeatherClient.getInstance(),
            ConcreteLocalSource(requireContext().applicationContext)
        ))
        viewModelFav= ViewModelProvider(this,favFactory).get(ViewModelFavorite::class.java)
        alertsFactory= ViewModelFactoryAlerts(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext().applicationContext)
            ))
        viewModelAlerts= ViewModelProvider(this,alertsFactory).get(ViewModelAlerts::class.java)

        geocoder= Geocoder(requireContext().applicationContext)
        //shared= PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
       /* val camera=CameraOptions.Builder()
            .center(Point.fromLngLat(shared.getString("lat","33.44")!!.toDouble(),shared.getString("lon","-94.04")!!.toDouble())).zoom(6.5).build()
        binding.mapView.getMapboxMap().setCamera(camera)*/
        binding.mapView.getMapboxMap().addOnMapLongClickListener { point ->
            binding.mapView.annotations.cleanup()
            binding.setLocationBtn.visibility = View.VISIBLE
            addAnnotationToMap(point)
            myPoint=point
            true
        }

        binding.setLocationBtn.setOnClickListener {
            if(viewModelFav.getDes()=="home"&&viewModelAlerts.getDes()=="home") {
                val editor=shared.edit()
                editor.putString("lat",myPoint.latitude().toString())
                editor.putString("lon",myPoint.longitude().toString())
                editor.commit()
                Navigation.findNavController(it).navigate(R.id.homeFragment)
            }
            else if(viewModelFav.getDes()=="fav"){
                address = geocoder.getFromLocation(myPoint.latitude(),myPoint.longitude(), 10) as MutableList<Address>
                des = "${address[0].adminArea}\n${address[0].countryName}"
                viewModelFav.addFav(FavoritesDB(des,myPoint.latitude(),myPoint.longitude()))
                viewModelFav.setDes("home")
                Navigation.findNavController(it).navigate(R.id.favoriteFragment)
            }
            else if(viewModelAlerts.getDes()=="alerts"){
                address = geocoder.getFromLocation(myPoint.latitude(),myPoint.longitude(), 10) as MutableList<Address>
                des = "${address[0].adminArea}\n${address[0].countryName}"
                viewModelAlerts.insertAlert(AlertsDB(0,des,viewModelAlerts.getDateTime(),viewModelAlerts.getType()))
                viewModelAlerts.setDes("home")
                Navigation.findNavController(it).navigate(R.id.alertFragment)
            }
            else{
                Toast.makeText(requireContext(),"nothing",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAnnotationToMap(point:Point) {

        bitmapFromDrawableRes(
            requireActivity().applicationContext,
            R.drawable.red_marker
        )?.let {
            val annotationApi = binding.mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(binding.mapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)

        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))
    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}
