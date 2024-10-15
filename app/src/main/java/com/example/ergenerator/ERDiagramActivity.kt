package com.example.ergenerator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ERDiagramActivity : AppCompatActivity() {

    private lateinit var erDiagramTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_er_diagram)

        // Find the TextView in the layout
        erDiagramTextView = findViewById(R.id.er_diagram_text_view)


        // Get the ER diagram text passed from MainActivity
        val erDiagramText = intent.getStringExtra("ER_DIAGRAM_TEXT") ?: ""

        // Check if the ER diagram text is empty
        if (erDiagramText.isEmpty()) {
            // Handle the error
            Toast.makeText(this, "No ER diagram text provided", Toast.LENGTH_SHORT).show()
            finish() // Close this activity if no text is provided
            return
        }

        // Set the ER diagram text to the TextView
        erDiagramTextView.text = erDiagramText

        // Back button functionality to return to MainActivity
        findViewById<Button>(R.id.back_button).setOnClickListener {
            finish() // Close this activity and return to the previous one
        }
    }
}

