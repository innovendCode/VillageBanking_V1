package com.villagebanking

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.main_row_layout.view.*
import kotlinx.android.synthetic.main.main_row_layout.view.tvName


class CustomAdapter(mContext: Context, private val accountHolderModel: ArrayList<AccountHolderModel>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    val mContext = mContext

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvName: TextView = itemView.tvName
        val tvShares: TextView = itemView.tvShares
        val tvLoanApplication: TextView = itemView.tvLoanApplication

        private val btnProcess: FloatingActionButton = itemView.btnProcess

        init {
            itemView.setOnClickListener {
                    val memberDetailsDialogLayout = LayoutInflater.from(itemView.context).inflate(R.layout.member_details, null)
                    val memberDetailsDialog = AlertDialog.Builder(itemView.context)
                            .setView(memberDetailsDialogLayout)
                            .setTitle(tvName.text)
                    val showMemberDetailsDialog = memberDetailsDialog.show()
            }
            }


        init {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context,"Long-Press to process Commitments", Toast.LENGTH_SHORT).show()
            }
            }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_row_layout, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val accountHolderModel: AccountHolderModel = accountHolderModel[position]
        holder.tvName.text = accountHolderModel.accountHoldersName
        holder.tvShares.text = accountHolderModel.accountHoldersShare.toString()
        holder.tvLoanApplication.text = accountHolderModel.accountHoldersLoanApp.toString()
    }


    override fun getItemCount(): Int {
        return  accountHolderModel.size
    }
}

