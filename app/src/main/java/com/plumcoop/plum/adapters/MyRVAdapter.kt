package com.plumcoop.plum.adapters

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.plumcoop.plum.R
import com.plumcoop.plum.models.DB
import com.plumcoop.plum.models.PlaceInfo
import com.plumcoop.plum.models.UserPlaces
import com.plumcoop.plum.utils.AppValueEventListener
import com.plumcoop.plum.utils.downloadAndSetImage
import com.plumcoop.plum.utils.downloadFitAndSetImage
import java.util.*


class MyRVAdapter(
    options: FirebaseRecyclerOptions<UserPlaces>,
    private var db : DB,
    private var mContext : Context) : FirebaseRecyclerAdapter<UserPlaces, PlacesHolder>(options) {


    private lateinit var dialog : Dialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)




        var vHolder = PlacesHolder(view)
        vHolder.cardView.setOnClickListener{
            makeDialog(vHolder,mContext)
        }

        return vHolder

    }



    override fun onBindViewHolder(holder: PlacesHolder, position: Int, model: UserPlaces) {
        val mRefPlaceById = db.database.reference.child("places").child(model.id)
        var listenerPlaces = AppValueEventListener{
            val place = it.getValue(PlaceInfo::class.java) ?: PlaceInfo()
            holder.name.text = place.name
            val address = place.address
            holder.adressFull = address
            if (address.length > 21){
                holder.address.text = address.substring(0,20) + "..."
            }else{
                holder.address.text = address
            }
            holder.photo.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.photo.downloadAndSetImage(place.url)
            holder.url = place.url
            holder.point = place.point

        }

        mRefPlaceById.addValueEventListener(listenerPlaces)

    }

    private fun makeDialog(vHolder : PlacesHolder, mContext: Context){

        dialog = PlaceDialog(vHolder,mContext)
        dialog.setContentView(R.layout.fragment_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT)
        dialog.show()
    }

}

