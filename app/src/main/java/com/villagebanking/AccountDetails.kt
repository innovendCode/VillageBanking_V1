package com.villagebanking

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_details.*
import kotlinx.android.synthetic.main.bank_info.view.*
import kotlinx.android.synthetic.main.dialog_payments.view.*
import kotlinx.android.synthetic.main.dialog_posts.view.*
import kotlinx.android.synthetic.main.main_row_layout.*
import kotlinx.android.synthetic.main.sub_row_layout.*
import java.util.*
import kotlin.collections.ArrayList








class AccountDetails : AppCompatActivity() {

    companion object {
        lateinit var dbHandler: DBHandler
    }

    //val name = tvDetailsName.text

    //Format date to Month Year
    //Reference date for loan payout
    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    val monthFormat = SimpleDateFormat("MMMM yyyy")
    @RequiresApi(Build.VERSION_CODES.N)
    val transactionMonth: String = monthFormat.format(date)
    //Format date to day Month Year
    //Get date to insert
    //Insert loan payout date
    @RequiresApi(Build.VERSION_CODES.N)
    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()
    @RequiresApi(Build.VERSION_CODES.N)
    val transactionDate: String = dateFormat.format(date)




    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_details)

        dbHandler = DBHandler(this, null, null, 1)



        val intent = intent
        val id = intent.getStringExtra("ID")
        tvDetailsID.text = id


        val actionBar = supportActionBar
        actionBar!!.title = "Transactions"
        actionBar.setDisplayHomeAsUpEnabled(true)

        getAccountDetails()
        viewTransactions()
    }


    override fun onRestart() {
        viewTransactions()
        super.onRestart()
    }




    private fun getAccountDetails(){

        val intent = intent
        val ids = intent.getStringExtra("ID")
        tvDetailsID.text = ids


        val id = tvDetailsID.text
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_ID_COL} = '$id'"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            tvDetailsName.text = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_NAME_COL))
            tvDetailsShares.text = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            tvDetailsLoan.text = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            tvDetailsCharge.text = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }
        cursor.close()
        db.close()



        viewTransactions()
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.transaction_menu, menu)
        return true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sharesPayment -> {
                shareCollected()
            }
            R.id.loanPayOut -> {
                loanPayout()
            }
            R.id.loanRepayment -> {
            }
            R.id.bankAccount -> {
                getBankingDetails()
            }
            R.id.posts -> {
                posts()
            }
        }
        return true
    }




    private fun getTransactions(): ArrayList<Model>{
        val name = tvDetailsName.text
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)

        val transactionsModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(this, "No Transactions Found", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val transactions = Model()
            transactions.transactionID = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_ID_COL))
            transactions.transactionMonth = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_MONTH_COL))
            transactions.transactionShares = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            transactions.transactionLoan = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            transactions.transactionLoanDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_DATE_LOAN_COL))
            transactions.transactionShareDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_DATE_SHARE_COL))
            transactions.transactionShareAmount = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            transactions.transactionSharePaid = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAID_COL))
            transactionsModel.add(transactions)
        }
        }
        cursor.close()
        db.close()
        return transactionsModel
    }


    private fun viewTransactions(){
        val transactionList = getTransactions()
        val adapter = CustomAdapter2(this, transactionList)
        val rv2: RecyclerView = recyclerView2
        rv2.setHasFixedSize(true)
        rv2.adapter = adapter

        adapter.notifyDataSetChanged()
    }


    private fun getBankingDetails(){
            val name = tvDetailsName.text
            val queue = "SELECT ${DBHandler.ACCOUNT_HOLDERS_BANK_INFO_COL} FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = '$name'"
            val db = dbHandler.writableDatabase
            val cursor = db.rawQuery(queue, null)
            if (cursor.moveToFirst()){
                val bankInfoDialogLayout = LayoutInflater.from(this).inflate(R.layout.bank_info, null)
                val bankInfoDialog = AlertDialog.Builder(this)
                        .setView(bankInfoDialogLayout)
                        .setPositiveButton("OK") {_:DialogInterface, _: Int ->}
                val bankDetails = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_BANK_INFO_COL))
                bankInfoDialogLayout.tvBankingInfo.setText(bankDetails)
                bankInfoDialog.show()
            }else{
                Toast.makeText(this, "Error finding banking info", Toast.LENGTH_SHORT).show()
            }
    }



    private fun shareCollected(){
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = '$transactionMonth'"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.count == 0){
            sharesSubmittedFirstPay(this)
        }else{
            sharesSubmittedUpdate(this)
            Toast.makeText(this,"Share Updated",Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        db.close()
    }





    private fun sharesSubmittedFirstPay(context: Context){
        val paymentsDialogLayout = LayoutInflater.from(this ).inflate(R.layout.dialog_payments, null)
        val etPayments = paymentsDialogLayout.etPayments
        val name = tvDetailsName.text
        val share = tvDetailsShares.text.toString().toDouble()

        //Restrict Zero Share Contribution
        //User alert dialog that could be seen
        if(tvDetailsShares.text.toString() == "0"){
            val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Information - (0)Zero Shares Error")
                    .setMessage("You cannot submit (0)zero shares. Shares value must be a minimum of (1)one")
                    .setIcon(R.drawable.ic_info)
                    .setNegativeButton("OK") {_:DialogInterface, _: Int ->}
            alertDialog.show()
            return
        }


        val query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        var shareValue = 0.0
        val shareAmount: Double

        if (cursor.moveToFirst()){
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }

        shareAmount = shareValue * share

        etPayments.setText(shareAmount.toString())
        AlertDialog.Builder(context)
                .setView(paymentsDialogLayout)
                .setMessage("How much is being paid toward the $shareAmount share?")
                .setPositiveButton("Received", null)
                .setNegativeButton("Cancel") {_,_ ->}
                .create().apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            //Insert Shares for current month
                            //To reverse shares month has to be deletes
                            //Date and time included for zero loan. Loan is always an updated entry
                            val contentValues = ContentValues()
                            val payments = etPayments.text.toString()

                            //Restrict overpayment
                            if (payments.toDouble() > shareAmount){
                                Toast.makeText(context,"Payment is more than share",Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            db = dbHandler.writableDatabase

                            contentValues.put(DBHandler.TRANSACTION_NAME_COL, name.toString())
                            contentValues.put(DBHandler.TRANSACTION_SHARE_COL, share)
                            contentValues.put(DBHandler.TRANSACTION_MONTH_COL, transactionMonth)
                            contentValues.put(DBHandler.TRANSACTION_DATE_SHARE_COL, transactionDate)
                            contentValues.put(DBHandler.TRANSACTION_DATE_LOAN_COL, transactionDate)
                            contentValues.put(DBHandler.TRANSACTION_SHARE_AMOUNT_COL, shareAmount)
                            contentValues.put(DBHandler.TRANSACTION_SHARE_PAID_COL, payments)
                            db.insert(DBHandler.TRANSACTION_TABLE, null, contentValues)
                            finish();
                            startActivity(intent);
                            dismiss()


                        }
                    }
                }
                .show()
    }






    private fun sharesSubmittedUpdate(context: Context){
        val paymentsDialogLayout = LayoutInflater.from(this ).inflate(R.layout.dialog_payments, null)
        val etPayments = paymentsDialogLayout.etPayments
        val name = tvDetailsName.text
        val share = tvDetailsShares.text.toString().toDouble()

        //Restrict Zero Share Contribution
        //User alert dialog that could be seen
        if(tvDetailsShares.text.toString() == "0"){
            val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Information - (0)Zero Shares Error")
                    .setMessage("You cannot submit (0)zero shares. Shares value must be a minimum of (1)one")
                    .setIcon(R.drawable.ic_info)
                    .setNegativeButton("OK") {_:DialogInterface, _: Int ->}
            alertDialog.show()
            return
        }


        val query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        var shareValue = 0.0
        val shareAmount: Double

        if (cursor.moveToFirst()){
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }

        shareAmount = shareValue * share
        var paidAmount: Double = tvTransactionSharePaid.text.toString().toDouble()

        val difference = shareAmount - paidAmount

        etPayments.setText(difference.toString())
        AlertDialog.Builder(context)
                .setView(paymentsDialogLayout)
                .setMessage("How much is being paid toward the ${(shareAmount - tvTransactionSharePaid.text.toString().toDouble())} outstanding share?")
                .setPositiveButton("Received", null)
                .setNegativeButton("Cancel") {_,_ ->}
                .create().apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            //Insert Shares for current month
                            //To reverse shares month has to be deletes
                            //Date and time included for zero loan. Loan is always an updated entry
                            val contentValues = ContentValues()
                            val payments = etPayments.text.toString().toDouble()


                            val sum = paidAmount + payments

                            //Restrict overpayment
                            if (payments.toDouble() > difference){
                                Toast.makeText(context,"Payment is more than the balance",Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            db = dbHandler.writableDatabase

                            contentValues.put(DBHandler.TRANSACTION_SHARE_COL, share)
                            contentValues.put(DBHandler.TRANSACTION_DATE_SHARE_COL, transactionDate)
                            contentValues.put(DBHandler.TRANSACTION_SHARE_AMOUNT_COL, shareAmount)
                            contentValues.put(DBHandler.TRANSACTION_SHARE_PAID_COL, sum)
                            db.update(DBHandler.TRANSACTION_TABLE, contentValues, "${DBHandler.TRANSACTION_MONTH_COL} = '$transactionMonth' AND ${DBHandler.TRANSACTION_NAME_COL} = '$name'", arrayOf())
                            viewTransactions()

                         dismiss()
                        }
                    }
                }
                .show()



    }






    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    fun loanPayout(){
        val name = tvDetailsName.text
        val loan = tvDetailsLoan.text

        val contentValues = ContentValues()
        var db = dbHandler.readableDatabase

        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name' AND ${DBHandler.TRANSACTION_MONTH_COL} = '$transactionMonth'"
        val cursor = db.rawQuery(query, null)
        if (cursor.count == 0){
            Toast.makeText(this, "Please collect share contributions before loan payout", Toast.LENGTH_SHORT).show()
            return
        }

        if (loan == "0.0"){
            val alertDialog = AlertDialog.Builder(this)
                    .setTitle("No Loan Application")
                    .setMessage("$name has not applied for any loan. Proceed with no application.")
                    .setIcon(R.drawable.ic_info)
                    .setPositiveButton("OK") {_:DialogInterface, _: Int ->}
            alertDialog.show()
        }

        db = dbHandler.writableDatabase
        contentValues.put(DBHandler.TRANSACTION_LOAN_APP_COL, loan.toString())
        contentValues.put(DBHandler.TRANSACTION_DATE_LOAN_COL, transactionDate)

        db.update(DBHandler.TRANSACTION_TABLE, contentValues, "${DBHandler.TRANSACTION_MONTH_COL} = '$transactionMonth' AND ${DBHandler.TRANSACTION_NAME_COL} = '$name'", arrayOf())
        zeroLoanApplicationAccountHoldersTable()
        db.close()
    }



    private fun zeroSharesAccountHoldersTable(){
        val name = tvDetailsName.text
        val contentValues = ContentValues()
        contentValues.put(DBHandler.ACCOUNT_HOLDERS_SHARE_COL, 0)
        val db = dbHandler.writableDatabase
        db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = '$name'", arrayOf())
        Toast.makeText(this, "$name 's share investment submitted", Toast.LENGTH_SHORT).show()
        db.close()
        tvDetailsShares.text = "0"
    }



    private fun zeroLoanApplicationAccountHoldersTable(){
        val name = tvDetailsName.text
        val contentValues = ContentValues()
        contentValues.put(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL, 0.0)
        val db = dbHandler.writableDatabase
        db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = '$name'", arrayOf())
        Toast.makeText(this, "$name 's loan application paid out", Toast.LENGTH_SHORT).show()
        db.close()
        viewTransactions()
        tvDetailsLoan.text = "0.0"
    }



    private fun posts(){
        val postsDialog = LayoutInflater.from(this).inflate(R.layout.dialog_posts, null)
        val name = tvDetailsName.text
        val etPostsShares : EditText = postsDialog.etPostsShares
        val etPostsLoanApplication : EditText = postsDialog.etPostsLoanApplication
        val etPostsCharges : EditText = postsDialog.etPostsCharges

        AlertDialog.Builder(this)
            .setTitle("$name")
            .setMessage("Shares, Loan Application & Charges")
            .setIcon(R.drawable.ic_money)
            .setView(postsDialog)
            .setPositiveButton("Submit", null)
            .setNegativeButton("Cancel"){_,_ ->}
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val db = dbHandler.writableDatabase
                        val contentValues = ContentValues()
                        contentValues.put(DBHandler.ACCOUNT_HOLDERS_SHARE_COL, etPostsShares.text.toString())
                        contentValues.put(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL, etPostsLoanApplication.text.toString())
                        contentValues.put(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL, etPostsCharges.text.toString())
                        db.update(DBHandler.ACCOUNT_HOLDERS_TABLE,contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = '$name'", arrayOf())



                        getAccountDetails()


                        updatePost()
                        viewTransactions()
                        db.close()
                        dismiss()
                    }
                }
            }
            .show()
    }



    fun updatePost(){
        val name = tvDetailsName.text
        val share = tvDetailsShares.text.toString().toDouble()

        var shareValue = 0.0
        var shareAmount : Double

        var query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }

        shareAmount = share * shareValue

        Toast.makeText(this, shareAmount.toString(), Toast.LENGTH_SHORT).show()

        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = '$transactionMonth' AND ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)

        if (cursor.count == 1){
            val contentValues = ContentValues()

            contentValues.put(DBHandler.TRANSACTION_SHARE_COL, share)
            contentValues.put(DBHandler.TRANSACTION_DATE_SHARE_COL, transactionDate)
            contentValues.put(DBHandler.TRANSACTION_SHARE_AMOUNT_COL, shareAmount)

            db.update(DBHandler.TRANSACTION_TABLE, contentValues, "${DBHandler.TRANSACTION_MONTH_COL} = '$transactionMonth' AND ${DBHandler.TRANSACTION_NAME_COL} = '$name'", arrayOf())
            db.close()
            cursor.close()
        }

    }



}

