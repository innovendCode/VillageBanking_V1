package com.villagebanking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sub_row_layout.view.*

class CustomAdapter2(mContext2: Context, private val transactionsModel: ArrayList<AccountHolderModel>): RecyclerView.Adapter<CustomAdapter2.ViewHolder>()  {

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionID : TextView = itemView.tvTransactionID
        val tvTransactionMonth : TextView = itemView.tvTransactionMonth
        val tvTransactionShares : TextView = itemView.tvTransactionShares
        val tvTransactionLoan : TextView = itemView.tvTransactionLoan
        val tvTransactionShareDate : TextView = itemView.tvTransactionShareDate
        val tvTransactionLoanDate : TextView = itemView.tvTransactionLoanDate

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_row_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionsModelPosition : AccountHolderModel = transactionsModel[position]
        holder.tvTransactionID.text = transactionsModelPosition.transactionID.toString()
        holder.tvTransactionMonth.text = transactionsModelPosition.transactionMonth
        holder.tvTransactionShares.text = transactionsModelPosition.transactionShares.toString()
        holder.tvTransactionLoan.text = transactionsModelPosition.transactionLoan.toString()
        holder.tvTransactionLoanDate.text = transactionsModelPosition.transactionLoanDate
        holder.tvTransactionShareDate.text = transactionsModelPosition.transactionShareDate
    }

    override fun getItemCount(): Int {
        return transactionsModel.size
    }


}