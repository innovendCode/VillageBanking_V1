package com.villagebanking

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.delete_confirmation.view.*
import kotlinx.android.synthetic.main.dialog_posts.view.*
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



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val accountHolderModelPosition: AccountHolderModel = accountHolderModel[position]
        holder.tvID.text = accountHolderModelPosition.accountHoldersID.toString()
        holder.tvName.text = accountHolderModelPosition.accountHoldersName
        holder.tvAdmin.text = accountHolderModelPosition.accountHoldersAdmin
        holder.tvShares.text = accountHolderModelPosition.accountHoldersShare.toString()
        holder.tvLoanApplication.text = accountHolderModelPosition.accountHoldersLoanApp.toString()

        holder.btnDelete?.setOnClickListener {
            val confirmDeleteDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.delete_confirmation, null)
            val etConfirmDelete: EditText = confirmDeleteDialogLayout.etConfirmDelete
            val btnConfirmDelete: Button = confirmDeleteDialogLayout.btnConfirmDelete
            val btnCancelConfirmDelete = confirmDeleteDialogLayout.btnCancelConfirmDelete
            val name = accountHolderModelPosition.accountHoldersName
            val alertDialog = AlertDialog.Builder(mContext)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete $name?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Yes") {_:DialogInterface, _: Int ->

                    val confirmDeleteLayout = AlertDialog.Builder(mContext)
                            .setTitle("Confirm Delete")
                            .setMessage("Type 'DELETE' to confirm")
                            .setView(confirmDeleteDialogLayout)
                    val showConfirmDeleteLayout = confirmDeleteLayout.show()

                        btnConfirmDelete.setOnClickListener {
                            if(etConfirmDelete.text.toString() == "DELETE"){
                                MainActivity.dbHandler.delAccount(accountHolderModelPosition.accountHoldersID)
                                Toast.makeText(mContext, "$name has been deleted", Toast.LENGTH_SHORT).show()
                                accountHolderModel.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeRemoved(position, accountHolderModel.size)
                                showConfirmDeleteLayout.dismiss()
                            }else{
                                Toast.makeText(mContext, "Type DELETE in CAPs", Toast.LENGTH_SHORT).show()
                            }
                        }
                        btnCancelConfirmDelete.setOnClickListener {
                            showConfirmDeleteLayout.dismiss()
                        }
                }
                .setNegativeButton("No") {_:DialogInterface, _: Int ->
                }
                alertDialog.show()
        }

        holder.btnProcess?.setOnClickListener {

            val postsDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_posts, null)
            val etPostsShares : EditText = postsDialogLayout.etPostsShares
            val etPostsLoanApplication : EditText = postsDialogLayout.etPostsLoanApplication
            val tvPostsName = accountHolderModelPosition.accountHoldersName

            etPostsShares.setText(accountHolderModelPosition.accountHoldersShare.toString())
            etPostsLoanApplication.setText(accountHolderModelPosition.accountHoldersLoanApp.toString())

            val alertDialog = AlertDialog.Builder(mContext)
                    .setTitle(tvPostsName)
                    .setIcon(R.drawable.ic_money)
                    .setView(postsDialogLayout)
                    .setPositiveButton("Post") {_:DialogInterface, _: Int ->

                        val update : Boolean = MainActivity.dbHandler.acceptShare(accountHolderModelPosition.accountHoldersID.toString(),
                                postsDialogLayout.etPostsShares.text.toString(),
                                postsDialogLayout.etPostsLoanApplication.text.toString())

                        if(update) {
                            accountHolderModel[position].accountHoldersShare = etPostsShares.text.toString().toInt()
                            accountHolderModel[position].accountHoldersLoanApp = etPostsLoanApplication.text.toString().toDouble()
                        }
                    }
                    .setNegativeButton("Cancel") {_:DialogInterface, _: Int ->
                    }
            alertDialog.show()
        }





    }



    override fun getItemCount(): Int {
        return accountHolderModel.size
    }



}

