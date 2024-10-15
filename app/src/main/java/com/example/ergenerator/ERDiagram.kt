package com.example.ergenerator

data class ERDiagram(
    val id: Int,
    val name: String,
    val tableNames: List<String>,
    val primaryKeys: List<String>,
    val attributes: List<List<String>>,
    val foreignKeys: List<List<String>>,
    val relationships: List<String>,
    val createdAt: String
)
