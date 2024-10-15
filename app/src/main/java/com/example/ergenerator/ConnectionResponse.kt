package com.example.ergenerator

data class ConnectionResponse(
    val erDiagram: String, // This should match the key in your JSON response
    val success: Boolean,
    val message: String
)
