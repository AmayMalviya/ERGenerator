package com.example.ergenerator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import network.ERDiagramData // Ensure this import is correct based on your project structure

class ERDiagramAdapter(
    private val context: Context,
    private val erDiagrams: List<ERDiagramData>
) : RecyclerView.Adapter<ERDiagramAdapter.ERDiagramViewHolder>() {

    private var onItemClickListener: ((ERDiagramData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ERDiagramViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.er_diagram_item, parent, false)
        return ERDiagramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ERDiagramViewHolder, position: Int) {
        val diagram = erDiagrams[position]
        holder.nameTextView.text = diagram.table // Assuming 'table' is the correct property for the diagram name
        //holder.createdAtTextView.text = "Created at: ${diagram.createdAt}" // Assuming there's a 'createdAt' property

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(diagram)
        }
    }

    override fun getItemCount(): Int {
        return erDiagrams.size
    }

    fun setOnItemClickListener(listener: (ERDiagramData) -> Unit) {
        onItemClickListener = listener
    }

    class ERDiagramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.diagram_name)
        val createdAtTextView: TextView = view.findViewById(R.id.diagram_created_at)
    }
}
