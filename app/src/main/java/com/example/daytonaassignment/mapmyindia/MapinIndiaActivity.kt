import com.example.daytonaassignment.mapmyindia.MapinIndiaVM


import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.daytonaassignment.R
import com.example.daytonaassignment.Utilities
import com.example.daytonaassignment.maps.RecentSearchAdapter
import com.example.daytonaassignment.maps.TextListItem
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.mapmyindia.sdk.plugin.annotation.OnSymbolDragListener
import com.mapmyindia.sdk.plugin.annotation.Symbol
import com.mapmyindia.sdk.plugin.annotation.SymbolManager
import com.mapmyindia.sdk.plugin.annotation.SymbolOptions
import com.mmi.services.api.PlaceResponse
import com.mmi.services.api.autosuggest.model.AutoSuggestAtlasResponse
import com.mmi.services.api.reversegeocode.MapmyIndiaReverseGeoCode
import com.mmi.services.api.textsearch.MapmyIndiaTextSearch
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_mapin_india.*
import kotlinx.android.synthetic.main.activity_mapin_india.et_search_query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MapinIndiaActivity : AppCompatActivity(), OnMapReadyCallback, LocationEngineListener {

    private lateinit var mapview: MapView
    private var mapboxMap: MapboxMap? = null
    private var symbolManager: SymbolManager? = null

    @Inject
    lateinit var viewModel: MapinIndiaVM
    private lateinit var recentSearchAdapter : RecentSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapin_india)

        mapview = findViewById(R.id.mapView)
        mapview.onCreate(savedInstanceState)
        mapview.getMapAsync(this)

        listeners()

        addObserver()

        setUpRecentSearchAdapter()

    }

    private fun setUpRecentSearchAdapter(){
        val onSearchItemClick = { position: Int ->
            val searchItem = recentSearchAdapter.getItemAt(position)
            if (searchItem != null){
                rv_recent_mip_search_list.visibility = View.GONE
                et_search_query.setText(searchItem.textRecentPlaces)
                Utilities.hideKeyboard(this, et_search_query)
           //     viewModel.getNearbyPlaces(searchItem.textRecentPlaces)
                setMarkerOnMap(searchItem.latitude as Double, searchItem.longitude as Double)
                initMarker(searchItem.latitude, searchItem.longitude)
            }else{
                Toast.makeText(this, "Error occured !", Toast.LENGTH_SHORT).show()
            }
        }

        //set adapter for recent place recyclerview
        rv_recent_mip_search_list.adapter = RecentSearchAdapter.newInstance(onSearchItemClick).also {
            recentSearchAdapter = it
        }
    }

    private fun setMarkerOnMap(latitude: Double, longitude: Double){

        mapboxMap?.clear()
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(latitude, longitude))
            .zoom(14.0)
            .tilt(0.0)
            .build()
        mapboxMap?.cameraPosition = cameraPosition

        mapboxMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))

    }

    private fun showRecentPlaces(recentPlaceList: List<TextListItem>) {

        if (recentPlaceList.isNotEmpty()) {
            rv_recent_mip_search_list.visibility = View.VISIBLE
            recentSearchAdapter.submitList(recentPlaceList)
        }
    }

    fun listeners(){

        ll_header.setOnClickListener {
            finish()
        }


        btn_done.setOnClickListener {
            val intent = Intent()
            intent.putExtra("address", ""+et_search_query.text.toString().trim())
            setResult(102, intent)
            finish()
        }

        btn_mipsearch.setOnClickListener {
            //either call the MapMyIndia Builder, which internally uses the Retrofit
            callTextSearchApi(et_search_query.text.toString().trim())
            Utilities.hideKeyboard(this, et_search_query)

            //or hit the auto suggest api directly
            //     viewModel.getNearbyPlaces(et_search_query.text.toString().trim())
            Log.d("*** On Click = ", "search button click")
        }
    }

    fun addObserver(){

        viewModel.placesLiveData.observe(this, Observer { places ->

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
            if (it) {
                mmi_progress.visibility = View.VISIBLE
            } else {
                mmi_progress.visibility = View.GONE
            }
        })
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        this.mapboxMap = mapboxMap
        val cameraPosition = CameraPosition.Builder()
        //    .target(LatLng(28.4595, 77.0266))
            .target(LatLng(26.8467, 80.92313))
            .zoom(14.0)
            .tilt(0.0)
            .build()
        mapboxMap?.cameraPosition = cameraPosition

        mapboxMap?.clear()
        mapboxMap?.addMarker(MarkerOptions().position(LatLng(28.4595, 77.0266)))

    //    initMarker(28.4595, 77.0266)
        initMarker(26.8467, 80.92313)

    }

    private fun initMarker(latitude: Double, longitude: Double){

        symbolManager = SymbolManager(mapview, mapboxMap!!)

        val symbolOptions = SymbolOptions()
            .icon(
                BitmapUtils.getBitmapFromDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.marker
                    )
                )
            )
            .draggable(true)
            .position(LatLng(latitude, longitude))
        symbolManager?.iconAllowOverlap = true
        symbolManager?.iconIgnorePlacement = false
        symbolManager?.create(symbolOptions)

        symbolManager?.addDragListener(object : OnSymbolDragListener {
            override fun onAnnotationDragStarted(p0: Symbol?) {

            }

            override fun onAnnotationDrag(p0: Symbol?) {

            }

            override fun onAnnotationDragFinished(symbol: Symbol?) {
                reverseGeocode(symbol?.position?.latitude, symbol?.position?.longitude)
                Toast.makeText(
                    this@MapinIndiaActivity,
                    "Lattitude = ${symbol?.position?.latitude} Longitude = ${symbol?.position?.longitude}",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })

    }

    private fun reverseGeocode(latitude: Double?, longitude: Double?) {
        viewModel.isLoading.postValue(true)
        MapmyIndiaReverseGeoCode.builder()
            .setLocation(latitude!!, longitude!!)
            .build().enqueueCall(object : Callback<PlaceResponse> {
                override fun onResponse(
                    call: Call<PlaceResponse>,
                    response: Response<PlaceResponse>
                ) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            val placesList = response.body()!!.places
                            val place = placesList[0]
                            val add = place.formattedAddress
                            Toast.makeText(this@MapinIndiaActivity, add, Toast.LENGTH_LONG).show()
                            //set address to edittext
                            et_search_query.setText("")
                            et_search_query.setText(place.formattedAddress)
                        } else {
                            Toast.makeText(
                                this@MapinIndiaActivity,
                                "Not able to get value, Try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MapinIndiaActivity,
                            response.message(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    viewModel.isLoading.postValue(false)
                }

                override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                    viewModel.isLoading.postValue(false)
                    Toast.makeText(this@MapinIndiaActivity, t.toString(), Toast.LENGTH_LONG).show()
                }
            })
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
        Log.d("*** onConnected = ", "lat = " + location?.latitude + " lon = " + location?.longitude)
    }

    private fun callTextSearchApi(searchString: String) {
        MapmyIndiaTextSearch.builder()
            .query(searchString)
            .build().enqueueCall(object : Callback<AutoSuggestAtlasResponse> {
                override fun onResponse(call: Call<AutoSuggestAtlasResponse>, response: Response<AutoSuggestAtlasResponse>) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            val suggestedList = response.body()?.suggestedLocations
                            if (suggestedList?.size ?: 0 > 0) {

                                val recentPlaces = suggestedList?.map {
                                    TextListItem(
                                        textRecentPlaces = it.placeName,
                                        longitude = it.longitude.toDouble(),
                                        latitude = it.latitude.toDouble()
                                    )
                                }
                                showRecentPlaces(recentPlaces?: arrayListOf())
                            }
                        } else {
                            Toast.makeText(this@MapinIndiaActivity, "Not able to get value, Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<AutoSuggestAtlasResponse>, t: Throwable) {
                    showToast(t.toString())
                }
            })
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}