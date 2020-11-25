package com.villagebanking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_row_layout.view.*

class CustomAdapter(mContext: Context, val accountHolderModel: ArrayList<AccountHolderModel>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    val mContext = mContext

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvName = itemView.tvName
        val tvAdmin = itemView.tvAdmin
        val tvShares = itemView.tvShares
        val tvLoanApplication = itemView.tvLoanApplication

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_row_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val accountHolderModel: AccountHolderModel = accountHolderModel[position]

        holder.tvName.text = accountHolderModel.account_holders_name
        holder.tvAdmin.text= accountHolderModel.account_holders_admin
        holder.tvShares.text = accountHolderModel.account_holders_preshare.toString()
        holder.tvLoanApplication.text = accountHolderModel.account_holders_loanapp.toString()
    }

    override fun getItemCount(): Int {
        return  accountHolderModel.size
    }
}