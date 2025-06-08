package it.antonino.palco.maps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.antonino.palco.databinding.ActivityMapsBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura osmdroid
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName


        mapView = binding.mapview
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Imposta il punto di partenza (Roma)
        val latitude = intent.extras?.getDouble("latitude") ?: 41.9028
        val longitude = intent.extras?.getDouble("longitude") ?: 12.4964

        val startPoint = GeoPoint(latitude, longitude)
        mapView.controller.setZoom(18.0)
        mapView.controller.setCenter(startPoint)

        // Aggiungi un marker
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = intent.extras?.getString("locationName")
        mapView.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

}