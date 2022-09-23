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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.gms.location.*
import de.damien.frontend.recyclerviews.place.Place
import de.damien.frontend.recyclerviews.place.PlaceAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101

    private val placeList = mutableListOf<Place>()
    private val adapter = PlaceAdapter(placeList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "FunnyPlaces"

//        val mainHandler = Handler(Looper.getMainLooper())
//        mainHandler.post(object : Runnable {
//            override fun run() {
//                getPlaces()
//                mainHandler.postDelayed(this, Constants.PULL_DELAY)
//            }
//        })

        fabAddPlace.setOnClickListener {
            startActivity(Intent(this, AddPlaceActivity::class.java))
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
    fun initMap() {
        Log.i("MYTEST", "initializing the map")
        Configuration.getInstance().userAgentValue = applicationContext.packageName
        map = findViewById(R.id.map)
        map!!.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map!!.controller.setZoom(19.0)

        updateMapPosition(GeoPoint(49.9540463, 7.9260000))

        //draw neutral zone
//        val poly = org.osmdroid.views.overlay.Polygon(map!!)
//        poly.points = fence2
//        map!!.overlays.add(poly)
//
//        val poly2 = org.osmdroid.views.overlay.Polygon(map!!)
//        poly2.points = fence
//        map!!.overlays.add(poly2)

    }

    lateinit var client: FusedLocationProviderClient

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
                    Log.i("MYTEST", "Lat: ${it.latitude}")
                    Log.i("MYTEST", "Lon: ${it.longitude}")
                    runOnUiThread {
                        updateMapPosition(GeoPoint(it.latitude, it.longitude))
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
                    Log.i(Constants.TAG, "API call to get all places received: $response")
                    for (i in 0 until response.length()) {
                        curPlace = response.getJSONObject(i)
                        curCreator = curPlace.getJSONObject("creator")
                        curImage = curPlace.getJSONObject("image")

                        placeList.add(
                            Place(
                                curPlace.get("placeId").toString(),
                                curPlace.get("title").toString(),
                                curCreator.get("name").toString(),
                                "${Constants.SERVER_URL}/images/${curImage.get("imageId")}",
                                curPlace.get("latitude").toString().toDouble(),
                                curPlace.get("longitude").toString().toDouble()
                            )
                        )
                    }
                    Log.i(Constants.TAG, placeList.toString())
                    adapter.notifyDataSetChanged()
                }, Response.ErrorListener { _ ->
                    Log.i(Constants.TAG, "API call GET /places failed")
                }) {}
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        } catch (ex: Exception) {

        }
    }
}