package com.example.ergenerator

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreviousERActivity : AppCompatActivity() {

    private lateinit var databaseHelper: MyDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var erDiagramAdapter: ERDiagramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_er)

        // Initialize the database helper
        databaseHelper = MyDatabaseHelper(this)

        // Fetch all ER diagrams from the database
        val erDiagrams = databaseHelper.getAllERDiagrams()

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.er_diagram_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with the context and the list of ER diagrams
        erDiagramAdapter = ERDiagramAdapter(this, erDiagrams)
        recyclerView.adapter = erDiagramAdapter

        // Set an item click listener on the adapter
        erDiagramAdapter.setOnItemClickListener { selectedDiagram ->
            // Display a Toast message when an item is clicked
            Toast.makeText(this, "Selected: ${selectedDiagram.table}", Toast.LENGTH_SHORT).show()
            // Optionally, you can navigate to another activity to display more detailed information about the selected diagram
        }
    }
}
