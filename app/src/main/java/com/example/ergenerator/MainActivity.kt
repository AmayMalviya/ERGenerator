package com.example.ergenerator

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import network.ERAttribute
import network.ERDiagramData
import network.RetrofitInstance

class MainActivity : AppCompatActivity() {

    private lateinit var schemaInput: EditText
    private lateinit var outputTextView: TextView
    private lateinit var apiUrlEditText: EditText
    private lateinit var databaseHelper: MyDatabaseHelper
    private val STORAGE_PERMISSION_CODE = 100
    private val PICK_FILE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        schemaInput = findViewById(R.id.schema_input)
        outputTextView = findViewById(R.id.output_text_view)
        apiUrlEditText = findViewById(R.id.api_url)
        val usernameInput = findViewById<EditText>(R.id.db_username)
        val passwordInput = findViewById<EditText>(R.id.db_password)
        val connectButton = findViewById<Button>(R.id.connect_button)
        val drawERButton = findViewById<Button>(R.id.draw_er_button) // New Draw ER button


        // Initialize database helper
        databaseHelper = MyDatabaseHelper(this)

        // Set default API URL
        apiUrlEditText.setText("http://10.0.2.2:3000/api/connect")

        // Check for storage permissions
        if (checkPermission()) {
            loadSchemaFile()
        } else {
            requestPermission()
        }

        // Button to connect to the database
        connectButton.setOnClickListener {
            val apiUrl = apiUrlEditText.text.toString().trim()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (apiUrl.isNotEmpty()) {
                connectToDatabase(apiUrl, username, password)
            } else {
                Toast.makeText(this, "Please enter the API URL", Toast.LENGTH_SHORT).show()
            }
        }

        drawERButton.setOnClickListener {
            executeAndDrawERDiagram()
        }
        // Button to execute schema (Send schema to API and generate ER Diagram)
        findViewById<Button>(R.id.execute_schema_button).setOnClickListener {
            val schema = schemaInput.text.toString()
            if (schema.isNotEmpty()) {
                sendSchemaToApi(schema)  // Send schema to the API for ER Diagram generation
            } else {
                Toast.makeText(this, "Please enter schema information", Toast.LENGTH_SHORT).show()
            }
        }

        // Button to load schema from a file
        findViewById<Button>(R.id.load_schema_button).setOnClickListener {
            loadSchemaFile()
        }

