package de.damien.frontend

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_add_place.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class AddPlaceActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 102

    var imageData: ByteArray? = null
    var imageId = -1

    var title = ""
    var description = ""
    var creator: JSONObject? = null
    var lat: Double? = null
    var lon: Double? = null

    companion object {
        private const val IMAGE_PICK_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        supportActionBar?.title = "Add Place"

        ivAddPlaceImage.setOnClickListener {
            openGallery()
        }

        buAddPlaceFillCoords.setOnClickListener {
            enterLastLocation()
        }

        buAddPlaceDone.setOnClickListener {
            if (etAddPlaceTitleInput.text.isBlank()) {
                Toast.makeText(this, "You have to enter a title", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (etAddPlaceDescInput.text.isBlank()) {
                Toast.makeText(this, "You have to enter a description", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (etAddPlaceLatInput.text.isBlank() || etAddPlaceLonInput.text.isBlank()) {
                Toast.makeText(this, "You have to enter latitude  and longitude", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            if (imageData == null) {
                Toast.makeText(this, "You have to select an image", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            //all attributes are set
            title = etAddPlaceTitleInput.text.toString()
            description = etAddPlaceDescInput.text.toString().trim()
            try {
                lat = etAddPlaceLatInput.text.toString().toDouble()
                lon = etAddPlaceLonInput.text.toString().toDouble()
            } catch (ex: NumberFormatException) {
                Toast.makeText(this, "Input for lat/lon invalid", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            addPlaceAndImage()
        }
    }

    private fun enterLastLocation() {
        etAddPlaceLatInput.setText(SessionData.latitude.toString())
        etAddPlaceLonInput.setText(SessionData.longitude.toString())
    }

    private fun postPlace() {
        val url = Constants.SERVER_URL + "/places"
        //String Request initialized
        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            null,
            Response.Listener { response ->
                Log.i(Constants.TAG, "POST /places response: $response")
                Toast.makeText(this, "Place \"$title\" has been added", Toast.LENGTH_LONG).show()
                finish()
            }, Response.ErrorListener { error ->
                Log.i(Constants.TAG, "POST /places threw error")
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params["title"] = title
                params["description"] = description
                params["latitude"] = lat.toString()
                params["longitude"] = lon.toString()
                val jsonRequestBody = JSONObject(params as Map<*, *>?)

                creator = JSONObject("{name: ${SessionData.name}}")
                jsonRequestBody.put("creator", creator)

                val imageJson = JSONObject("{imageId: $imageId}")
                jsonRequestBody.put("image", imageJson)

                Log.i(Constants.TAG, "POST /places JSON: $jsonRequestBody")
                return jsonRequestBody.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String>? {
                val params: HashMap<String, String> = HashMap()
                params["token"] = SessionData.token
                return params
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun openGallery() {
        Log.i(Constants.TAG, "try to open gallery")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun addPlaceAndImage() {
        Log.i(Constants.TAG, "try to upload image")
        imageData ?: return
        val url = "${Constants.SERVER_URL}/images"
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                imageId = String(response.data).toInt()
                Log.i(Constants.TAG, "upload image was successful with id=$imageId")
                postPlace()
            },
            Response.ErrorListener {
                Log.i(Constants.TAG, "upload image failed")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                params["image"] = FileDataPart("imagePlace", imageData!!, "jpeg")
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params: HashMap<String, String> = HashMap()
                params["token"] = SessionData.token
                return params
            }
        }
        Log.i(Constants.TAG, "upload request added to volley-queue")
        Volley.newRequestQueue(this).add(request)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(Constants.TAG, "Returned from other activity with result")
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            Log.i(Constants.TAG, "-> from imagePicker with uri = $uri")
            if (uri != null) {
                getEXIFDataFromImage(uri)
                ivAddPlaceImage.setImageURI(uri)
                createImageData(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getEXIFDataFromImage(uri: Uri) {
        try {
            Log.i(Constants.TAG, "read exif data of image")
            val inputStream = contentResolver.openInputStream(uri)!!
            val exifInterface = ExifInterface(inputStream)

            val lat = exifInterface.latLong?.get(0)
            val lon = exifInterface.latLong?.get(1)
            var exif =
                "\nIMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
            exif += "\nIMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
            exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE)
            exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL)
            exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION)
            exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE)
            exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH)
            exif += "\nGPS related:"
            exif += "\n TAG_GPS_DATESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)
            exif += "\n TAG_GPS_TIMESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
            exif += "\n TAG_GPS_LATITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            exif += "\n TAG_GPS_LATITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
            exif += "\n TAG_GPS_LONGITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
            exif += "\n TAG_GPS_LONGITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
            exif += "\n TAG_GPS_PROCESSING_METHOD: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)
            Log.i(Constants.TAG, exif)

            val isLatByHand = etAddPlaceLatInput.text.toString().isNotBlank()
            val isLonByHand = etAddPlaceLatInput.text.toString().isNotBlank()
            if (lat == null || lon == null) {
                Log.i(Constants.TAG, "Image contained no coordinates")
                return
            }
            if (isLatByHand || isLonByHand) {
                Log.i(Constants.TAG, "Coordinates already set by hand")
                return
            }
            Log.i(Constants.TAG, "exif-data: lat=$lat, long=$lon")
            etAddPlaceLatInput.setText(lat.toString())
            etAddPlaceLonInput.setText(lon.toString())
        } catch (ex: Exception) {
            Log.e(Constants.TAG, ex.toString())
        }
    }
}