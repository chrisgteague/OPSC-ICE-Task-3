package com.example.opsc_ice3_st10083450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class MainScreen : AppCompatActivity() {
    private lateinit var addBtn: Button
    private lateinit var backBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var profileList: MutableList<ProfileModel>
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var noEntriesTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        noEntriesTextView = findViewById(R.id.textViewNoEntries)
        recyclerView = findViewById(R.id.recyclerViewProfile)
        addBtn = findViewById(R.id.btnAddProfile)

        profileList = mutableListOf()
        profileAdapter = ProfileAdapter(profileList)

        recyclerView.adapter = profileAdapter


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = profileAdapter
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserID != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserID)



            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    profileList.clear()


                    val profile = dataSnapshot.getValue(ProfileModel::class.java)
                    if (profile != null) {

                        profileList.add(profile)
                    }


                    profileAdapter.notifyDataSetChanged()


                    if (profileList.isNotEmpty()) {
                        noEntriesTextView.visibility = View.GONE
                    } else {
                        noEntriesTextView.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MainScreen, "Failed to retrieve data", Toast.LENGTH_LONG).show()
                }
            })

            addBtn.setOnClickListener {
                var Intent = Intent(this, CreateProfileScreen::class.java)
                startActivity(Intent)
            }

        }
        }

        inner class ProfileAdapter(private val profiles: List<ProfileModel>) :
            RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
            inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val username: TextView = itemView.findViewById(R.id.tvUsername)
                val qualification: TextView = itemView.findViewById(R.id.tvQualification)
                val studentNumber: TextView = itemView.findViewById(R.id.tvStudentNumber)
                val imageDisplay: ImageView = itemView.findViewById(R.id.ivDisplayImg)

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_profile, parent, false)
                return ProfileViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
                val profile = profiles[position]
                holder.username.text = profile.user_Username
                holder.qualification.text = profile.user_Qualification
                holder.studentNumber.text = profile.user_StudentNumber
                Picasso.get().load(profile.image_Url).into(holder.imageDisplay)
            }

            override fun getItemCount() = profiles.size
        }

}