package com.bangkit.storyapp.ui

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.R
import com.bangkit.storyapp.api.ApiConfig
import com.bangkit.storyapp.response.StoryListResponse

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bangkit.storyapp.databinding.ActivityMapsBinding
import com.bangkit.storyapp.data.AppDataStore
import com.bangkit.storyapp.data.AuthViewModel
import com.bangkit.storyapp.data.ViewModelFactory
import com.bangkit.storyapp.data.StoryList
import com.bangkit.storyapp.response.StoryListResponseItem
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val pref = AppDataStore.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        authViewModel.loginToken().observe(this) { token: String? ->
            getAllStories(token)
        }

        supportActionBar?.title = resources.getString(R.string.locate_user)
    }

    private fun getAllStories(token: String?) {

        val bearerToken = HashMap<String, String>()
        bearerToken["Authorization"] = "Bearer $token"

        val client = ApiConfig.getApiService().getLocation(bearerToken)
        client.enqueue(object : Callback, retrofit2.Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        getStory(responseBody.listStory)
                    }
                } else {
                    Log.e(this@MapsActivity.toString(), "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                Log.e(this@MapsActivity.toString(), "onFailure: ${t.message}")
            }
        })
    }

    private fun getStory(listStory: List<StoryListResponseItem>) {

        val storyList = ArrayList<StoryList>()

        for (item in listStory) {
            storyList.add(
                StoryList(
                    item.photoUrl,
                    item.name,
                    item.description,
                    item.lat.toString(),
                    item.lon.toString()
                )
            )
        }
        addMarkers(storyList)
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addMarkers(storyList: java.util.ArrayList<StoryList>) {
        storyList.forEach { stories ->
            val latLng = LatLng(stories.lat!!.toDouble(), stories.lon!!.toDouble())
            mMap.addMarker(
                MarkerOptions().position(latLng).title(stories.name).snippet(stories.description)
            )
            boundsBuilder.include(latLng)
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Cannot find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        getMyLocation()
        setMapStyle()
    }
}