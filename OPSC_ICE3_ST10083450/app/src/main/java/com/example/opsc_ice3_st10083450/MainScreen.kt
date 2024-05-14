package com.example.opsc_ice3_st10083450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class MainScreen : AppCompatActivity() {
    private lateinit var addBtn: Button
    private lateinit var backBtn: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        recyclerView = findViewById(R.id.recyclerViewProfile)
        addBtn = findViewById(R.id.btnAddProfile)

        addBtn.setOnClickListener{
            var Intent = Intent(this, CreateProfileScreen::class.java)
            startActivity(Intent)
        }

    }
}