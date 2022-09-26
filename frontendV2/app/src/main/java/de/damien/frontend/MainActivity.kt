package de.damien.frontend

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.location.*
import de.damien.frontend.recyclerviews.place.Place
import de.damien.frontend.recyclerviews.place.PlaceAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101

    private val placeList = mutableListOf<Place>()
    private val adapter = PlaceAdapter(placeList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "FunnyPlaces"

        //pulling for new places
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                getPlaces()
                mainHandler.postDelayed(this, Constants.PULL_DELAY)
            }
        })
        //move map center to selected place
        val mapCenter = Handler(Looper.getMainLooper())
        mapCenter.post(object : Runnable {
            override fun run() {
                if (SessionData.isSelected) {
                    SessionData.isSelected = false
                    updateMapPosition(
                        GeoPoint(
                            SessionData.mapLat,
                            SessionData.mapLon
                        )
                    )
                }
                mapCenter.postDelayed(this, 1000)
            }
        })


        fabAddPlace.setOnClickListener {
            startActivity(Intent(this, AddPlaceActivity::class.java))
        }
        fabCenterMapOnYou.setOnClickListener {
            SessionData.isSelected = true
            SessionData.mapLat = currentLat.toDouble()
            SessionData.mapLon = currentLon.toDouble()
        }
        fabLogout.setOnClickListener {
            logout()
        }

        if (SessionData.token.isBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        getPlaces()

        rvPlaces.adapter = adapter
        rvPlaces.layoutManager = LinearLayoutManager(this)

        if (!Environment.isExternalStorageManager()) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            val uri: Uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val gpsPermissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, gpsPermissions, RECORD_REQUEST_CODE)
        }
    }

    override fun onStart() {
        super.onStart()
        initMap()
        initGps()
    }

    private var map: MapView? = null

    private fun initMap() {
        Log.i("MYTEST", "initializing the map")
        Configuration.getInstance().userAgentValue = applicationContext.packageName
        map = findViewById(R.id.map)
        map!!.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map!!.controller.setZoom(19.0)

        updateMapPosition(GeoPoint(49.9540463, 7.9260000))
    }

    private lateinit var client: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private fun initGps() {
        client = LocationServices.getFusedLocationProviderClient(this)
        client.requestLocationUpdates(
            LocationRequest().setInterval(1000).setPriority(Priority.PRIORITY_HIGH_ACCURACY),
            locationCallback,
            Looper.myLooper()
        )
    }

    var currentLat: String = ""

    var currentLon: String = ""
    var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (it in locationResult.locations) {
                if (currentLat != "%.4f".format(it.latitude) || currentLon != "%.4f".format(
                        it.longitude
                    )
                ) {
                    currentLat = "%.4f".format(it.latitude)
                    currentLon = "%.4f".format(it.longitude)
                    SessionData.latitude = it.latitude
                    SessionData.longitude = it.longitude
                    Log.i(Constants.TAG, "Lat: ${it.latitude}")
                    Log.i(Constants.TAG, "Lon: ${it.longitude}")
                    runOnUiThread {
                        drawCurPosMarker()
                    }
                    //checkFenceStatus(GeoPoint(it.latitude, it.longitude))
                }
            }
        }
    }

    fun updateMapPosition(myPos: GeoPoint) {
        map!!.controller.setCenter(myPos)
        map!!.controller.setZoom(19.0)
    }


    private fun logout() {
        Log.i(Constants.TAG, "Try to logout: user=${SessionData.name}, token=${SessionData.token}")
        val url = Constants.SERVER_URL + "/logout"
        //String Request initialized
        val request = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                Toast.makeText(
                    applicationContext,
                    "Logged Out Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i(Constants.TAG, "user logged out: $response")
                SessionData.token = ""
                SessionData.name = ""
                startActivity(Intent(this, LoginActivity::class.java))

            }, Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    "Logout failed",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getHeaders():  MutableMap<String, String>{
                val params = HashMap<String, String>()
                params["token"] = SessionData.token
                return params
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun getPlaces() {
        try {
            var curPlace: JSONObject
            var curCreator: JSONObject
            var curImage: JSONObject

            val url = Constants.SERVER_URL + "/places"
            //String Request initialized
            val request = object : JsonArrayRequest(
                Method.GET,
                url,
                null,
                Response.Listener { response ->
                    placeList.clear()
                    //Log.i(Constants.TAG, "API call to get all places received: $response")
                    for (i in 0 until response.length()) {
                        curPlace = response.getJSONObject(i)
                        curCreator = curPlace.getJSONObject("creator")
                        curImage = curPlace.getJSONObject("image")

                        placeList.add(
                            Place(
                                curPlace.get("placeId").toString(),
                                curPlace.get("title").toString(),
                                curCreator.get("name").toString(),
                                "null",
                                "${Constants.SERVER_URL}/images/${curImage.get("imageId")}",
                                curPlace.get("latitude").toString().toDouble(),
                                curPlace.get("longitude").toString().toDouble()
                            )
                        )
                    }
                    //Log.i(Constants.TAG, placeList.toString())
                    adapter.notifyDataSetChanged()
                    drawPlaceMarker()
                }, Response.ErrorListener { _ ->
                    Log.i(Constants.TAG, "API call GET /places failed")
                }) {}
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        } catch (ex: Exception) {

        }
    }

    private fun drawPlaceMarker() {
        //draw all places
        for (place in placeList) {
            val marker = Marker(map!!)
            marker.position = GeoPoint(
                place.latitude,
                place.longitude
            )
            map!!.overlays.add(marker)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = resources.getDrawable(org.osmdroid.library.R.drawable.marker_default)
            marker.title = place.title
            marker.snippet = "by ${place.creator}"
        }
    }

    private var markerUserPos: Marker? = null
    private fun drawCurPosMarker() {
        map!!.overlays.remove(markerUserPos)
        markerUserPos = Marker(map!!)
        markerUserPos?.position = GeoPoint(
            currentLat.toDouble(),
            currentLon.toDouble()
        )
        map!!.overlays.add(markerUserPos)
        markerUserPos?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        markerUserPos?.icon = resources.getDrawable(org.osmdroid.library.R.drawable.person)
        markerUserPos?.title = "You"
    }

}