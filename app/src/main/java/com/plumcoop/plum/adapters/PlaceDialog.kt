package com.plumcoop.plum.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.plumcoop.plum.R
import com.plumcoop.plum.utils.downloadFitAndSetImage
import java.util.*


class PlaceDialog(var vHolder : PlacesHolder, var mContext: Context) : Dialog(mContext) {


    private lateinit var tvDialogPlace : TextView
    private lateinit var tvDialogAddress : TextView
    private lateinit var ivDialogImg : ImageView
    private lateinit var googleButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvDialogPlace =  findViewById(R.id.dialog_place)
        tvDialogAddress = findViewById(R.id.dialog_address)
        ivDialogImg = findViewById(R.id.dialog_img)
        googleButton = findViewById(R.id.go_on_google)
        tvDialogAddress.text = vHolder.adressFull
        tvDialogPlace.text = vHolder.name.text.toString()
        vHolder.url?.let { it1 -> ivDialogImg.downloadFitAndSetImage(it1) }

        val splitedPoint = vHolder.point?.split(" ")
        var pos = splitedPoint?.get(0) + ", " + splitedPoint?.get(1)
        Log.d("gettedPos","$pos")
        googleButton.setOnClickListener{
            val uri: String =
                java.lang.String.format(Locale.ENGLISH, "geo:$pos")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            mContext.startActivity(intent)
        }


    }

}