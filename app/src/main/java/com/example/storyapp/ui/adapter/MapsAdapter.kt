package com.example.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.di.LocationConverter
import com.example.storyapp.remote.response.ListStoryItem
import com.google.android.gms.maps.model.LatLng

class MapsAdapter (private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<MapsAdapter.MapsViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsViewHolder {
        val view=
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_maps, parent, false)
        return MapsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapsViewHolder, position: Int) {
        val data = listStory[position]
        val latlng: LatLng? = LocationConverter.toLatlng(data.lat, data.lon)
        holder.iconLocation.visibility =
            if (latlng != null) View.VISIBLE else View.GONE
        holder.tvName.text = data.name
        Glide.with(holder.itemView)
            .load(data.photoUrl)
            .into(holder.imgPhoto)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.bindingAdapterPosition])
        }
    }

    class MapsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgPhoto: ImageView = view.findViewById(R.id.img_photo)
        var tvName: TextView = view.findViewById(R.id.tv_name)
        var iconLocation: ImageView = view.findViewById(R.id.icon_location_available)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    override fun getItemCount(): Int = listStory.size
}