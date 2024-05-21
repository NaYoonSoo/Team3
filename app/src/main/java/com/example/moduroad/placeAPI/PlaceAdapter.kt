package com.example.moduroad.placeAPI


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moduroad.R
interface PlaceDataHandler {
    fun setData(newPlaces: List<Place>)
    fun addData(newPlaces: List<Place>)
}
class PlacesAdapter(
    private var places: MutableList<Place> = mutableListOf(),
    private val onPlaceSelected: (Place) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>(), PlaceDataHandler {

    override fun setData(newPlaces: List<Place>) {
        places.clear()
        places.addAll(newPlaces)
        notifyDataSetChanged()
    }

    override fun addData(newPlaces: List<Place>) {
        val startPosition = places.size
        places.addAll(newPlaces)
        notifyItemRangeInserted(startPosition, newPlaces.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view).apply {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPlaceSelected(places[position])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_list_name)
        private val roadAddressTextView: TextView = itemView.findViewById(R.id.tv_list_road)
        private val addressTextView: TextView = itemView.findViewById(R.id.tv_list_address)

        fun bind(place: Place) {
            titleTextView.text = place.title // 장소명
            roadAddressTextView.text = place.roadAddress // 도로명
            addressTextView.text = place.address // 지번
        }
    }
}