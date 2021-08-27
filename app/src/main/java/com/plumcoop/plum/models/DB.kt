package com.plumcoop.plum.models

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.storage.ktx.storage
import com.plumcoop.plum.models.PlaceInfo
import java.io.ByteArrayOutputStream


class DB(){
    val user = Firebase.auth
    private val url = "https://plum-back-default-rtdb.europe-west1.firebasedatabase.app/"
    val database = Firebase.database(url)
    val storage = Firebase.storage

}