        // Button to view ER Diagrams stored in the local database
        findViewById<Button>(R.id.view_stored_diagrams_button).setOnClickListener {
            showStoredERDiagrams()
        }
    }



    private fun executeAndDrawERDiagram() {
        lifecycleScope.launch {
            try {
                val erDiagrams = databaseHelper.getAllERDiagrams() // Fetch the saved ER diagrams from the local database
                if (erDiagrams.isNotEmpty()) {
                    val ddlScript = databaseHelper.getDDLScript() // Fetch the DDL script from the local database
                    outputTextView.text = ddlScript // Display the DDL script in the outputTextView
                    Toast.makeText(this@MainActivity, "ER Diagram loaded from local database", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "No stored ER Diagram data available", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error loading ER Diagram: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun connectToDatabase(apiUrl: String, username: String, password: String) {
        val requestBody = mapOf(
            "username" to username,
            "password" to password
        )

        lifecycleScope.launch {
            try {
                // Make the API call
                val connectionResponse = RetrofitInstance.apiService.connectToDatabase(requestBody)

                // Log the response to see what you receive
                Log.d("API Response", connectionResponse.toString())

                if (connectionResponse.success) {
                    // Successful connection; proceed to fetch schema
                    Toast.makeText(this@MainActivity, "Connected to database", Toast.LENGTH_SHORT).show()
                    fetchSchemaAndGenerateERDiagram(apiUrl)
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${connectionResponse.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchSchemaAndGenerateERDiagram(apiUrl: String) {
        lifecycleScope.launch {
            try {
                // Fetch the schema data from the API
                val schemaResponse = RetrofitInstance.apiService.getSchema()
                Log.d("Schema Response", schemaResponse.toString())

                if (schemaResponse.success) {
                    val ddlScript = convertApiResponseToDDL(schemaResponse.erDiagram)
                    executeDDL(ddlScript) // Execute the generated DDL script
                    generateERDiagram(schemaResponse.erDiagram) // Generate ER diagram
                } else {
                    Toast.makeText(this@MainActivity, "Error fetching schema: ${schemaResponse.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error fetching schema: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendSchemaToApi(schema: String) {
        val apiUrl = apiUrlEditText.text.toString().trim()

        if (apiUrl.isNotEmpty()) {
            val requestBody = mapOf("schema" to schema)

            lifecycleScope.launch {
                try {
                    val responseBody = RetrofitInstance.apiService.sendSchemaToApi(requestBody)

                    Log.d("API Response", responseBody.toString())

                    if (responseBody.success) {
                        val ddlScript = convertApiResponseToDDL(responseBody.erDiagram) // Generate the DDL script
                        saveERDiagramToDatabase(responseBody.erDiagram, ddlScript) // Pass the DDL script as an argument
                        executeDDL(ddlScript) // Execute the generated DDL script
                        generateERDiagram(responseBody.erDiagram) // Generate ER diagram
                    } else {
                        Toast.makeText(this@MainActivity, "Error: ${responseBody.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Error", "API call failed", e)
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please enter the API URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executeDDL(ddlScript: String) {
        lifecycleScope.launch {
            val db = databaseHelper.writableDatabase
            try {
                // Split the DDL script into individual statements
                ddlScript.split(";").forEach { statement ->
                    val trimmedStatement = statement.trim()
                    if (trimmedStatement.isNotEmpty()) {
                        db.execSQL(trimmedStatement) // Execute each DDL statement
                    }
                }
                Toast.makeText(this@MainActivity, "DDL executed successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error executing DDL: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                db.close()
            }
        }
    }

    // Method to convert API response to DDL script
    private fun convertApiResponseToDDL(erDiagram: List<ERDiagramData>): String {
        val ddlBuilder = StringBuilder()

        erDiagram.forEach { entity ->
            ddlBuilder.append("CREATE TABLE ${entity.table} (\n")
            entity.attributes.forEach { attribute ->
                ddlBuilder.append("  ${attribute.name} ${attribute.type}")
                if (attribute.key == "PRIMARY") {
                    ddlBuilder.append(" PRIMARY KEY")
                }
                if (attribute.key == "FOREIGN") {
                    ddlBuilder.append(" FOREIGN KEY")
                }
                ddlBuilder.append(",\n")
            }
            Log.d("Entity Attributes", entity.attributes.toString())

            ddlBuilder.setLength(ddlBuilder.length - 2) // Remove last comma
            ddlBuilder.append("\n);\n\n")
        }

        val ddlScript = ddlBuilder.toString()
        Log.d("DDL Script", ddlScript)

        outputTextView.text = ddlScript

        return ddlScript
    }

    // Save ER Diagram data to SQLite database
    private fun saveERDiagramToDatabase(erDiagram: List<network.ERDiagramData>, ddlScript: String) {
        lifecycleScope.launch {
            val db = databaseHelper.writableDatabase

            erDiagram.forEach { entity ->
                val tableNames = entity.table
                val primaryKeys = entity.attributes.filter { it.key == "PRIMARY" }.joinToString(",") { it.name }
                val attributes = entity.attributes.joinToString("|") { it.name + "," + it.type }
                val foreignKeys = entity.attributes.filter { it.key == "FOREIGN" }.joinToString(",") { it.name }
                val relationships = "" // Assume relationship data isn't provided directly in the response

                val query = "INSERT INTO er_diagrams (table_names, primary_keys, attributes, foreign_keys, relationships, ddl_script) VALUES (?, ?, ?, ?, ?, ?)"
                db.execSQL(query, arrayOf(tableNames, primaryKeys, attributes, foreignKeys, relationships, ddlScript))
            }

            db.close()
            Toast.makeText(this@MainActivity, "ER Diagram data and DDL script saved to local database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateERDiagram(erDiagram: List<network.ERDiagramData>) {
        if (erDiagram.isEmpty()) {
            Toast.makeText(this, "No ER Diagram data available", Toast.LENGTH_SHORT).show()
            return
        }

        val erDiagramString = buildString {
            erDiagram.forEach { entity ->
                append("Entity: ${entity.table}\n")
                entity.attributes.forEach { attribute ->
                    append("  ${attribute.name} (${attribute.type}) - ${attribute.key}\n")
                }
                append("\n")
            }
        }

        val intent = Intent(this, ERDiagramActivity::class.java).apply {
            putExtra("ER_DIAGRAM_TEXT", erDiagramString)
        }
        startActivity(intent)
    }


    private fun showStoredERDiagrams() {
        val erDiagrams = databaseHelper.getAllERDiagrams()
        val erDiagramText = erDiagrams.joinToString("\n\n") {
            """
        Entity: ${it.table}
        Attributes: ${it.attributes.joinToString(", ") { attr -> "${attr.name} (${attr.type})" }}
        Primary Keys: ${it.primaryKeys.joinToString(", ")}
        Foreign Keys: ${it.foreignKeys.joinToString(", ")}
        """.trimIndent()
        }

        val intent = Intent(this, ERDiagramActivity::class.java).apply {
            putExtra("ER_DIAGRAM_TEXT", erDiagramText)
        }
        startActivity(intent)
    }


    private fun loadSchemaFile() {
        // Launch file picker to select schema input file
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadSchemaFile()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                readSchemaFile(uri)
            }
        }
    }

    private fun readSchemaFile(uri: Uri) {
        // Read the selected file and set the schema input field
        contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
            val schemaContent = reader?.readText() ?: ""
            schemaInput.setText(schemaContent)
        }
    }
}

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "ERDatabase.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE er_diagrams (id INTEGER PRIMARY KEY AUTOINCREMENT, table_names TEXT, primary_keys TEXT, attributes TEXT, foreign_keys TEXT, relationships TEXT, ddl_script TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS er_diagrams")
        onCreate(db)
    }


    fun getDDLScript(): String {
        val db = readableDatabase
        var ddlScript = ""
        val cursor = db.rawQuery("SELECT ddl_script FROM er_diagrams LIMIT 1", null)

        cursor.use {
            if (it.moveToFirst()) {
                ddlScript = it.getString(it.getColumnIndexOrThrow("ddl_script"))
            }
        }

        db.close()
        return ddlScript
    }


    fun getAllERDiagrams(): List<ERDiagramData> {
        val erDiagrams = mutableListOf<ERDiagramData>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM er_diagrams", null)
        cursor.use {
            while (it.moveToNext()) {
                val tableNames = it.getString(it.getColumnIndexOrThrow("table_names"))
                val primaryKeys = it.getString(it.getColumnIndexOrThrow("primary_keys")).split(",")
                val attributes = it.getString(it.getColumnIndexOrThrow("attributes")).split("|").map { attr ->
                    val parts = attr.split(",")
                    ERAttribute(parts[0], parts[1], "")
                }
                val foreignKeys = it.getString(it.getColumnIndexOrThrow("foreign_keys")).split(",")
                val relationships = it.getString(it.getColumnIndexOrThrow("relationships"))

                erDiagrams.add(
                    ERDiagramData(
                        table = tableNames,
                        primaryKeys = primaryKeys,
                        attributes = attributes,
                        foreignKeys = foreignKeys,
                        relationships = relationships
                    )
                )
            }
        }
        db.close()
        return erDiagrams
    }
}
