package eg.gov.iti.jets.weatherapp.homeScreen.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
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
import eg.gov.iti.jets.weatherapp.databinding.FragmentMapBinding
import eg.gov.iti.jets.weatherapp.splashScreen.shared

class MapFragment : Fragment() {

    lateinit var binding:FragmentMapBinding
    lateinit var myPoint:Point
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
            val editor=shared.edit()
            editor.putString("lat",myPoint.latitude().toString())
            editor.putString("lon",myPoint.longitude().toString())
            editor.commit()
            Navigation.findNavController(it).navigate(R.id.homeFragment)
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
