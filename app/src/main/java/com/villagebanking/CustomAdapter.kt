package com.villagebanking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.villagebanking.MainActivity.Companion.dbHandler
import kotlinx.android.synthetic.main.account_holders.*
import kotlinx.android.synthetic.main.account_holders.view.*
import kotlinx.android.synthetic.main.delete_confirmation.view.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.view.*
import kotlinx.android.synthetic.main.dialog_insert_password.*
import kotlinx.android.synthetic.main.dialog_insert_password.view.*
import kotlinx.android.synthetic.main.dialog_posts.*
import kotlinx.android.synthetic.main.dialog_posts.view.*
import kotlinx.android.synthetic.main.main_row_layout.*
import kotlinx.android.synthetic.main.main_row_layout.view.*
import kotlinx.android.synthetic.main.main_row_layout.view.tvName
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong


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
        val tvApproved: TextView = itemView.tvApproved
        val tvAsset: TextView = itemView.tvAsset
        val tvLiability: TextView = itemView.tvLiability
        val imgClearedAll: ImageView = itemView.imgClearedAll
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_row_layout, parent, false)
        return ViewHolder(view)
    }



    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val accountHolderModelPosition: Model = accountHolderModel[position]
        holder.tvID.text = accountHolderModelPosition.accountHoldersID.toString()
        holder.tvName.text = accountHolderModelPosition.accountHoldersName
        holder.tvAdmin.text = accountHolderModelPosition.accountHoldersAdmin
        holder.tvShares.text = accountHolderModelPosition.accountHoldersShare.toString()
        holder.tvLoanApplication.text = BigDecimal(accountHolderModelPosition.accountHoldersLoanApp).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvCharges.text = BigDecimal(accountHolderModelPosition.accountHoldersCharges).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvAsset.text = BigDecimal(accountHolderModelPosition.accountHoldersAsset).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvLiability.text = BigDecimal(accountHolderModelPosition.accountHoldersLiability).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvApproved.text = accountHolderModelPosition.accountHoldersApproved

        holder.imgClearedAll.isGone = accountHolderModel[position].accountHoldersApproved != "No Arrears"

        if (holder.tvLiability.text.toString().toDouble() > -1) {
            holder.tvLiability.setTextColor(Color.parseColor("#09AA9B"))
        } else {
            holder.tvLiability.setTextColor(Color.parseColor("#BA0707"))
        }

        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))

        var interestRate = 0.0
        var shareValue = 0.0

        var shareAmount = 0.0
        var sharePayment = 0.0
        var loanApplication = 0.0
        var loanToRepay = 0.0
        var loanPayout = 0.0
        var loanRepayment = 0.0
        var charge = 0.0
        var chargePayment = 0.0
        var virtualBalance = 0.0
        var actualBalance = 0.0

        var totalShareSubmitted = 0.0
        var totalLoanSubmitted = 0.0
        var totalChargeSubmitted = 0.0

        var shareSubmitted = 0.0
        var loanSubmitted = 0.0
        var chargeSubmitted = 0.0

        var shareApproved = 0.0
        var loanApproved = 0.0
        var chargeApproved = 0.0

        var query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            interestRate = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_INTEREST_RATE_COL))
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }


        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            shareAmount += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            sharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            loanApplication += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            loanPayout += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            loanToRepay += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            loanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            charge += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            chargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
        }

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            totalShareSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            totalLoanSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            totalChargeSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }

        virtualBalance = sharePayment + loanRepayment + chargePayment - totalLoanSubmitted
        actualBalance = sharePayment + loanRepayment + chargePayment - loanPayout

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(accountHolderModel[position].accountHoldersName))
        if (cursor.moveToFirst()) {
            shareSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loanSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            chargeSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }

        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ? AND ${DBHandler.TRANSACTION_MONTH_COL} = ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(accountHolderModel[position].accountHoldersName, transactionMonth))
        if (cursor.moveToFirst()) {
            shareApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            loanApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            chargeApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
        }

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {

            if (shareSubmitted != shareApproved) {
                holder.tvShares.setTextColor(Color.parseColor("#BA0707"))
                holder.tvShares.setTypeface(null, Typeface.BOLD_ITALIC)
            } else {
                holder.tvShares.setTextColor(Color.parseColor("#757575"))
                holder.tvShares.setTypeface(null, Typeface.ITALIC)
            }

            if (loanSubmitted != loanApproved) {
                holder.tvLoanApplication.setTextColor(Color.parseColor("#BA0707"))
                holder.tvLoanApplication.setTypeface(null, Typeface.BOLD_ITALIC)
            } else {
                holder.tvLoanApplication.setTextColor(Color.parseColor("#757575"))
                holder.tvLoanApplication.setTypeface(null, Typeface.ITALIC)
            }

            if (chargeSubmitted != chargeApproved) {
                holder.tvCharges.setTextColor(Color.parseColor("#BA0707"))
                holder.tvCharges.setTypeface(null, Typeface.BOLD_ITALIC)
            } else {
                holder.tvCharges.setTextColor(Color.parseColor("#757575"))
                holder.tvCharges.setTypeface(null, Typeface.ITALIC)
            }

            val arrears = shareAmount + loanToRepay + charge - loanApplication
            val payments = sharePayment + loanRepayment + chargePayment - loanPayout

            val contentValues = ContentValues()
            if (payments == arrears) {
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "No Arrears")
            } else {
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
            }

            if (holder.tvShares.text.toString().toDouble() == 0.0) {
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
            }

            if (shareSubmitted == 0.0) {
                holder.tvShares.setTextColor(Color.parseColor("#BA0707"))
                holder.tvShares.setTypeface(null, Typeface.BOLD_ITALIC)
            }


            db = AccountHolders.dbHandler.writableDatabase
            db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?",
                    arrayOf(accountHolderModel[position].accountHoldersName))

            holder.imgClearedAll.isGone = accountHolderModel[position].accountHoldersApproved != "No Arrears"
        }


        holder.itemView.setOnClickListener {

            val postsDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_posts, null)

            postsDialogLayout.etPostsShares.setText(accountHolderModel[position].accountHoldersShare.toString())
            postsDialogLayout.etPostsLoanApplication.setText(accountHolderModel[position].accountHoldersLoanApp.toString())
            postsDialogLayout.etPostsCharges.setText(accountHolderModel[position].accountHoldersCharges.toString())

            AlertDialog.Builder(mContext)
                    .setTitle("Submissions")
                    .setMessage("Virtual Balance: ${BigDecimal(virtualBalance).setScale(2, RoundingMode.HALF_EVEN)}")
                    .setView(postsDialogLayout)
                    .setPositiveButton("Submit", null)
                    .setNegativeButton("Cancel") { _, _ -> }
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                if (etPostsShares.text.toString() == "") {
                                    etPostsShares.setText("0")
                                }

                                if (etPostsLoanApplication.text.toString() == "") {
                                    etPostsLoanApplication.setText("0")
                                }

                                if (etPostsCharges.text.toString() == "") {
                                    etPostsCharges.setText("0")
                                }

                                if (postsDialogLayout.etPostsLoanApplication.text.toString().toDouble() > (virtualBalance + loanSubmitted)) {
                                    Toast.makeText(mContext, "Insufficient funds for loan application", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if (postsDialogLayout.etPostsShares.text.toString().toDouble() < shareSubmitted) {

                                    val difference = (shareApproved * shareValue) - (postsDialogLayout.etPostsShares.text.toString().toDouble() * shareValue)

                                    if (virtualBalance != actualBalance) {
                                        Toast.makeText(mContext, "Settle pending loan applications before reducing share", Toast.LENGTH_LONG).show()
                                        return@setOnClickListener
                                    }

                                    if (actualBalance + 0.1 < difference) {
                                        Toast.makeText(mContext, "Insufficient cash for $difference refund", Toast.LENGTH_SHORT).show()
                                        return@setOnClickListener
                                    }
                                }

                                val posts: Boolean = dbHandler.postings(mContext, accountHolderModelPosition.accountHoldersID,
                                        postsDialogLayout.etPostsShares.text.toString(),
                                        postsDialogLayout.etPostsLoanApplication.text.toString(),
                                        postsDialogLayout.etPostsCharges.text.toString())
                                if (posts) {
                                    accountHolderModel[position].accountHoldersShare = postsDialogLayout.etPostsShares.text.toString().toInt()
                                    accountHolderModel[position].accountHoldersLoanApp = postsDialogLayout.etPostsLoanApplication.text.toString().toDouble()
                                    accountHolderModel[position].accountHoldersCharges = postsDialogLayout.etPostsCharges.text.toString().toDouble()
                                    notifyDataSetChanged()
                                    Toast.makeText(mContext, "Submission Successful", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                } else {
                                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .show()


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

            val etFullNameName: EditText = addAccountHolderDialogLayout.etFullNames
            val etContactNo: EditText = addAccountHolderDialogLayout.etContactNo
            val etAccountInfo: EditText = addAccountHolderDialogLayout.etAccountInfo

            val etInsertPin: EditText = insertPINDialogLayout.etInsertPIN
            val etPinHint: EditText = insertPINDialogLayout.etPinHint

            //Add array to Spinner List in layout
            val arrayAdapter = ArrayAdapter(mContext, android.R.layout.simple_selectable_list_item, admin)
            addAccountHolderDialogLayout.spAdministrators.adapter = arrayAdapter

            var selectedAdmin = ""

            addAccountHolderDialogLayout.spAdministrators.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

            if (selectedAdmin != "Chairperson") {
                admin[item] = "Chairperson"
                admin[0] = accountHolderModelPosition.accountHoldersAdmin
            }

            AlertDialog.Builder(mContext)
                    .setTitle("Update or Delete Account")
                    .setIcon(R.drawable.ic_account_holders)
                    .setView(addAccountHolderDialogLayout)
                    .setNeutralButton("Delete") { _, _ ->
                        if (accountHolderModelPosition.accountHoldersAdmin == "Chairperson") {
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
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton("Yes") { _: DialogInterface, _: Int ->

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
                                        dbHandler.delAccountHolder(accountHolderModelPosition.accountHoldersID)
                                        dbHandler.delAccountHolderTransactions(accountHolderModelPosition.accountHoldersName)
                                        Toast.makeText(mContext, "$name has been deleted", Toast.LENGTH_SHORT).show()
                                        accountHolderModel.removeAt(position)
                                        notifyItemRemoved(position)
                                        notifyItemRangeRemoved(position, accountHolderModel.size)
                                        showConfirmDeleteLayout.dismiss()
                                        true
                                    }
                                }
                                .setNegativeButton("No") { _: DialogInterface, _: Int ->
                                }
                        alertDialog.show()
                    }

                    .setPositiveButton("Update", null)
                    .setNegativeButton("Cancel") { _, _ -> }
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                                if (addAccountHolderDialogLayout.etFullNames.text.isEmpty()) {
                                    Toast.makeText(mContext, "Please type Full Name", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if (addAccountHolderDialogLayout.etContactNo.text.isEmpty()) {
                                    Toast.makeText(mContext, "Please enter contact number", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if (addAccountHolderDialogLayout.etAccountInfo.text.isEmpty()) {
                                    Toast.makeText(mContext, "Please enter account or mobile banking info", Toast.LENGTH_LONG).show()
                                    return@setOnClickListener
                                }


                                if (selectedAdmin != "Account Holder") {
                                    val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_ADMIN_COL} = ?"
                                    val db = dbHandler.readableDatabase
                                    val cursor = db.rawQuery(query, arrayOf(selectedAdmin))
                                    if (cursor.count > 0){
                                        Toast.makeText(mContext, "Duplicate $selectedAdmin", Toast.LENGTH_LONG).show()
                                        return@setOnClickListener
                                    }

                                    val update: Boolean = dbHandler.editAccountHolder(mContext,
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
                                }



                                if (selectedAdmin == "Account Holder") {

                                    val update: Boolean = dbHandler.editAccountHolder(mContext,
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
                                }


                                if (selectedAdmin == "Chairperson") {
                                    val update: Boolean = dbHandler.editAccountHolder(mContext,
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
                                                setOnShowListener {
                                                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                                        if (etInsertPIN.text.isEmpty()) {
                                                            Toast.makeText(mContext, "Please type PIN", Toast.LENGTH_SHORT).show()
                                                            return@setOnClickListener
                                                        }

                                                        if (etPinRepeat.text.isEmpty()) {
                                                            Toast.makeText(mContext, "Please type repeat PIN", Toast.LENGTH_SHORT).show()
                                                            return@setOnClickListener
                                                        }

                                                        if (etPinHint.text.isEmpty()) {
                                                            Toast.makeText(mContext, "Please type hint", Toast.LENGTH_SHORT).show()
                                                            return@setOnClickListener
                                                        }

                                                        if (etInsertPIN.text.toString() != etPinRepeat.text.toString()) {
                                                            Toast.makeText(mContext, "PIN and repeat PIN do not match. Re-type", Toast.LENGTH_LONG).show()
                                                            return@setOnClickListener
                                                        }

                                                        if (etInsertPIN.text.length < 4) {
                                                            Toast.makeText(mContext, "PIN should be 4 digits", Toast.LENGTH_LONG).show()
                                                            return@setOnClickListener
                                                        }

                                                        val update2: Boolean = dbHandler.editAccountHolder(mContext,
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

                                }
                            }
                        }
                    }
                    .show()

            true
        }



        holder.btnPosting?.setOnClickListener {

            val id = holder.tvID.text
            val intent = Intent(mContext, AccountDetails::class.java)
            intent.putExtra("ID", id)
            holder.itemView.context.startActivity(intent)
        }



        holder.btnPosting?.setOnLongClickListener {

            AlertDialog.Builder(mContext)
                    .setTitle("Reset Submissions?")
                    .setMessage("Reset Shares, Loan and Charges?")
                    .setNegativeButton("No") { _, _ -> }
                    .setPositiveButton("Yes") { _, _ ->
                        val posts: Boolean = dbHandler.postings(mContext, accountHolderModelPosition.accountHoldersID,
                                0.0.toString(),
                                0.0.toString(),
                                0.0.toString())
                        if (posts) {
                            accountHolderModel[position].accountHoldersShare = 0
                            accountHolderModel[position].accountHoldersLoanApp = 0.0
                            accountHolderModel[position].accountHoldersCharges = 0.0
                            notifyDataSetChanged()
                            Toast.makeText(mContext, "Submissions Reset", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }

                    }
                    .show()
            true
        }

        cursor.close()
    }








    override fun getItemCount(): Int {
        return accountHolderModel.size
    }

}

