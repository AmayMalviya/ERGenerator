package com.example.ergenerator

// Request body model for API request
data class RequestBody(
    val username: String,
    val password: String
)

// Response body model for API response
data class ResponseBody(
    val success: Boolean,
    val message: String
)
