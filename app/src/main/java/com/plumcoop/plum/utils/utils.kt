package com.plumcoop.plum.utils

import android.widget.ImageView
import com.google.firebase.database.DataSnapshot
import com.plumcoop.plum.R
import com.squareup.picasso.Picasso


/* Also we need use .fetch() to save imgs in cache*/

fun ImageView.downloadAndSetImage(url: String) {
    /* Функция раширения ImageView, скачивает и устанавливает картинку*/
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.brokes)
        .into(this)

}
fun ImageView.downloadFitAndSetImage(url: String) {
    Picasso.get()
        .load(url)
        .fit()
        .centerCrop()
        .placeholder(R.drawable.brokes)
        .into(this)
}
