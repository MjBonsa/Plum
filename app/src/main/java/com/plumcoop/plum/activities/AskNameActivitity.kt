package com.plumcoop.plum.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import com.plumcoop.plum.models.DB
import com.plumcoop.plum.R


class AskNameActivitity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    private lateinit var userNameTextView : EditText
    private lateinit var confirmUserNameButton : Button
    private lateinit var welcomeTextView: TextView
    private var isButtonView : Boolean = false
    private lateinit var db : DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_main)


        user = intent.getParcelableExtra("user")!!
        userNameTextView = findViewById(R.id.user_name)
        confirmUserNameButton = findViewById(R.id.confirm_user_name)
        welcomeTextView = findViewById(R.id.welcome_to_plum)

        onCreateAnimation()
        userNameTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!isButtonView) {
                    confirmUserNameButton.animate().alpha(1F).setDuration(1500)
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        confirmUserNameButton.setOnClickListener(){
            if (userNameTextView.text.toString().length > 0){
                db = DB()
                val name = userNameTextView.text.toString()
                val refUserDatabase = db.database.reference.child("users").child(user.uid)
                refUserDatabase.child("name").setValue(name)
                //refUserDatabase.child("phone_number").setValue(user.phoneNumber.toString())
                updateUI()

            }
        }

    }

    private fun updateUI() {
        val intent = Intent(this, MainActivity::class.java).apply {

        }
        startActivity(intent)
        finish();
    }
    private fun onCreateAnimation(){
        userNameTextView.animate().alpha(1F).setDuration(1500)
        welcomeTextView.animate().alpha(1F).setDuration(1500)
    }
}