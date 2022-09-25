package de.damien.frontend.recyclerviews.comment

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import de.damien.frontend.Constants
import de.damien.frontend.R
import de.damien.frontend.SessionData
import de.damien.frontend.VolleySingleton
import kotlinx.android.synthetic.main.item_comment.view.*
import org.json.JSONObject
import kotlin.coroutines.coroutineContext

class CommentAdapter(
    var comments: List<Comment>,
    val context: Context
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.itemView.apply {
            val writer = comments[position].writer
            val id = comments[position].id
            val text = comments[position].text
            tvCommentWriter.text = "$writer:"
            tvCommentText.text = text

            if (writer == SessionData.name) {
                //your own comment
                ivCommentEdit.visibility = View.VISIBLE
                ivCommentEdit.setOnClickListener {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Edit comment")
                    val input = EditText(context)
                    input.setText(text)
                    input.hint = "Enter Comment"
                    input.isSingleLine = false
                    input.inputType = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                    builder.setView(input)

                    builder.setPositiveButton("OK") { dialog, _ ->
                        val textInput = input.text.toString()
                        if (textInput == text) {
                            dialog.dismiss()
                        }
                        if (textInput.isBlank()) {
                            Toast.makeText(
                                context,
                                "You have to enter something",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Comment has been edited",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            editComment(id, textInput)
                        }
                    }
                    builder.setNegativeButton("Cancel") { dialog, which ->
                        dialog.cancel()
                    }
                    builder.show()

                }

            }
        }
    }

    private fun editComment(id: String, textInput: String) {
        Log.i(Constants.TAG, "try to edit comment with id: $id")
        val url = Constants.SERVER_URL + "/comments/$id"
        val request = object : StringRequest(
            Method.PATCH,
            url,
            Response.Listener { response ->
                Log.i(Constants.TAG, "PATCH /comments/$id response: $response")
            }, Response.ErrorListener { error ->
                Log.e(Constants.TAG, "Something failed: $error")
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params["text"] = textInput
                val jsonRequestBody = JSONObject(params as Map<*, *>?)
                Log.i(Constants.TAG, "PATCH /comments/$id JSON: $jsonRequestBody")
                return jsonRequestBody.toString().toByteArray()
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(request)

    }

}