package de.damien.frontend

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import de.damien.frontend.recyclerviews.comment.Comment
import de.damien.frontend.recyclerviews.comment.CommentAdapter
import kotlinx.android.synthetic.main.activity_place_detail.*
import org.json.JSONObject

class PlaceDetailActivity : AppCompatActivity() {

    private val commentList = mutableListOf<Comment>()
    private val adapter = CommentAdapter(commentList, this)
    private var placeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        supportActionBar?.hide()

        placeId = intent.getStringExtra("placeId")!!

        tvPlaceDetailDescContent.movementMethod = ScrollingMovementMethod()

        rvPlaceComments.adapter = adapter
        rvPlaceComments.layoutManager = LinearLayoutManager(this)

        buPlaceDetailAddComment.setOnClickListener {
            showDialog()
        }

        getPlace()

        //pulling for new comments
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                getComments()
                mainHandler.postDelayed(this, Constants.PULL_DELAY)
            }
        })

    }

    private fun getPlace() {
        try {
            var creator: JSONObject
            var image: JSONObject

            val url = Constants.SERVER_URL + "/places/$placeId"
            //String Request initialized
            val request = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener { response ->
                    Log.i(Constants.TAG, "API call to get place by id received: $response")
                    creator = response.getJSONObject("creator")
                    image = response.getJSONObject("image")
                    val url = "${Constants.SERVER_URL}/images/${image.get("imageId")}"

                    Glide.with(this).load(url).into(ivPlaceDetailImage)
                    tvPlaceDetailTitleContent.text = response.get("title").toString()
                    tvPlaceDetailDescContent.text = response.get("description").toString()
                    tvPlaceCreatorContent.text = creator.get("name").toString()
                    tvPlaceLatValue.text = response.get("latitude").toString()
                    tvPlaceLonValue.text = response.get("longitude").toString()
                }, Response.ErrorListener { _ ->
                    Log.i(Constants.TAG, "API call GET /places/{id} failed")
                }) {}
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        } catch (ex: Exception) {

        }
    }

    private fun getComments() {
        try {
            var curComment: JSONObject
            var curCreator: JSONObject

            val url = "${Constants.SERVER_URL}/places/$placeId/comments"
            //String Request initialized
            val request = object : JsonArrayRequest(
                Method.GET,
                url,
                null,
                Response.Listener { response ->
                    commentList.clear()
                    Log.i(Constants.TAG, "API call to get all comments by placeId: $response")
                    for (i in 0 until response.length()) {
                        curComment = response.getJSONObject(i)
                        curCreator = curComment.getJSONObject("writer")

                        commentList.add(
                            Comment(
                                curComment.get("commentId").toString(),
                                curCreator.get("name").toString(),
                                curComment.get("text").toString()
                            )
                        )
                    }
                    Log.i(Constants.TAG, commentList.toString())
                    adapter.notifyDataSetChanged()
                }, Response.ErrorListener { _ ->
                    Log.i(Constants.TAG, "API call GET /places/{id}/comments failed")
                }) {}
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        } catch (ex: Exception) {

        }
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Add a Comment")
        val input = EditText(this)
        input.hint = "Enter Comment"
        input.isSingleLine = false
        input.inputType = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val textInput = input.text.toString()
            if (textInput.isBlank()) {
                Toast.makeText(this, "You have to enter something", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Comment has been added", Toast.LENGTH_SHORT).show()
                addComment(textInput)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun addComment(text: String) {
        Log.i(Constants.TAG, "try to add comment: $text")
        val url = Constants.SERVER_URL + "/comments"
        val request = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                Log.i(Constants.TAG, "POST /comments response: $response")
            }, Response.ErrorListener { error ->
                Log.e(Constants.TAG, "Something failed: $error")
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params["text"] = text
                val jsonRequestBody = JSONObject(params as Map<*, *>?)

                val creatorJson = JSONObject("{name: ${SessionData.name}}")
                jsonRequestBody.put("writer", creatorJson)

                val placeJson = JSONObject("{placeId: $placeId}")
                jsonRequestBody.put("place", placeJson)

                Log.i(Constants.TAG, "POST /comments JSON: $jsonRequestBody")
                return jsonRequestBody.toString().toByteArray()
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}