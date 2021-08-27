package com.plumcoop.plum.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseUser
import com.plumcoop.plum.R
import com.plumcoop.plum.models.DB
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import java.io.ByteArrayOutputStream


class AddActivity : AppCompatActivity(), GeoObjectTapListener, InputListener,
    UserLocationObjectListener, Session.SearchListener{

    private lateinit var searchManager : SearchManager
    private lateinit var db : DB
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var ivPlace : ImageView
    private lateinit var etPlaceName : EditText
    private lateinit var etPlaceAddress : EditText
    private lateinit var bUpload : Button
    private lateinit var loadingPanel : ProgressBar
    private lateinit var user : FirebaseUser
    private lateinit var mapView : MapView
    private var lastSelectedPoint: String ?= null
    private lateinit var userLocationLayer: UserLocationLayer
    private var searchSession: Session? = null
    private var flagChecked : Boolean = false
    private lateinit var tvTapHere : TextView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        this.window.statusBarColor = ContextCompat.getColor(this,R.color.white)

        user = intent.getParcelableExtra("user")!!
        db = DB()
        ivPlace = findViewById(R.id.add_image_view)
        etPlaceAddress = findViewById(R.id.address_of_place)
        etPlaceName = findViewById(R.id.name_place)
        bUpload = findViewById(R.id.upload_place)
        loadingPanel = findViewById(R.id.loadingPanel)
        mapView = findViewById(R.id.mapview)
        mapView.map.addTapListener(this)
        mapView.map.addInputListener(this)
        tvTapHere = findViewById(R.id.tap_here)
        tvTapHere.visibility = View.VISIBLE

        initClickListeners()





        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)

        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true

        userLocationLayer.setObjectListener(this)


    }


    override fun onSearchResponse(response: Response) {

        val firstName = response.collection.children[0].obj!!.descriptionText.toString()
        val lastName = (response.collection.children[0].obj!!.name.toString())

        if (firstName != "null" &&  lastName != "null"){
            etPlaceAddress.setText("$firstName, $lastName")
        }else{
            if (firstName != "null"){
                etPlaceAddress.setText(firstName)
            }
            else{
                etPlaceAddress.setText(lastName)
            }
        }
        Log.d("checkresult", "$firstName $lastName")
    }
    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    override fun onSearchError(p0: Error) {
        Log.d("ErrorSearch",p0.toString())
    }

    private fun checkFields() : Boolean{
        val isNameFilled = etPlaceName.text.toString().isNotEmpty()
        val isAddressFilled = etPlaceAddress.text.toString().isNotEmpty()
        val isImageFilled = ivPlace.drawable != null
        val isPointField = lastSelectedPoint != null
        return isAddressFilled && isImageFilled && isNameFilled && isPointField
    }

    private fun initClickListeners(){
        bUpload.setOnClickListener {
            if (checkFields()){
                loadingPanel.visibility = View.VISIBLE
                uploadPlace()
            }else{
                Toast.makeText(this,"Не все данные введены",Toast.LENGTH_SHORT).show()
            }

        }

        ivPlace.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery,pickImage)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            ivPlace.setImageURI(imageUri)
            tvTapHere.visibility = View.GONE

        }
    }

    private fun uploadPlace(){
        val id = db.database.reference.child("places").push().key

        if (id != null){
            val imgRef = db.storage.reference.child("images/$id.jpg")
            val bitmap = (ivPlace.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val uploadTask = imgRef.putBytes(baos.toByteArray())

            val refPlace = db.database.reference.child("places").child(id)
            val refUserPlaces = db.database.reference.child("user_places").child(user.uid)


            refPlace.child("name").setValue(etPlaceName.text.toString()).addOnFailureListener {
                failedLoad()
            }
            refPlace.child("address").setValue(etPlaceAddress.text.toString()).addOnFailureListener {
                failedLoad()
            }
            refPlace.child("point").setValue(lastSelectedPoint).addOnFailureListener {
                failedLoad()
            }

            uploadTask.addOnFailureListener {
                failedLoad()
            }.addOnSuccessListener {
                imgRef.downloadUrl.addOnSuccessListener {
                    refPlace.child("url").setValue(it.toString()).addOnSuccessListener {
                        refUserPlaces.child("$id").child("id").setValue("$id").addOnFailureListener{
                            failedLoad()
                        }
                        loadingPanel.visibility = View.GONE
                        finish()
                    }.addOnFailureListener{
                            failedLoad()
                    }
                }.addOnFailureListener{
                    failedLoad()
                }
            }


        }
        else{
            failedLoad()
        }

    }

    private fun failedLoad(){
        Toast.makeText(this, "Can`t upload\ncheck your internet connection", Toast.LENGTH_SHORT)
            .show()
         loadingPanel.visibility = View.GONE
    }
    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        Log.d(
            "onObjectTap", "Point: " + geoObjectTapEvent.geoObject.geometry[0].point!!
                .longitude
        )
        return true
    }

    override fun onMapTap(map: Map, point: Point) {}

    override fun onMapLongTap(map: Map, point: Point) {
        Log.d("LongClickPoint", "Point: " + point.latitude + " " + point.longitude)
        lastSelectedPoint = "${point.latitude} ${point.longitude}"
        submitQuery(lastSelectedPoint!!)
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()
        mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(this, R.drawable.search_layer_pin_selected_default)
        )
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    override fun onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {

        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {}

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer.setAnchor(
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat()),
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.83).toFloat())
        )
        if (!flagChecked){
            userLocationLayer.isAutoZoomEnabled = true
            flagChecked = true
            userLocationLayer.isAutoZoomEnabled = false
        }

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                this, R.drawable.search_layer_pin_dust_default
            )
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
        Log.d("UserPoint",userLocationView.arrow.geometry.latitude.toString() + " " +
                userLocationView.arrow.geometry.longitude.toString())
        Log.d("CameraPos", userLocationLayer.cameraPosition().toString())
        Log.d("UserPoint",userLocationView.pin.geometry.latitude.toString()  + " " + userLocationView.pin.geometry.longitude.toString())

    }


}