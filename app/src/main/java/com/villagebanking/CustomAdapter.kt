package com.villagebanking

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.main_row_layout.view.*
import kotlinx.android.synthetic.main.main_row_layout.view.tvName


class CustomAdapter(mContext: Context, private val accountHolderModel: ArrayList<AccountHolderModel>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    val mContext = mContext

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvID: TextView = itemView.tvID
        val tvName: TextView = itemView.tvName
        val tvAdmin: TextView = itemView.tvAdmin
        val tvShares: TextView = itemView.tvShares
        val tvLoanApplication: TextView = itemView.tvLoanApplication

        val btnProcess: ImageButton? = itemView.btnProcess
        val btnDelete: ImageButton? = itemView.btnDelete
        val btnEdit: ImageButton? = itemView.btnEdit

        init {
            itemView.setOnClickListener {
               val intent = Intent(itemView.context, AccountDetails::class.java)
               val name = tvName.text.toString()
               intent.putExtra("nameDetailsAD", name)
               itemView.context.startActivity(intent)
            }
            }




    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_row_layout, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val accountHolderModelPosition: AccountHolderModel = accountHolderModel[position]
        holder.tvID.text = accountHolderModelPosition.accountHoldersID.toString()
        holder.tvName.text = accountHolderModelPosition.accountHoldersName
        holder.tvAdmin.text = accountHolderModelPosition.accountHoldersAdmin
        holder.tvShares.text = accountHolderModelPosition.accountHoldersShare.toString()
        holder.tvLoanApplication.text = accountHolderModelPosition.accountHoldersLoanApp.toString()





        holder.btnDelete?.setOnClickListener {

            val name = accountHolderModelPosition.accountHoldersName

            val alertDialog = AlertDialog.Builder(mContext)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete $name?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Yes") {_:DialogInterface, _: Int ->
                    MainActivity.dbHandler.delAccount(accountHolderModelPosition.accountHoldersID)
                    Toast.makeText(mContext, "$name has been deleted", Toast.LENGTH_SHORT).show()
                    accountHolderModel.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeRemoved(position, accountHolderModel.size)
                }
                .setNegativeButton("No") {_:DialogInterface, _: Int ->
                }
                alertDialog.show()


        }

    }



    override fun getItemCount(): Int {
        return  accountHolderModel.size
    }



}

