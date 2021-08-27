package com.plumcoop.plum.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.plumcoop.plum.R
import com.plumcoop.plum.activities.MainActivity
import com.plumcoop.plum.adapters.MyRVAdapter
import com.plumcoop.plum.adapters.PlacesHolder
import com.plumcoop.plum.models.DB
import com.plumcoop.plum.models.UserPlaces
import com.plumcoop.plum.models.UserProfile
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var helloTextView: TextView
    private lateinit var db: DB
    private lateinit var user: FirebaseUser
    private lateinit var userProfile: UserProfile
    private lateinit var mAdapter: FirebaseRecyclerAdapter<UserPlaces, PlacesHolder>
    private lateinit var mRefPlaces: DatabaseReference
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var bChangeColumn : Button
    private var spanCount : Int = 2
    private lateinit var loadingPanel : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = (activity as MainActivity).db
        user = (activity as MainActivity).user

        mRecyclerView = requireView().findViewById(R.id.recycler_view)
        helloTextView = view.findViewById(R.id.hello_mesage)
        bChangeColumn = view.findViewById(R.id.change_column)
        loadingPanel = view.findViewById(R.id.loading_panel)
        loadingPanel.visibility = View.VISIBLE

        bChangeColumn.setOnClickListener{
            spanCount = if (spanCount == 2) 1 else 2
            bChangeColumn.setText(if (spanCount == 2) "|" else "| |")
            mRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
        }
        
        db.database.reference.child("users").child(user.uid).get().addOnSuccessListener {
            val userAs = it.getValue(UserProfile::class.java)
            if (userAs != null) {
                userProfile = userAs
                helloTextView.text = getHelloText()
                bChangeColumn.setText(if (spanCount == 2) "|" else "| |")
                mRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
                initRecyclerView()
            }
        }


    }

    private fun getHelloText() : String{
        return when (Calendar.getInstance().get(Calendar.HOUR)) {
            0, 1, 2, 3, 4, 5, 6 -> "Good night ${userProfile.name}!"
            7, 8, 9, 10, 11, 12 -> "Good morning ${userProfile.name}!"
            13, 14, 15, 16, 17, 18, 19 -> "Good afternoon ${userProfile.name}!"
            20, 21, 22, 23, 24 -> "Good evening ${userProfile.name}!"
            else -> "Error of getting time :("
        }
    }

    private fun initRecyclerView() {
        loadingPanel.visibility = View.GONE
        mRefPlaces = db.database.reference.child("user_places").child(user.uid)
        val options = FirebaseRecyclerOptions.Builder<UserPlaces>().setQuery(
            mRefPlaces, UserPlaces::class.java
        ).build()

        mAdapter = MyRVAdapter(options, db, (activity as MainActivity))



        val spacing = 35 // px
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setPadding(spacing, spacing, spacing, spacing)
        mRecyclerView.clipToPadding = false
        mRecyclerView.clipChildren = false

        mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val column = position % spanCount
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        })

        mAdapter.startListening()

    }



}





