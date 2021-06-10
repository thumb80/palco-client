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
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var place: String? = null
    private var artist: String? = null
    private var city: String? = null
    private var addresses: List<Address> = listOf()

    val TAG = MapsActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = intent.extras?.getString("place")
        artist = intent.extras?.getString("artist")
        city = intent.extras?.getString("city")

        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val geodecoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geodecoder.getFromLocationName(place?.plus(" $city"), 10)
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
            // Add a marker in Sydney and move the camera
            val place = LatLng(addresses.get(0).latitude, addresses.get(0).longitude)
            mMap.addMarker(MarkerOptions().position(place).title("${this.artist} in ${this.place}"))
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