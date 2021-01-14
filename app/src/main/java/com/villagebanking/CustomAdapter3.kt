package com.villagebanking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.statement_row_layout.view.*

class CustomAdapter3(mContext3: Context, private val statementsModel: ArrayList<Model>): RecyclerView.Adapter<CustomAdapter3.ViewHolder>()   {

    val mContext3 = mContext3


    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatementTransactionID : TextView = itemView.tvStatementTransactionID
        val tvStatementDate : TextView = itemView.tvStatementDate

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.statement_row_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statementModelPosition = statementsModel[position]
        holder.tvStatementTransactionID.text = statementModelPosition.statementsID.toString()
        holder.tvStatementDate.text = statementModelPosition.statementsDate
    }

    override fun getItemCount(): Int {
        return statementsModel.size
    }


}