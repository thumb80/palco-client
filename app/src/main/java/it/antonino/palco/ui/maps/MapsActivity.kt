package it.antonino.palco.ui.maps

import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import it.antonino.palco.R
import it.antonino.palco.databinding.ActivityMapsBinding
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var place: String? = null
    private var artist: String? = null
    private var city: String? = null
    private var addresses: List<Address> = listOf()
    private lateinit var binding: ActivityMapsBinding

    val TAG = MapsActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)

        place = intent.extras?.getString("place")
        artist = intent.extras?.getString("artist")
        city = intent.extras?.getString("city")

        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val geodecoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = place?.plus(" $city")?.let { geodecoder.getFromLocationName(it, 10) } as List<Address>
        }
        catch (e: IOException) {
            Log.d(TAG, "Cannot parse location")
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        val zoomIn = 16.0f

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_map
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        if (addresses.size > 0) {
            binding.locationName.text = getString(
                R.string.maps_text_view,
                addresses.get(0).adminArea,
                addresses.get(0).locality,
                this.artist,
                this.place)
            val place = LatLng(addresses.get(0).latitude, addresses.get(0).longitude)

            val markerString = this.artist?.plus(" - ")?.plus(this.place)
            mMap.addMarker(MarkerOptions().position(place).title(markerString))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, zoomIn))

            mMap.uiSettings.setAllGesturesEnabled(true)
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isRotateGesturesEnabled = true
            mMap.uiSettings.isTiltGesturesEnabled = true
            mMap.uiSettings.isScrollGesturesEnabled = true
        }
        else {
            Toast.makeText(this, "Ops.. c'è stato un problema", Toast.LENGTH_LONG).show()
            this.onBackPressed()
        }
    }
}