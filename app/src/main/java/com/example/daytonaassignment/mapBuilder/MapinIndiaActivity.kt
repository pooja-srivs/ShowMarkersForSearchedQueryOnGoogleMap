package com.example.daytonaassignment.mapBuilder


import android.content.Intent
import android.graphics.Color
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
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.Polygon
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mapin_india.*
import kotlinx.android.synthetic.main.activity_mapin_india.et_search_query
import kotlinx.android.synthetic.main.activity_mapin_india.ll_header
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class MapinIndiaActivity : AppCompatActivity(), OnMapReadyCallback, LocationEngineListener {

    private lateinit var mapview: MapView
    private var mapboxMap: MapboxMap? = null
    private var symbolManager: SymbolManager? = null
    private val listOfLatlang = ArrayList<LatLng>()
    private val listOfLatlang2 = ArrayList<LatLng>()
    private var polygon : Polygon? = null

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

    private fun setPolygon(listOfLatlang: ArrayList<LatLng>) {
        mapboxMap?.setPadding(20, 20, 20, 20)
        /* this is done for move camera focus to particular position */

        /* this is done for move camera focus to particular position */
        val latLngBounds = LatLngBounds.Builder().includes(listOfLatlang).build()
        mapboxMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 70))

        polygon = mapboxMap?.addPolygon(
            PolygonOptions().addAll(this.listOfLatlang)
                .fillColor(
                Color.parseColor("#753bb2d0")
            )
        )
        polygon?.id = 300

    }

    private fun setUpRecentSearchAdapter(){
        val onSearchItemClick = { position: Int ->
            val searchItem = recentSearchAdapter.getItemAt(position)
            if (searchItem != null){
                rv_recent_mip_search_list.visibility = View.GONE
                et_search_query.setText(searchItem.textRecentPlaces)
                Utilities.hideKeyboard(this, et_search_query)
           //     viewModel.getNearbyPlaces(searchItem.textRecentPlaces)
                Log.d("*** MapMyIndiaAct >>>> ${searchItem.latitude} ${searchItem.longitude}", "")
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
        mapboxMap?.removeAnnotations()
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

      /*  btn_placeAutocomplete.setOnClickListener {
            val placeAutocompleteActivity: Intent = PlaceAutocomplete.IntentBuilder()
                //      .placeOptions(placeOptions)
                .build(this)
            startActivityForResult(placeAutocompleteActivity, 102)
        }
*/

        ll_header.setOnClickListener {
            finish()
        }


        btn_done.setOnClickListener {
            val intent = Intent()
            intent.putExtra("address", "" + et_search_query.text.toString().trim())
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

        listOfLatlang.add(LatLng(28.703900, 77.101318))
        listOfLatlang.add(LatLng(28.703331, 77.102155))
        listOfLatlang.add(LatLng(28.703905, 77.102761))
        listOfLatlang.add(LatLng(28.704248, 77.102370))

        setPolygon(listOfLatlang)
        /*listOfLatlang2.add(LatLng(28.703901, 77.101319))
        listOfLatlang2.add(LatLng(28.703332, 77.102156))
        listOfLatlang2.add(LatLng(28.703906, 77.102762))
        listOfLatlang2.add(LatLng(28.704249, 77.102371))
        setPolygon(listOfLatlang2)
*/
        mapboxMap?.setOnPolygonClickListener {
            Log.d("*** POLYGON CLICKED 1>>>>", ""+it.points)
            Log.d("*** POLYGON CLICKED 2>>>>", ""+it.holes)
            Log.d("*** POLYGON CLICKED 3>>>>", ""+it.id)
        }
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
                override fun onResponse(
                    call: Call<AutoSuggestAtlasResponse>,
                    response: Response<AutoSuggestAtlasResponse>
                ) {
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
                                showRecentPlaces(recentPlaces ?: arrayListOf())
                            }
                        } else {
                            Toast.makeText(
                                this@MapinIndiaActivity,
                                "Not able to get value, Try again.",
                                Toast.LENGTH_SHORT
                            ).show()
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

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 102) {
            if(resultCode == Activity.RESULT_OK) {
         //       val eLocation : ELocation = Gson().fromJson(data?.getStringExtra(PlaceConstants.RETURNING_ELOCATION_DATA), ELocation::class.java)

                //Log.d("*** eloc = ", ""+eLocation)
            }
        }
    }
*/
}