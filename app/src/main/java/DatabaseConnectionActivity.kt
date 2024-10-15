package com.example.ergenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.StrictMode
import android.widget.Toast
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_connection)

        // Allow network operations on the main thread (not recommended for production)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val connection = connectToDatabase("localhost", "3306", "er_test_db", "root", "your_password")
        if (connection != null) {
            Toast.makeText(this, "Connected to the database", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to connect to the database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToDatabase(host: String, port: String, dbName: String, user: String, password: String): Connection? {
        var connection: Connection? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connectionURL = "jdbc:mysql://$host:$port/$dbName"
            connection = DriverManager.getConnection(connectionURL, user, password)
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return connection
    }
}