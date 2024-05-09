package com.example.moduroad.placeAPI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moduroad.R

class PlacesAdapter(private var places: List<Place> = listOf()) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setData(newPlaces: List<Place>) {
        places = newPlaces
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view).apply {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onClick(it, position)
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
            titleTextView.text = place.title
            roadAddressTextView.text = place.roadAddress
            addressTextView.text = place.address
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}
