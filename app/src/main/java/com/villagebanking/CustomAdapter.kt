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
import kotlinx.android.synthetic.main.dialog_add_account_holder.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.view.*
import kotlinx.android.synthetic.main.dialog_insert_password.*
import kotlinx.android.synthetic.main.dialog_insert_password.view.*
import kotlinx.android.synthetic.main.dialog_posts.*
import kotlinx.android.synthetic.main.dialog_posts.view.*
import kotlinx.android.synthetic.main.main_row_layout.view.*
import kotlinx.android.synthetic.main.main_row_layout.view.tvName


class CustomAdapter(mContext: Context, private val accountHolderModel: ArrayList<Model>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    val mContext = mContext

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvID: TextView = itemView.tvID
        val tvName: TextView = itemView.tvName
        val tvAdmin: TextView = itemView.tvAdmin
        val tvShares: TextView = itemView.tvShares
        val tvLoanApplication: TextView = itemView.tvLoanApplication
        val tvCharges: TextView = itemView.tvCharges
        val btnPosting: ImageButton? = itemView.btnPostings
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_row_layout, parent, false)
        return ViewHolder(view)
    }



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val accountHolderModelPosition: Model = accountHolderModel[position]
        holder.tvID.text = accountHolderModelPosition.accountHoldersID.toString()
        holder.tvName.text = accountHolderModelPosition.accountHoldersName
        holder.tvAdmin.text = accountHolderModelPosition.accountHoldersAdmin
        holder.tvShares.text = accountHolderModelPosition.accountHoldersShare.toString()
        holder.tvLoanApplication.text = accountHolderModelPosition.accountHoldersLoanApp.toString()
        holder.tvCharges.text = accountHolderModelPosition.accountHoldersCharges.toString()



            holder.itemView.setOnClickListener {

                if (    holder.tvShares.text.toString().toDouble() == 0.0){
                    Toast.makeText(mContext,"Cannot post zero shares",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val id =holder.tvID.text
                val intent = Intent(mContext, AccountDetails::class.java)
                intent.putExtra("ID", id)
                holder.itemView.context.startActivity(intent)
            }



            holder.itemView.setOnLongClickListener {

                //Create Spinner Array
                val admin = arrayOf(
                        "Chairperson",
                        "Vice Chairperson",
                        "Secretary",
                        "Money Counter 1",
                        "Money Counter 2",
                        "Account Holder"
                )

                //Need to declare variables for the layout before adding array to it
                //Otherwise the layout will not know what variables to place in the spinner
                val addAccountHolderDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_account_holder, null)
                val insertPINDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_insert_password, null)

                val etFullNameName : EditText = addAccountHolderDialogLayout.etFullNames
                val etContactNo : EditText = addAccountHolderDialogLayout.etContactNo
                val etAccountInfo : EditText = addAccountHolderDialogLayout.etAccountInfo

                val etInsertPin : EditText = insertPINDialogLayout.etInsertPIN
                val etPinHint : EditText = insertPINDialogLayout.etPinHint


                //Add array to Spinner List in layout
                val arrayAdapter = ArrayAdapter(mContext, android.R.layout.simple_selectable_list_item, admin)
                addAccountHolderDialogLayout.spAdministrators.adapter = arrayAdapter

                var selectedAdmin = ""

                addAccountHolderDialogLayout.spAdministrators.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedAdmin = admin[position]
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

                val item = admin.indexOf(accountHolderModelPosition.accountHoldersAdmin)

                etFullNameName.setText(accountHolderModelPosition.accountHoldersName)
                etContactNo.setText(accountHolderModelPosition.accountHolderContact)
                etAccountInfo.setText(accountHolderModelPosition.accountHolderBankInfo)
                etFullNameName.isEnabled = false

                if (selectedAdmin != "Chairperson"){
                    admin[item] = "Chairperson"
                    admin[0] = accountHolderModelPosition.accountHoldersAdmin
                }

                AlertDialog.Builder(mContext)
                        .setTitle("Update or Delete Account")
                        .setIcon(R.drawable.ic_account_holders)
                        .setView(addAccountHolderDialogLayout)
                        .setNeutralButton("Delete") {_,_->
                            if (accountHolderModelPosition.accountHoldersAdmin == "Chairperson"){
                                Toast.makeText(mContext, "Cannot delete Chairperson.", Toast.LENGTH_SHORT).show()
                                return@setNeutralButton
                            }

                            val confirmDeleteDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.delete_confirmation, null)
                            val btnConfirmDelete: Button = confirmDeleteDialogLayout.btnConfirmDelete
                            val btnCancelDelete: Button = confirmDeleteDialogLayout.btnCancelDelete
                            val name = accountHolderModelPosition.accountHoldersName
                            val alertDialog = AlertDialog.Builder(mContext)
                                    .setTitle("Warning!")
                                    .setMessage("Are you sure you want to delete $name? All account related information will be deleted")
                                    .setIcon(R.drawable.ic_delete)
                                    .setPositiveButton("Yes") {_:DialogInterface, _: Int ->

                                        val confirmDeleteLayout = AlertDialog.Builder(mContext)
                                                .setTitle("Confirm Delete")
                                                .setIcon(R.drawable.ic_delete)
                                                .setMessage("Long press to delete")
                                                .setView(confirmDeleteDialogLayout)
                                        val showConfirmDeleteLayout = confirmDeleteLayout.show()

                                        btnConfirmDelete.setOnClickListener {
                                            Toast.makeText(mContext, "Long press to delete", Toast.LENGTH_SHORT).show()
                                        }

                                        btnCancelDelete.setOnClickListener {
                                            showConfirmDeleteLayout.dismiss()
                                        }

                                        btnConfirmDelete.setOnLongClickListener {
                                                MainActivity.dbHandler.delAccountHolder(accountHolderModelPosition.accountHoldersID)
                                                MainActivity.dbHandler.delAccountHolderTransactions(accountHolderModelPosition.accountHoldersName)
                                                Toast.makeText(mContext, "$name has been deleted", Toast.LENGTH_SHORT).show()
                                                accountHolderModel.removeAt(position)
                                                notifyItemRemoved(position)
                                                notifyItemRangeRemoved(position, accountHolderModel.size)
                                                showConfirmDeleteLayout.dismiss()
                                            true
                                        }
                                    }
                                    .setNegativeButton("No") {_:DialogInterface, _: Int ->
                                    }
                            alertDialog.show()






                        }




                        .setPositiveButton("Update", null)
                        .setNegativeButton("Cancel") {_,_->}
                        .create().apply {
                            setOnShowListener{
                                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                                    if (addAccountHolderDialogLayout.etFullNames.text.isEmpty()) {
                                        Toast.makeText(mContext, "Please type Full Name", Toast.LENGTH_SHORT).show()
                                        return@setOnClickListener}

                                    if (addAccountHolderDialogLayout.etContactNo.text.isEmpty()) {
                                        Toast.makeText(mContext, "Please enter contact number", Toast.LENGTH_SHORT).show()
                                        return@setOnClickListener}

                                    if (addAccountHolderDialogLayout.etAccountInfo.text.isEmpty()) {
                                        Toast.makeText(mContext, "Please enter account or mobile banking info", Toast.LENGTH_LONG).show()
                                        return@setOnClickListener}

                                    when (selectedAdmin) {"Account Holder" -> {



                                        val update : Boolean = MainActivity.dbHandler.editAccountHolder(mContext,
                                                accountHolderModelPosition.accountHoldersID.toString().toInt(),
                                                etFullNameName.text.toString(),
                                                selectedAdmin,
                                                etContactNo.text.toString(),
                                                etAccountInfo.text.toString(),
                                                etInsertPin.text.toString(),
                                                etPinHint.text.toString())
                                        if (update) {
                                            accountHolderModel[position].accountHoldersName = etFullNameName.text.toString()
                                            accountHolderModel[position].accountHoldersAdmin = selectedAdmin
                                            accountHolderModel[position].accountHolderContact = etContactNo.text.toString()
                                            accountHolderModel[position].accountHolderBankInfo = etAccountInfo.text.toString()
                                            accountHolderModel[position].accountHolderPin = ""
                                            accountHolderModel[position].accountHolderPinHint = ""
                                            notifyDataSetChanged()
                                            dismiss()
                                        }



                                    }"Chairperson" ->{


                                        val update : Boolean = MainActivity.dbHandler.editAccountHolder(mContext,
                                                accountHolderModelPosition.accountHoldersID.toString().toInt(),
                                                etFullNameName.text.toString(),
                                                selectedAdmin,
                                                etContactNo.text.toString(),
                                                etAccountInfo.text.toString(),
                                                etInsertPin.text.toString(),
                                                etPinHint.text.toString())
                                        if (update) {
                                            accountHolderModel[position].accountHoldersName = etFullNameName.text.toString()
                                            accountHolderModel[position].accountHoldersAdmin = selectedAdmin
                                            accountHolderModel[position].accountHolderContact = etContactNo.text.toString()
                                            accountHolderModel[position].accountHolderBankInfo = etAccountInfo.text.toString()
                                            accountHolderModel[position].accountHolderPin = etInsertPin.text.toString()
                                            accountHolderModel[position].accountHolderPinHint = etPinHint.text.toString()
                                            notifyDataSetChanged()
                                            dismiss()
                                        }
                                        Toast.makeText(mContext, "Update Chairpersons PIN", Toast.LENGTH_SHORT).show()
                                        AlertDialog.Builder(mContext)
                                                .setTitle("Update PIN")
                                                .setIcon(R.drawable.ic_key)
                                                .setView(insertPINDialogLayout)
                                                .setPositiveButton("Update", null)
                                                .create().apply {
                                                    setOnShowListener{
                                                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                                            if(etInsertPIN.text.isEmpty()){
                                                                Toast.makeText(mContext, "Please type PIN", Toast.LENGTH_SHORT).show()
                                                                return@setOnClickListener}

                                                            if(etPinRepeat.text.isEmpty()){
                                                                Toast.makeText(mContext, "Please type repeat PIN", Toast.LENGTH_SHORT).show()
                                                                return@setOnClickListener}

                                                            if(etPinHint.text.isEmpty()){
                                                                Toast.makeText(mContext, "Please type hint", Toast.LENGTH_SHORT).show()
                                                                return@setOnClickListener}

                                                            if(etInsertPIN.text.toString() != etPinRepeat.text.toString()){
                                                                Toast.makeText(mContext, "PIN and repeat PIN do not match. Re-type", Toast.LENGTH_LONG).show()
                                                                return@setOnClickListener}

                                                            if(etInsertPIN.text.length < 4){
                                                                Toast.makeText(mContext, "PIN should be 4 digits", Toast.LENGTH_LONG).show()
                                                                return@setOnClickListener}

                                                            val update2 : Boolean = MainActivity.dbHandler.editAccountHolder(mContext,
                                                                    accountHolderModelPosition.accountHoldersID.toString().toInt(),
                                                                    etFullNameName.text.toString(),
                                                                    selectedAdmin,
                                                                    etContactNo.text.toString(),
                                                                    etAccountInfo.text.toString(),
                                                                    etInsertPin.text.toString(),
                                                                    etPinHint.text.toString())
                                                            if (update2) {
                                                                accountHolderModel[position].accountHoldersName = etFullNameName.text.toString()
                                                                accountHolderModel[position].accountHoldersAdmin = selectedAdmin
                                                                accountHolderModel[position].accountHolderContact = etContactNo.text.toString()
                                                                accountHolderModel[position].accountHolderBankInfo = etAccountInfo.text.toString()
                                                                accountHolderModel[position].accountHolderPin = etInsertPin.text.toString()
                                                                accountHolderModel[position].accountHolderPinHint = etPinHint.text.toString()
                                                                notifyDataSetChanged()
                                                                dismiss()
                                                            }
                                                        }
                                                    }
                                                }
                                                .show()

                                    }else -> {}
                                    }
                                }
                            }
                        }
                        .show()

                true
            }



        holder.btnPosting?.setOnClickListener {
            val postsDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_posts, null)

            postsDialogLayout.etPostsShares.setText(accountHolderModel[position].accountHoldersShare.toString())
            postsDialogLayout.etPostsLoanApplication.setText(accountHolderModel[position].accountHoldersLoanApp.toString())
            postsDialogLayout.etPostsCharges.setText(accountHolderModel[position].accountHoldersCharges.toString())

            AlertDialog.Builder(mContext)
                    .setTitle("Preparations")
                    .setView(postsDialogLayout)
                    .setPositiveButton("Submit", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                if (etPostsShares.text.toString() == ""){
                                    etPostsShares.setText("0")
                                }

                                if (etPostsLoanApplication.text.toString() == ""){
                                    etPostsLoanApplication.setText("0")
                                }

                                if (etPostsCharges.text.toString() == ""){
                                    etPostsCharges.setText("0")
                                }

                                val posts : Boolean = MainActivity.dbHandler.postings(mContext, accountHolderModelPosition.accountHoldersID,
                                    postsDialogLayout.etPostsShares.text.toString(),
                                    postsDialogLayout.etPostsLoanApplication.text.toString(),
                                    postsDialogLayout.etPostsCharges.text.toString())
                                if (posts){
                                    accountHolderModel[position].accountHoldersShare = postsDialogLayout.etPostsShares.text.toString().toDouble()
                                    accountHolderModel[position].accountHoldersLoanApp = postsDialogLayout.etPostsLoanApplication.text.toString().toDouble()
                                    accountHolderModel[position].accountHoldersCharges = postsDialogLayout.etPostsCharges.text.toString().toDouble()
                                    notifyDataSetChanged()
                                    dismiss()
                                }else{
                                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .show()
        }
        }


    override fun getItemCount(): Int {
        return accountHolderModel.size
    }









}

