package com.villagebanking

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_details.*
import kotlinx.android.synthetic.main.bank_info.view.*
import java.util.*
import kotlin.collections.ArrayList

class AccountDetails : AppCompatActivity() {

    companion object {
        lateinit var dbHandler: DBHandler
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_details)

        dbHandler = DBHandler(this, null, null, 1)

        val intent = intent
        val name = intent.getStringExtra("Name")
        val shares = intent.getStringExtra("Shares")
        val loan = intent.getStringExtra("Loan")
        val tvDetailsName : TextView = tvDetailsName
        val tvDetailsShares : TextView = tvDetailsShares
        val tvDetailsLoan : TextView = tvDetailsLoan

        tvDetailsName.text = name
        tvDetailsShares.text = shares
        tvDetailsLoan.text = loan

        viewTransactions()
        getBankingDetails()

        val actionBar = supportActionBar
        actionBar!!.title = "Transactions"
        actionBar.setDisplayHomeAsUpEnabled(true)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.transaction_menu, menu)
        return true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sharesPayment -> {
                shareCollected(AccountHolderModel())
            }
            R.id.loanPayOut -> {
                loanPayout()
            }
            R.id.loanRepayment -> {
            }
        }




        return true
    }




    private fun getTransactions(): ArrayList<AccountHolderModel>{
        val name = tvDetailsName.text
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)

        val transactionsModel = ArrayList<AccountHolderModel>()
        if(cursor.count == 0)
            Toast.makeText(this, "No Transactions Found", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val transactions = AccountHolderModel()
            transactions.transactionID = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_ID_COL))
            transactions.transactionMonth = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_MONTH_COL))
            transactions.transactionShares = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            transactions.transactionLoan = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            transactions.transactionLoanDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_DATE_LOAN_COL))
            transactions.transactionShareDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_DATE_SHARE_COL))
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
    }



    private fun getBankingDetails(){
        val btnDetailsBankInfo : Button = btnDetailsBankInfo
        btnDetailsBankInfo.setOnClickListener {
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





    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    fun shareCollected(accountHolderModel: AccountHolderModel){

        val name = tvDetailsName.text

            //Format date to Month Year
            //Get month to insert
        val date = Calendar.getInstance().time
        val monthFormat = SimpleDateFormat("MMMM yyyy")
        val transactionMonth = monthFormat.format(date)
        //Format date to day Month Year
        //Get date to insert
        //Share collected date
        val dateFormat = SimpleDateFormat.getDateInstance()
        val transactionDate = dateFormat.format(date)


            //Check for duplicate entry
            //Check if data appears in table and return if it does.
            //If not proceed with insertion
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} " +
                "WHERE ${DBHandler.TRANSACTION_MONTH_COL} = '$transactionMonth'" +
                " AND ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
        var db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.count == 1){
            Toast.makeText(this, "${tvDetailsName.text.toString()}'s shares already received", Toast.LENGTH_SHORT).show()
            return
        }


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

            //Insert Shares for current month
            //To reverse shares month has to be deletes
            //Date and time included for zero loan. Loan is always an updated entry
            val contentValues = ContentValues()
            db = dbHandler.readableDatabase
            contentValues.put(DBHandler.TRANSACTION_NAME_COL, tvDetailsName.text.toString())
            contentValues.put(DBHandler.TRANSACTION_SHARE_COL, tvDetailsShares.text.toString())
            contentValues.put(DBHandler.TRANSACTION_MONTH_COL, transactionMonth)
            contentValues.put(DBHandler.TRANSACTION_DATE_SHARE_COL, transactionDate)
            contentValues.put(DBHandler.TRANSACTION_DATE_LOAN_COL, transactionDate)
            db.insert(DBHandler.TRANSACTION_TABLE, null, contentValues)
            viewTransactions()
        zeroSharesAccountHoldersTable()
        cursor.close()
        db.close()
    }



    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    fun loanPayout(){
        val name = tvDetailsName.text
        val loan = tvDetailsLoan.text
        //Format date to Month Year
        //Reference date for loan payout
        val date = Calendar.getInstance().time
        val monthFormat = SimpleDateFormat("MMMM yyyy")
        val transactionMonth = monthFormat.format(date)
        //Format date to day Month Year
        //Get date to insert
        //Insert loan payout date
        val dateFormat = SimpleDateFormat.getDateInstance()
        val transactionDate = dateFormat.format(date)
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

}

