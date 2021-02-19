package com.example.daytonaassignment.mapmyindia

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.daytonaassignment.R
import androidx.lifecycle.Observer
import com.example.daytonaassignment.Utilities
import com.example.daytonaassignment.maps.RecentSearchAdapter
import com.example.daytonaassignment.maps.TextListItem
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.mapboxsdk.MapmyIndia
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mmi.services.account.MapmyIndiaAccountManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mapin_india.*
import kotlinx.android.synthetic.main.activity_mapin_india.et_search_query
import javax.inject.Inject

class MapinIndiaActivity : AppCompatActivity(), OnMapReadyCallback, LocationEngineListener {

    private lateinit var mapview: MapView
    private var mapboxMap: MapboxMap? = null

    @Inject
    lateinit var viewModel: MapinIndiaVM
    private lateinit var recentSearchAdapter : RecentSearchAdapter
    private var mCurrLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        initializeMap()
        setContentView(R.layout.activity_mapin_india)

        mapview = findViewById(R.id.mapView)
        mapview.onCreate(savedInstanceState)
        mapview.getMapAsync(this)

        listeners()

        addObserver()

        setUpRecentSearchAdapter()
    }

    private fun setUpRecentSearchAdapter(){
        val onSearchItemClick = {position : Int ->
            val searchItem = recentSearchAdapter.getItemAt(position)
            if (searchItem != null){
                rv_recent_mip_search_list.visibility = View.GONE
                et_search_query.setText(searchItem.textRecentPlaces)
                Utilities.hideKeyboard(this, et_search_query)
           //     viewModel.getNearbyPlaces(searchItem.textRecentPlaces)
                setMarkerOnMap(searchItem.latitude, searchItem.longitude)
            }else{
                Toast.makeText(this, "Error occured !", Toast.LENGTH_SHORT).show()
            }
        }

        //set adapter for recent place recyclerview
        rv_recent_mip_search_list.adapter = RecentSearchAdapter.newInstance(onSearchItemClick).also {
            recentSearchAdapter = it
        }
    }

    private fun setMarkerOnMap(latitude : Double, longitude : Double){

        mapboxMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))

    }

    private fun showRecentPlaces(recentPlaceList: List<TextListItem>) {

        if (recentPlaceList.isNotEmpty()) {
            rv_recent_mip_search_list.visibility = View.VISIBLE
            recentSearchAdapter.submitList(recentPlaceList)
        }
    }

    fun listeners(){

        btn_mipsearch.setOnClickListener {
            viewModel.getNearbyPlaces(et_search_query.text.toString().trim())
            Log.d("*** On Click = ", "search button click")
        }
    }

    fun addObserver(){

        viewModel.placesLiveData.observe(this, Observer {places ->

            val recentPlaces = places.suggestedLocations?.map {
                TextListItem(
                    textRecentPlaces = it.placeName,
                    longitude = it.longitude,
                    latitude = it.latitude
                )
            }
            showRecentPlaces(recentPlaces)

        })

        viewModel.isLoading.observe(this, Observer {
            if (it){
                mmi_progress.visibility = View.VISIBLE
            }else{
                mmi_progress.visibility = View.GONE
            }
        })
    }

    fun initializeMap(){
        MapmyIndiaAccountManager.getInstance().restAPIKey = "hxhuuxpuv1fld3x5iywyyl1jpkexcvps"
        MapmyIndiaAccountManager.getInstance().mapSDKKey = "s7ymgngc189paj74h33r1fmpbdym8nqz"
        MapmyIndiaAccountManager.getInstance().atlasClientId = "33OkryzDZsIPpKdCDcWqpfoGvluX5e3ENsWSqfyumc1QbWfgSeMC8oneW9XVrqnR4tsyE3_vbHqeqVxqpxa457lRYjnnCiTVAmrOJN8IZpMm7A75hBoV5w=="
        MapmyIndiaAccountManager.getInstance().atlasClientSecret = "lrFxI-iSEg8XC1YkQ_EuWFfpVShSmjrVpSXdhPVqHF2zG3wPZH9misBuZpXf4JZ448q_LPjdtPopLlefFS_zVSWe-eb1eeJuH_KpsSd9ZPMemA7bG4ojd7IKyhh9gmfA"
        //      MapmyIndiaAccountManager.getInstance().atlasGrantType = getAtlasGrantType()
        MapmyIndia.getInstance(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        this.mapboxMap = mapboxMap
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(28.4595, 77.0266))
            .zoom(14.0)
            .tilt(0.0)
            .build()
        mapboxMap?.cameraPosition = cameraPosition

        mapboxMap?.addMarker(MarkerOptions().position(LatLng(28.4595, 77.0266)))
    }

    override fun onMapError(p0: Int, p1: String?) {
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapview.onSaveInstanceState(outState)
    }

    override fun onConnected() {
        Log.d("*** onConnected = ", "true")
    }

    override fun onLocationChanged(location: Location?) {
        Log.d("*** onConnected = ", "lat = "+location?.latitude + " lon = "+location?.longitude)
    }

}