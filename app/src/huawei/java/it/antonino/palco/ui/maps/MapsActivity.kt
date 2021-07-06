package it.antonino.palco.ui.maps

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.util.LogM
import it.antonino.palco.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    // HUAWEI map
    private var hMap: HuaweiMap? = null

    private var mMapView: MapView? = null

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mMapView = findViewById(R.id.mapView)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle =
                savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView?.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@MapsActivity)
        }
    }

    override fun onMapReady(map: HuaweiMap) {
        hMap = map
    }
}