package com.example.ergenerator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome) // Ensure this matches your XML file name

        // Transition to MainActivity after a delay
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java) // Ensure this is your next activity
            startActivity(intent)
            finish() // Finish the welcome activity to prevent going back to it
        }, 3000) // Delay in milliseconds (3 seconds)
    }
}
