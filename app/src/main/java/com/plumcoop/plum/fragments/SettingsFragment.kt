package com.plumcoop.plum.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.plumcoop.plum.R
import com.plumcoop.plum.activities.MainActivity
import com.plumcoop.plum.activities.SignIn

class SettingsFragment : Fragment(R.layout.fragment_settings)  {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private lateinit var bLogOut : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bLogOut = view.findViewById(R.id.exit_buttoo)
        bLogOut.setOnClickListener {
            Firebase.auth.signOut()
            var intent = Intent(activity, SignIn::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}