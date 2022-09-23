package de.damien.frontend

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var buSignup: Button
    private lateinit var buLogin: Button
    private lateinit var etName: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "Login"

        buSignup = findViewById<Button>(R.id.buLoginSignup)
        buLogin = findViewById<Button>(R.id.buLoginLogin)
        etName = findViewById<EditText>(R.id.etLoginName)
        etPassword = findViewById<EditText>(R.id.etLoginPassword)

        buSignup.setOnClickListener {
            signup()
        }
        buLogin.setOnClickListener {
            login()
        }

    }

    //make it not possible to get back from login activity
    override fun onBackPressed() {
        Toast.makeText(this, "This action is not permitted", Toast.LENGTH_SHORT).show()
    }

    //TODO remove later
    private fun referenceForLater() {
        val url = Constants.SERVER_URL + "/signup"
        val request: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    "Login failed",
                    Toast.LENGTH_LONG
                ).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = etName.text.toString()
                params["password"] = etPassword.text.toString()
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun signup() {
        try {
            val url = Constants.SERVER_URL + "/signup"
            //String Request initialized
            val request = object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    Toast.makeText(
                        applicationContext,
                        "Signed up Successfully as $response",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }, Response.ErrorListener { error ->
                    Toast.makeText(
                        applicationContext,
                        "Signup failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getBodyContentType(): String {
                    return "application/json"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["name"] = etName.text.toString()
                    params["password"] = etPassword.text.toString()
                    return JSONObject(params as Map<*, *>?).toString().toByteArray()
                }
            }
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        } catch (ex: Exception) {

        }

    }

    private fun login() {
        try {
            val url = Constants.SERVER_URL + "/login"
            //String Request initialized
            val request = object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    Toast.makeText(
                        applicationContext,
                        "Logged In Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i(Constants.TAG, "token received: $response")
                    SessionData.token = response
                    SessionData.name = etName.text.toString()
                    finish()
                }, Response.ErrorListener { error ->
                    Toast.makeText(
                        applicationContext,
                        "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getBodyContentType(): String {
                    return "application/json"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["name"] = etName.text.toString()
                    params["password"] = etPassword.text.toString()
                    return JSONObject(params as Map<*, *>?).toString().toByteArray()
                }
            }
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        } catch (ex: Exception) {

        }
    }


}