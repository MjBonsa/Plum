package com.plumcoop.plum.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.plumcoop.plum.models.DB
import com.plumcoop.plum.R
import com.plumcoop.plum.models.UserProfile
import com.plumcoop.plum.fragments.*
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {

    lateinit var userProfile: UserProfile
    private lateinit var auth: FirebaseAuth
    public lateinit var user : FirebaseUser
    public lateinit var db : DB
    private var settingsFragment = SettingsFragment()
    private var homeFragment = HomeFragment()
    private val FINE_REQUEST_CODE = 1337


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        user = auth.currentUser!!
        db = DB()

        this.window.statusBarColor = ContextCompat.getColor(this,R.color.white)

        loadUserProfile()

        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_settings -> setCurrentFragment(settingsFragment)
                R.id.nav_home -> setCurrentFragment(homeFragment)
                R.id.nav_add -> setupPermissions()
            }
            true
        }


        setCurrentFragment((homeFragment))

    }
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("PermLoc", "Not allowed")
            makeRequest()
        }else{
            startUploadActivity()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            FINE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FINE_REQUEST_CODE -> {
                Log.d("Checkperms","${grantResults.isEmpty()} ${grantResults[0]} ${PackageManager.PERMISSION_DENIED} ${grantResults.toString()}")
                if (grantResults.isEmpty() || (grantResults[0] != PackageManager.PERMISSION_DENIED)) {
                    startUploadActivity()
                } else {
                    Toast.makeText(this,"Ну знаешь, надо было разрешать, теперь только через настройки(",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun startUploadActivity(){
        var intent = Intent(this, AddActivity::class.java).apply {
            putExtra("user",user)
        }
        startActivity(intent)

    }


    private fun loadUserProfile(){
        db.database.reference.child("users").child(user.uid).get().addOnSuccessListener {
            var map = it.getValue(UserProfile::class.java)
            if (map != null) {
                userProfile = map

            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }




    private fun setCurrentFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }


}