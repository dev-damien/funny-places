package de.damien.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val name: String? = null
    private val password: String? = null
    private val token: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val buSignup = findViewById<Button>(R.id.buLoginSignup)
        buSignup.setOnClickListener {
            signup()
        }
    }

    private fun signup2() {
        val url = Constants.SERVER_URL + "/signup"
        val request: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    "Login failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = "Neo"
                params["password"] = "redPill123"
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun signup() {
        val jsonObj = JSONObject()
        jsonObj.put("name", "Neo")
        jsonObj.put("password", "redPill123")

        val url = Constants.SERVER_URL + "/signup"
        //String Request initialized
        val request = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                Toast.makeText(applicationContext, "Logged In Successfully as $response", Toast.LENGTH_SHORT)
                    .show()

            }, Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params["name"] = "Neo"
                params["password"] = "redPill123"
                return JSONObject(params as Map<*, *>?).toString().toByteArray()
            }

        }
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }


}