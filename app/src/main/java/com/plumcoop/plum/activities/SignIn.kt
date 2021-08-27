package com.plumcoop.plum.activities

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.plumcoop.plum.R
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory
import java.util.concurrent.TimeUnit



class SignIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var phoneTextView: EditText
    private lateinit var buttonSendCode: Button
    private lateinit var textAskingEnterPhone: TextView

    private lateinit var codeEditText: EditText


    override fun onStart() {
        super.onStart()


        val currentUser = auth.currentUser
        Log.d("LoginInfo","Current User : $currentUser")
        if (currentUser != null){
            updateUI()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        auth = Firebase.auth


        Log.d("db check", "seted")
        codeEditText = findViewById(R.id.editCode)
        textAskingEnterPhone = findViewById(R.id.text_to_get_code)
        phoneTextView = findViewById(R.id.editTextPhone)
        phoneTextView.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        buttonSendCode = findViewById(R.id.send_code)


        loginWithPhone()


    }


    private fun updateUI(){
        MapKitFactory.setApiKey("Your api key")
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)

        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
        finish()
    }

    private fun loginWithPhone(){
        val callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Log.d("Super","logged")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.d("Super", "not logged")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onSendCodeAnimation()
                buttonSendCode.setOnClickListener {
                    val credential = PhoneAuthProvider.getCredential(
                        verificationId,
                        codeEditText.text.toString()
                    )
                    signInWithPhoneAuthCredential(credential)
                }

            }
        }

        buttonSendCode.setOnClickListener {
            val phoneNumber = phoneTextView.text.toString()
            Log.d("EnteredPhone", phoneNumber)
            if (checkCorrectOfNumberPhone(phoneNumber)) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setCallbacks(callbacks)// OnVerificationStateChangedCallbacks
                    .setActivity(this@SignIn)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                Toast.makeText(this@SignIn, "INVALID PHONE", Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Super", "signInWithCredential:success")
                    val user = task.result?.user
                    if (user != null && user.displayName == null) {
                        Log.d("Super","name : "+user.displayName.toString())
                        updateUIFirstLogin(user)
                    }
                    else{
                        updateUI()
                    }


                } else {
                    Log.w("Super", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(applicationContext,"Код неправельный",Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun updateUIFirstLogin(user: FirebaseUser) {
        val intent = Intent(this, AskNameActivitity::class.java).apply{
            putExtra("user", user)
        }
        startActivity(intent)
        finish()
    }



    private fun onSendCodeAnimation(){
        buttonSendCode.text = "CONFIRM"
        textAskingEnterPhone.text = "Enter your code"
        phoneTextView.setText("")
        phoneTextView.alpha = 0f
        codeEditText.alpha = 1f
    }



    private fun checkCorrectOfNumberPhone(number : String) : Boolean{
        val numberClear = number.replace("\\D+".toRegex(),"")

        //Log.d("enter","changed number $numberClear")

        return numberClear.matches("^[+]?[0-9]{10,13}\$".toRegex())
    }





}