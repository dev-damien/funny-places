package de.damien.frontend.recyclerviews.place

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.damien.frontend.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_place.view.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class PlaceAdapter(
    var places: List<Place>
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.itemView.apply {
            tvItemTitel.text = places[position].title
            tvItemCreator.text = "by ${places[position].creator}"
            Glide.with(holder.itemView.context).load(places[position].imageUrl).into(ivItemPlace)
            this.setOnClickListener {
                val placeId = places[position].id
                Log.i(Constants.TAG, "clicked on place with id $placeId")
                SessionData.isSelected = true
                SessionData.mapLat = places[position].latitude
                SessionData.mapLon = places[position].longitude
                Log.i(
                    Constants.TAG,
                    "new map center: lat=${SessionData.mapLat}, lon=${SessionData.mapLon}"
                )

            }
            ivItemPlaceInfo.setOnClickListener {
                val placeId = places[position].id
                Log.i(Constants.TAG, "clicked on info for place with id $placeId")
                val intent = Intent(holder.itemView.context, PlaceDetailActivity::class.java)
                intent.putExtra("placeId", placeId)
                holder.itemView.context.startActivity(intent)
            }
            //TODO change field color depending on isSelected attribute
        }
    }


}