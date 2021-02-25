package com.example.daytonaassignment.form

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.daytonaassignment.R
import com.example.daytonaassignment.Utilities
import com.example.daytonaassignment.mapmyindia.MapinIndiaVM
import com.example.daytonaassignment.maps.RecentSearchAdapter
import com.example.daytonaassignment.maps.TextListItem
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
import kotlinx.android.synthetic.main.activity_mapin_india.btn_mipsearch
import kotlinx.android.synthetic.main.activity_mapin_india.et_search_query
import kotlinx.android.synthetic.main.activity_mapin_india.rv_recent_mip_search_list
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapview: MapView
    private var mapboxMap: MapboxMap? = null
    private var symbolManager: SymbolManager? = null
    private lateinit var recentSearchAdapter : RecentSearchAdapter

    private var listener : MapListener? = null
//    @Inject
//    lateinit var viewModel: MapVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //    AndroidInjection.inject(requireActivity())
        super.onViewCreated(view, savedInstanceState)

//        viewModel = ViewModelProvider(this)
//            .get(MapVM::class.java)

        mapview = view.mapView
        mapview.onCreate(savedInstanceState)
        mapview.getMapAsync(this)

        setUpRecentSearchAdapter()

        btn_mipsearch.setOnClickListener {
            callTextSearchApi(et_search_query.text.toString().trim())
        }

        addObserver()
    }

    private fun addObserver(){

//        viewModel.placesLiveData.observe(requireActivity(), Observer {
//            Log.d("*** MapFragment = ", ""+it)
//        })
    }
    override fun onMapReady(mapbox: MapboxMap?) {
        this.mapboxMap = mapbox
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(28.4595, 77.0266))
            .zoom(14.0)
            .tilt(0.0)
            .build()
        mapboxMap?.cameraPosition = cameraPosition

        mapboxMap?.addMarker(MarkerOptions().position(LatLng(28.4595, 77.0266)))

        initMarker(28.4595, 77.0266)

    }

    private fun initMarker(latitude: Double, longitude: Double){

        symbolManager = SymbolManager(mapview, mapboxMap!!)

        val symbolOptions = SymbolOptions()
            .icon(
                BitmapUtils.getBitmapFromDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
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
                    requireContext(),
                    "Lattitude = ${symbol?.position?.latitude} Longitude = ${symbol?.position?.longitude}",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })

    }

    private fun reverseGeocode(latitude: Double?, longitude: Double?) {
   //     viewModel.isLoading.postValue(true)
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
                            Toast.makeText(requireActivity(), add, Toast.LENGTH_LONG).show()
                            //set address to edittext
                            et_search_query.setText("")
                            et_search_query.setText(place.formattedAddress)

                     //       viewModel.placesLiveData.value = place.formattedAddress

                            listener?.selectedPlace(place.formattedAddress)

                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "Not able to get value, Try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.message(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

            //        viewModel.isLoading.postValue(false)
                }

                override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
            //        viewModel.isLoading.postValue(false)
                    Toast.makeText(requireActivity(), t.toString(), Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onMapError(p0: Int, p1: String?) {
        TODO("Not yet implemented")
    }

    private fun setUpRecentSearchAdapter(){
        val onSearchItemClick = { position: Int ->
            val searchItem = recentSearchAdapter.getItemAt(position)
            if (searchItem != null){
                rv_recent_mip_search_list.visibility = View.GONE
                et_search_query.setText(searchItem.textRecentPlaces)
            //    viewModel.placesLiveData.value = searchItem.textRecentPlaces
                listener?.selectedPlace(searchItem.textRecentPlaces)

                Utilities.hideKeyboard(requireActivity(), et_search_query)
                //     viewModel.getNearbyPlaces(searchItem.textRecentPlaces)
                setMarkerOnMap(searchItem.latitude, searchItem.longitude)
                initMarker(searchItem.latitude, searchItem.longitude)
            }else{
                Toast.makeText(requireActivity(), "Error occured !", Toast.LENGTH_SHORT).show()
            }
        }

        //set adapter for recent place recyclerview
        rv_recent_mip_search_list.adapter = RecentSearchAdapter.newInstance(onSearchItemClick).also {
            recentSearchAdapter = it
        }
    }

    private fun setMarkerOnMap(latitude: Double, longitude: Double){

        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(latitude, longitude))
            .zoom(14.0)
            .tilt(0.0)
            .build()
        mapboxMap?.cameraPosition = cameraPosition

        mapboxMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
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
                            Toast.makeText(requireActivity(), "Not able to get value, Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<AutoSuggestAtlasResponse>, t: Throwable) {
                    showToast(t.toString())
                }
            })
    }

    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showRecentPlaces(recentPlaceList: List<TextListItem>) {

        if (recentPlaceList.isNotEmpty()) {
            rv_recent_mip_search_list.visibility = View.VISIBLE
            recentSearchAdapter.submitList(recentPlaceList)
        }
    }

    interface MapListener{
        fun selectedPlace(location : String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if ( context is MapListener){
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (listener != null){
            listener = null
        }
    }
}