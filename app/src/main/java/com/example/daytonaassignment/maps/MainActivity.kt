package com.example.daytonaassignment.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.daytonaassignment.R
import com.example.daytonaassignment.Utilities
import com.example.daytonaassignment.data.remote.sources.SearchDataModel
import com.example.daytonaassignment.form.FormActivity
import com.example.daytonaassignment.form.OptionActivity
import com.example.daytonaassignment.maps.di.RecentSearchListItem
import com.example.daytonaassignment.maps.di.UserPlaceAdapter
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLocationRequest: LocationRequest
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var recentSearchAdapter : RecentSearchAdapter
    private lateinit var userPlaceAdapter : UserPlaceAdapter
    private var userPlaceList : ArrayList<RecentSearchListItem> = arrayListOf()
    private var isExpanded : Boolean = true

    private var mapFrag: SupportMapFragment? = null

    companion object {
       private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val RADIUS = "5000"
    }

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)

        addObserver()

        setListeners()

        setUpRecentSearchAdapter()

        setUpUserPlacesAdapter()

        //set user fav loc adapter
        userPlaceAdapter.submitList(setUserPlaceData())
    }

    private fun setListeners(){

        btn_mapmyindia.setOnClickListener {
            startActivity(Intent(this, OptionActivity::class.java))
        }

        btn_search.setOnClickListener {
            ll_recent_search.visibility = GONE
            et_search_query.text.toString().trim().let { str ->
                Utilities.hideKeyboard(this, btn_search)
                if(str.isNotEmpty()){
                    viewModel.getNearbyPlaces(str)
                }
            }
        }

        iv_hamburger.setOnClickListener {
            Utilities.hideKeyboard(this, iv_hamburger)
            if (isExpanded){
                ll_user_places_list.visibility = VISIBLE
                cl_search.visibility = GONE
                isExpanded = false
            }else{
                ll_user_places_list.visibility = GONE
                cl_search.visibility = View.VISIBLE
                isExpanded = true
            }
        }

        et_search_query.setOnTouchListener(OnTouchListener { view, motionEvent ->
            val recentPlaces = viewModel.getAllRecentSearchedData()?.map {
                TextListItem(
                    textRecentPlaces = it.name
                )
            }
            showRecentPlaces(recentPlaces)
            false
        })
    }

    private fun setUpRecentSearchAdapter(){
        val onSearchItemClick = {position : Int ->
            val searchItem = recentSearchAdapter.getItemAt(position)
            if (searchItem != null){
                ll_recent_search.visibility = GONE
                et_search_query.setText(searchItem.textRecentPlaces)
                Utilities.hideKeyboard(this, et_search_query)
                viewModel.getNearbyPlaces(searchItem.textRecentPlaces)

            }else{
                Toast.makeText(this@MainActivity, "Error occured !", Toast.LENGTH_SHORT).show()
            }
        }

        //set adapter for recent place recyclerview
        rv_recent_search_list.adapter = RecentSearchAdapter.newInstance(onSearchItemClick).also {
            recentSearchAdapter = it
        }
    }

    private fun setUpUserPlacesAdapter(){
        val onItemClick = {position : Int ->
            val searchItem = userPlaceAdapter.getItemAt(position)
            if (searchItem != null){
                ll_user_places_list.visibility = GONE
                val latlng = LatLng(searchItem.latitude, searchItem.longitude)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0F))
                cl_search.visibility = VISIBLE
            }else{
                Toast.makeText(this@MainActivity, "Error occured !", Toast.LENGTH_SHORT).show()
            }
        }

        val onFavItemClick = {position : Int, isFav : Boolean ->
            val searchItem = userPlaceAdapter.getItemAt(position)
            if (searchItem != null){
                val distance = viewModel.getDistance(searchItem.latitude, searchItem.longitude)
                viewModel.insertUserPlaceItem(searchItem, distance)
                ll_user_places_list.visibility = GONE
                cl_search.visibility = VISIBLE

            }else{
                Toast.makeText(this@MainActivity, "Error occured !", Toast.LENGTH_SHORT).show()
            }
        }

        //set adapter for recent place recyclerview
        rv_user_place_list.adapter = UserPlaceAdapter.newInstance(onItemClick, onFavItemClick).also {
            userPlaceAdapter = it
        }
    }

    private fun showRecentPlaces(recentPlaceList: List<TextListItem>) {

        if (recentPlaceList.isNotEmpty()) {
            ll_recent_search.visibility = VISIBLE
            recentSearchAdapter.submitList(recentPlaceList)
        }
    }

    private fun setUserPlaceRecyclerView(places: SearchDataModel) {

        setUserPlaceData()
        places.results.map {place ->

            userPlaceList.add(RecentSearchListItem(
                textname = place.name,
                textAddress = place.place_id,
                isFav = false,
                latitude = place.geometry.location.lat,
                longitude = place.geometry.location.lng,
                rating = place.rating,
                currentlyOpen = place.opening_hours?.open_now?: false,
                distance = 0
            ))
        }

        userPlaceAdapter.submitList(userPlaceList)

    }

    private fun addObserver(){

        viewModel.placesLiveData.observe(this, Observer {places ->

            setUserPlaceRecyclerView(places)

            mGoogleMap.clear()

            places.results.map {place ->

                val latlng = LatLng(place.geometry.location.lat, place.geometry.location.lng)

                MarkerOptions().apply {
                    position(latlng)
                    title(place.name)
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                }
            }.also {
                Log.d("Http Markers = ", "$it")
            }
                .map { marker ->
                    mCurrLocationMarker = mGoogleMap.addMarker(marker)
            }

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getLanlng(), 13.0F))

        })

        viewModel.isLoading.observe(this, Observer {
            if (it){
                progress.visibility = VISIBLE
            }else{
                progress.visibility = GONE
            }
        })

        viewModel.errorLiveData.observe(this, Observer {
            Toast.makeText(this, "Please try again !", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap
        mGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mLocationRequest = LocationRequest().apply {
            interval = 120000
            fastestInterval = 120000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                mGoogleMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mGoogleMap.isMyLocationEnabled = true
        }
    }

    public override fun onPause() {
        super.onPause()
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude())
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions().apply {
                    position(latLng)
                    title("Current Position")
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                }
                //save lat lon in viewmodel
                viewModel.setGetLatLon(latLng)
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0F))
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()


            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        mFusedLocationClient?.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                        mGoogleMap.setMyLocationEnabled(true)
                    }

                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun setUserPlaceData(): List<RecentSearchListItem> {
        if (userPlaceList.isNullOrEmpty()) {
            viewModel.getAllUserPlaceData().map { place ->
                userPlaceList.add(
                    RecentSearchListItem(
                        textname = place.name,
                        textAddress = place.address,
                        isFav = place.isFav,
                        latitude = place.lattitude,
                        longitude = place.longitude,
                        rating = place.rating,
                        currentlyOpen = place.currentlyOpen,
                        distance = place.distance
                    )
                )
            }
        }

        return userPlaceList
    }
}