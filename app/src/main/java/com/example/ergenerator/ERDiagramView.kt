package com.example.ergenerator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

// Define a data class to represent a table with attributes and keys
data class Table(
    val name: String,
    val attributes: List<String>,
    val primaryKey: String,
    val foreignKeys: List<String>
)

class ERDiagramView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var tables: List<Table> = listOf()
    private var relationships: Map<String, String> = mapOf()

    // Paint styles for drawing
    private val entityPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        strokeWidth = 5f
    }
    private val keyPaint = Paint().apply {
        color = Color.RED
        textSize = 40f
        strokeWidth = 5f
    }
    private val foreignKeyPaint = Paint().apply {
        color = Color.BLUE
        textSize = 40f
        strokeWidth = 5f
    }

    // Set the ER data (tables and relationships)
    fun setERData(tables: List<Table>, relationships: Map<String, String>) {
        this.tables = tables
        this.relationships = relationships
        invalidate() // Trigger a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (tables.isEmpty()) return // Nothing to draw if there are no tables

        val padding = 50f
        val tableWidth = 400f // Width of each table box
        val tableHeight = 250f // Height of each table box (can be adjusted)
        val boxHeightPerAttribute = 50f // Height for each row inside the table
        var xPosition = padding // Start drawing from the left
        var yPosition: Float = padding // Start drawing from the top

        // Store positions of tables to draw relationships later
        val tablePositions = mutableMapOf<String, Pair<Float, Float>>()

        for ((index, table) in tables.withIndex()) {
            // Draw two boxes for each entity
            for (j in 0..1) {
                // Calculate position for the current box
                val currentXPosition = xPosition + (j * (tableWidth + padding))
                val currentYPosition = yPosition + (index * (tableHeight + padding))

                // Draw the entity (table) box
                canvas.drawRect(currentXPosition, currentYPosition, currentXPosition + tableWidth, currentYPosition + tableHeight, entityPaint)
                canvas.drawRect(currentXPosition, currentYPosition, currentXPosition + tableWidth, currentYPosition + tableHeight, borderPaint)

                // Draw the table name inside the box (header)
                textPaint.color = Color.BLACK
                canvas.drawText("Table: ${table.name}", currentXPosition + 20f, currentYPosition + 40f, textPaint)

                // Start drawing attributes below the table name
                var currentDrawY = currentYPosition + 80f // Adjust vertical position after the table name

                // Draw the primary key (highlight in red)
                textPaint.color = Color.RED
                canvas.drawText("Primary Key: ${table.primaryKey}", currentXPosition + 20f, currentDrawY, textPaint)
                currentDrawY += boxHeightPerAttribute

                // Draw the attributes (normal text color)
                textPaint.color = Color.BLACK
                canvas.drawText("Attributes:", currentXPosition + 20f, currentDrawY, textPaint)
                currentDrawY += boxHeightPerAttribute

                table.attributes.forEach { attribute ->
                    canvas.drawText("- $attribute", currentXPosition + 40f, currentDrawY, textPaint)
                    currentDrawY += boxHeightPerAttribute
                }

                // Draw foreign keys (highlight in blue if any exist)
                if (table.foreignKeys.isNotEmpty()) {
                    textPaint.color = Color.BLUE
                    canvas.drawText("Foreign Keys:", currentXPosition + 20f, currentDrawY, textPaint)
                    currentDrawY += boxHeightPerAttribute

                    table.foreignKeys.forEach { foreignKey ->
                        canvas.drawText("- $foreignKey", currentXPosition + 40f, currentDrawY, textPaint)
                        currentDrawY += boxHeightPerAttribute
                    }
                }

                // Store the position of the current table for later drawing of relationships
                tablePositions["${table.name}_$j"] = Pair(currentXPosition + tableWidth / 2, currentYPosition + tableHeight)
            }
        }

        // Draw relationships between tables
        textPaint.color = Color.BLACK
        relationships.forEach { (fromTable, toTable) ->
            val fromPos = tablePositions[fromTable]
            val toPos = tablePositions[toTable]

            if (fromPos != null && toPos != null) {
                // Draw a line between the two entities
                canvas.drawLine(
                    fromPos.first,
                    fromPos.second,
                    toPos.first,
                    toPos.second,
                    borderPaint
                )
                // Draw the relationship label
                canvas.drawText("$fromTable -> $toTable", (fromPos.first + toPos.first) / 2, (fromPos.second + toPos.second) / 2 - 10, textPaint)
            }
        }
    }
}
