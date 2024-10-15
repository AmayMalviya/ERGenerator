package com.example.ergenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.ERDiagramData

class ERDiagramListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var erDiagramAdapter: ERDiagramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_er_diagram_list)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch ER diagrams from database
        val erDiagrams = getAllERDiagrams() // Using the method from MyDatabaseHelper class

        // Initialize adapter and set it to the RecyclerView
        erDiagramAdapter = ERDiagramAdapter(this, erDiagrams) // Pass the context
        recyclerView.adapter = erDiagramAdapter
    }

    // Use getAllERDiagrams from MyDatabaseHelper
    private fun getAllERDiagrams(): List<ERDiagramData> {
        val databaseHelper = MyDatabaseHelper(this) // Create instance of MyDatabaseHelper directly
        return databaseHelper.getAllERDiagrams()
    }
}
