package de.damien.frontend

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Transformations.map
import com.google.android.gms.location.*
import de.damien.frontend.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MainActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (AccountData.token.isBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

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
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            ActivityCompat.requestPermissions(this, gpsPermissions, RECORD_REQUEST_CODE)

        }

        initMap()
        initGps()

    }

    var map: MapView? = null

    fun initMap() {
        Configuration.getInstance().userAgentValue = applicationContext.packageName

        map = findViewById(R.id.map)

        map!!.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map!!.controller.setZoom(19.0)

        updateMapPosition(GeoPoint(49.9540463, 7.9260000))
/*-
        val poly = org.osmdroid.views.overlay.Polygon(map!!)
        poly.points = fence.getPoints()

        map!!.overlays.add(poly)
   */
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
            locationResult ?: return
            for (it in locationResult.locations) {

                if (currentLat != "%.4f".format(it.latitude) ||
                    currentLon != "%.4f".format(it.longitude)
                ) {
                    currentLat = "%.4f".format(it.latitude)
                    currentLon = "%.4f".format(it.longitude)
                    Log.i("MYTEST", "Lat: ${it.latitude}")
                    Log.i("MYTEST", "Lon: ${it.longitude}")
                    runOnUiThread {
                        updateMapPosition(GeoPoint(it.latitude, it.longitude))
                    }
                }
                //            checkFenceStatus(GeoPoint(it.latitude, it.longitude))
            }
        }
    }


    fun updateMapPosition(myPos: GeoPoint) {
        map!!.controller.setCenter(myPos)
        map!!.controller.setZoom(19.0)
    }
}