package com.plumcoop.plum.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plumcoop.plum.R
import com.plumcoop.plum.models.PlaceInfo

class CardAdapter(var cards : ArrayList<PlaceInfo>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView : View,) : RecyclerView.ViewHolder(itemView){

       var namePLace: TextView
       var imagePlace: ImageView
       var addresPlace: TextView

        init{
            namePLace = itemView.findViewById<TextView>(R.id.tvPlace)
            imagePlace = itemView.findViewById<ImageView>(R.id.image_place)
            addresPlace = itemView.findViewById<TextView>(R.id.address_of_place)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {

        holder.itemView.apply{
            holder.namePLace.text = cards[position].name
            holder.addresPlace.text = cards[position].address
            //holder.imagePlace.setImageBitmap(loadImg(cards[position].id))
        }
    }

    override fun getItemCount(): Int = cards.size






}
