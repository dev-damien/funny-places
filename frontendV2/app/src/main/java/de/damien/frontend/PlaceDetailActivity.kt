package de.damien.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import de.damien.frontend.recyclerviews.comment.Comment
import de.damien.frontend.recyclerviews.comment.CommentAdapter
import de.damien.frontend.recyclerviews.place.Place
import de.damien.frontend.recyclerviews.place.PlaceAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.item_place.view.*
import org.json.JSONObject
import java.lang.Exception

class PlaceDetailActivity : AppCompatActivity() {

    private val commentList = mutableListOf<Comment>()
    private val adapter = CommentAdapter(commentList)
    private var placeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        supportActionBar?.hide()

        rvPlaceComments.adapter = adapter
        rvPlaceComments.layoutManager = LinearLayoutManager(this)

        placeId = intent.getStringExtra("placeId")!!

        getPlace()
        getComments()
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
                    val url = "${Constants.SERVER_URL}/images/$placeId"

                    Glide.with(this).load(url).into(ivPlaceImage)
                    tvPlaceTitleContent.text = response.get("title").toString()
                    tvPlaceDescContent.text = response.get("description").toString()
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
}