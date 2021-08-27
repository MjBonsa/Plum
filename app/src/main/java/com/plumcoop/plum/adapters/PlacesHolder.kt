package com.plumcoop.plum.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.plumcoop.plum.R


class PlacesHolder(view: View) : RecyclerView.ViewHolder(view) {
    var adressFull: String ?= null
    val cardView: CardView = view.findViewById<CardView>(R.id.card_view)
    val name: TextView = view.findViewById(R.id.tvPlace)
    val address: TextView = view.findViewById(R.id.address_of_place)
    val photo: ImageView = view.findViewById(R.id.image_place)
    var url : String? = null
    var point : String? = null




}
