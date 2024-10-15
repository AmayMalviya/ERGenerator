package network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// ApiService interface for Retrofit
interface ApiService {

    // Fetch the schema as a list of tables with their schema details
    @GET("api/generateER") // Update to use the specific endpoint
    suspend fun getSchema(): SchemaResponse

    // Connect to the database using provided credentials
    @POST("api/connect")
    suspend fun connectToDatabase(@Body requestBody: Map<String, String>): ConnectionResponse

    // Send schema to the API for processing
    @POST("your/api/endpoint")
    suspend fun sendSchemaToApi(@Body requestBody: Map<String, String>): ApiResponse
}


// Data class for Table Schema
data class TableSchema(
    val tableName: String,     // Name of the table
    val columns: List<Column>  // List of columns in the table
)

// Data class for column information
data class Column(
    val columnName: String,
    val dataType: String,
    val isPrimaryKey: Boolean,
    val isForeignKey: Boolean
)

// Response body model for API response
data class ResponseBody(
    val success: Boolean,
    val message: String,
    val erDiagram: List<ERDiagramData> // Ensure this is the correct type
)

data class SchemaResponse(
    val success: Boolean,
    val message: String,
    val erDiagram: List<ERDiagramData>
)

// Response model for database connection
data class ConnectionResponse(
    val success: Boolean,
    val message: String,
    val erDiagram: List<ERDiagramData> // Should match the JSON structure
)

// Data class for API response
data class ApiResponse(
    val success: Boolean,
    val erDiagram: List<ERDiagramData>, // Adjust this if needed based on your response structure
    val message: String? = null // Optional message for error cases
)


// Data class for ER Diagram attributes
data class ERAttribute(
    val name: String,
    val type: String,
    val key: String
)

// Data class for ER Diagram data
data class ERDiagramData(
    val table: String,
    val attributes: List<ERAttribute>,
    val primaryKeys: List<String> = emptyList(),
    val foreignKeys: List<String> = emptyList(),
    val relationships: String = ""
)