package com.villagebanking

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_details.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList


class AccountDetails : AppCompatActivity() {

    companion object {
        lateinit var dbHandler: DBHandler
    }

    //Format date to Month Year
    //Reference date for loan payout
/*    @RequiresApi(Build.VERSION_CODES.O)
    val now: YearMonth = YearMonth.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val lastMonth: YearMonth = now.minusMonths(1)
    @RequiresApi(Build.VERSION_CODES.O)
    val monthYearFormatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("MMMM yyyy")
    @RequiresApi(Build.VERSION_CODES.O)
    val transactionMonth: String = now.format(monthYearFormatter)
    @RequiresApi(Build.VERSION_CODES.O)
    val transactionLastMonth: String = lastMonth.format(monthYearFormatter)*/

    //Format date to day Month Year
    //Get date to insert


/*    val date = Calendar.getInstance().time
    @RequiresApi(Build.VERSION_CODES.N)
    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()
    @RequiresApi(Build.VERSION_CODES.N)
    val transactionDate: String = dateFormat.format(date)*/

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    val sdf = SimpleDateFormat("dd MMM yyyy")
    @RequiresApi(Build.VERSION_CODES.N)
    val transactionDate: String = sdf.format(Date())




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

        getAccountDetails()
        detectChanges()
    }



    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.transaction_menu, menu)
        return true
    }



    @SuppressLint("SimpleDateFormat", "NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.viewAll -> {
                viewTransactions()
            }
            R.id.information -> {
                getBankingDetails()
            }
            R.id.arrears_shares -> {
                viewArrearsShares()
            }
            R.id.arrears_loan_payout -> {
                viewArrearsLoanPayout()
            }
            R.id.arrears_loan_repayment -> {
                viewArrearsLoanRepayment()
            }
            R.id.arrears_charge_payment -> {
                viewArrearsCharges()
            }
            R.id.payments_shares -> {
                sharePayment()
            }
            R.id.payments_loan_payout -> {
                loanPayment()
            }
            R.id.payments_loan_repayments -> {
                loanRepayment()
            }
            R.id.payments_charges -> {
                chargePayment()
            }
        }
        return true
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
        }
        cursor.close()
        db.close()
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
            transactions.transactionInterest = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_INTEREST_COL))
            transactions.transactionName = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_NAME_COL))
            transactions.transactionShares = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            transactions.transactionShareAmount = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            transactions.transactionSharePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            transactions.transactionShareDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_DATE_COL))
            transactions.transactionLoanApp = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            transactions.transactionLoanToRepay = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            transactions.transactionLoanPayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            transactions.transactionLoanPaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL))
            transactions.transactionLoanRepayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            transactions.transactionLoanRepaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL))
            transactions.transactionCharge = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            transactions.transactionChargePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
            transactions.transactionChargePaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_DATE_COL))
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



    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    fun createMonth(){
        val name = tvDetailsName.text
        var share = 0
        var shareValue = 0.0
        var interest = 0.0
        var loan = 0.0
        var charge = 0.0
        var loanToRepay = 0.0

        val transactionModel = Model()

        var query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
            interest = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_INTEREST_RATE_COL))
        }

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?"
        db = dbHandler.readableDatabase
        cursor =  db.rawQuery(query, arrayOf(name.toString()))
        if(cursor.moveToFirst()){
            share = cursor.getInt(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loan = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            charge = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }
        cursor.close()
        db.close()

        interest = (interest/100) + 1

        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        val stf = java.text.SimpleDateFormat("kk:mm")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))
        val currentTime = (stf.format(c.time))

        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = ? AND ${DBHandler.TRANSACTION_NAME_COL} = ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(transactionLastMonth, name.toString()))
        if(cursor.count > 0){
            if(cursor.moveToFirst()){
                loanToRepay = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            }
        }else{
            loanToRepay = 0.0
        }
        cursor.close()
        db.close()

            transactionModel.transactionName = name.toString()
            transactionModel.transactionMonth = transactionMonth
            transactionModel.transactionShares = share
            transactionModel.transactionShareAmount = shareValue * share
            transactionModel.transactionShareDate = transactionDate
            transactionModel.transactionLoanApp = loan
            transactionModel.transactionLoanPaymentDate = transactionDate
            transactionModel.transactionLoanToRepay = loanToRepay * interest
            transactionModel.transactionLoanRepaymentDate = transactionDate
            transactionModel.transactionCharge = charge
            transactionModel.transactionChargePaymentDate = transactionDate
            transactionModel.transactionInterest = interest
            dbHandler.createMonth(this, transactionModel)
            populateInvestment()

        transactionModel.statementsMonth = transactionMonth
        transactionModel.statementsDate = transactionDate
        transactionModel.statementsTime = currentTime
        transactionModel.statementsName = name.toString()
        transactionModel.statementsAction = "Approved Submissions"
        transactionModel.statementsShare = share
        transactionModel.statementsShareAmount = shareValue * share
        transactionModel.statementsLoan = BigDecimal(loan).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        transactionModel.statementsCharge = BigDecimal(charge).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        dbHandler.getStatements(this, transactionModel)

    }



     @SuppressLint("SimpleDateFormat")
     private fun updateMonth(){

         val c: Calendar = GregorianCalendar()
         c.time = Date()
         val sdf = java.text.SimpleDateFormat("MMMM yyyy")
         val stf = java.text.SimpleDateFormat("kk:mm")
         //println(sdf.format(c.time)) // NOW
         val transactionMonth = (sdf.format(c.time))
         val currentTime = (stf.format(c.time))

        val name = tvDetailsName.text
        var share = 0
        var shareValue = 0.0
        var loan = 0.0
        var charge = 0.0

        var query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?"
        cursor =  db.rawQuery(query, arrayOf(name.toString()))
        if(cursor.moveToFirst()){
            share = cursor.getInt(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loan = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            charge = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }


        val transactionModel = Model()
        transactionModel.transactionName = name.toString()
        transactionModel.transactionMonth = transactionMonth
        transactionModel.transactionShares = share
        transactionModel.transactionShareAmount = (shareValue * share)
        transactionModel.transactionLoanApp = loan
        transactionModel.transactionCharge = charge
        dbHandler.updateMonth(this, transactionModel, name.toString(), transactionMonth)

        transactionModel.statementsMonth = transactionMonth
        transactionModel.statementsDate = transactionDate
        transactionModel.statementsTime = currentTime
        transactionModel.statementsName = name.toString()
        transactionModel.statementsAction = "Approved Submissions"
        transactionModel.statementsShare = share
        transactionModel.statementsShareAmount = BigDecimal(shareValue * share).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        transactionModel.statementsLoan = BigDecimal(loan).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        transactionModel.statementsCharge = BigDecimal(charge).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        dbHandler.getStatements(this, transactionModel)

         cursor.close()
         db.close()
    }



    @SuppressLint("SimpleDateFormat")
    private fun populateInvestment(){
        val name = tvDetailsName.text
        var lastMonthSharePayment = 0.0


        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))


        //Get previous month sharePayment to be populated as current share out
        var query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ? AND ${DBHandler.TRANSACTION_MONTH_COL} = ?"
        var db = dbHandler.readableDatabase
        var cursor =  db.rawQuery(query, arrayOf(name.toString(), transactionLastMonth))
        if (cursor.count != 0){
            if(cursor.moveToFirst()){
                lastMonthSharePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            }
        }else{
            lastMonthSharePayment = 0.0
        }
        cursor.close()
        db.close()

        //Insert previous month sharePayment as current share out
        //Done at the beginning of the month because the share had to work to gain interest
        val contentValues = ContentValues()
        contentValues.put(DBHandler.TRANSACTION_SHARE_OUT_COL, lastMonthSharePayment)
        db = dbHandler.writableDatabase
        db.update(DBHandler.TRANSACTION_TABLE, contentValues, "${DBHandler.TRANSACTION_MONTH_COL} = ? AND " +
                "${DBHandler.TRANSACTION_NAME_COL} = ?", arrayOf(transactionLastMonth, name.toString()))

        //Populate incremental interests for all past months
        query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_SHARE_OUT_COL} = ${DBHandler.TRANSACTION_SHARE_OUT_COL} * " +
                "${DBHandler.TRANSACTION_INTEREST_COL} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
        db = dbHandler.writableDatabase
        db.execSQL(query)
        db.close()
    }




    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun detectChanges(){
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))

        val name = tvDetailsName.text

        var shareSubmitted = 0.0
        var loanSubmitted = 0.0
        var chargeSubmitted = 0.0
        var shareApproved = 0.0
        var loanApproved = 0.0
        var chargeApproved = 0.0


        var query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, arrayOf(name.toString()))
        if (cursor.moveToFirst()) {
            shareSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loanSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            chargeSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }

        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ? AND ${DBHandler.TRANSACTION_MONTH_COL} = ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(name.toString(), transactionMonth))
        if (cursor.moveToFirst()) {
            shareApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            loanApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            chargeApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
        }

        if (shareApproved != shareSubmitted){
            checkMonth()
        }else if (loanApproved != loanSubmitted){
            checkMonth()
        }else if (chargeApproved != chargeSubmitted){
            checkMonth()
        }else{
            viewTransactions()
        }
    }



    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkMonth(){
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))

        val name = tvDetailsName.text

        var shareSubmitted = 0.0
        var loanSubmitted = 0.0
        var chargeSubmitted = 0.0
        var shareApproved = 0.0
        var loanApproved = 0.0
        var chargeApproved = 0.0


        var query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, arrayOf(name.toString()))
        if (cursor.moveToFirst()) {
            shareSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loanSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            chargeSubmitted = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }


        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ? AND ${DBHandler.TRANSACTION_MONTH_COL} = ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(name.toString(), transactionMonth))
        if (cursor.moveToFirst()) {
            shareApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            loanApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            chargeApproved = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
        }

        AlertDialog.Builder(this)
                .setTitle("Changes have been made?")
                .setPositiveButton("Approve") {_,_->

                    val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = ? AND ${DBHandler.TRANSACTION_NAME_COL} = ?"
                    val db = dbHandler.readableDatabase
                    val cursor = db.rawQuery(query, arrayOf(transactionMonth, name.toString()))
                    if (cursor.count == 0){
                        createMonth()
                        Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
                    }else{
                        updateMonth()
                        Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
                    }
                    cursor.close()
                    db.close()
                    viewTransactions()
                }

                .setNeutralButton("Undo") {_,_->
                    val contentValues = ContentValues()
                    contentValues.put(DBHandler.ACCOUNT_HOLDERS_SHARE_COL, shareApproved)
                    contentValues.put(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL, loanApproved)
                    contentValues.put(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL, chargeApproved)
                    db = dbHandler.writableDatabase
                    db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?", arrayOf(name.toString()))
                    val intent = Intent(this, AccountHolders::class.java)
                    startActivity(intent)
                    AccountDetails().finish()
                }

                .setNegativeButton("View") {_,_->
                    viewTransactions()
                }
                .show()
    }








    private fun getBankingDetails(){
        var bankDetails: String = ""
        var contactDetails: String = ""
        val name = tvDetailsName.text
        val queue = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(queue, arrayOf(name.toString()))
            if (cursor.moveToFirst()){
                bankDetails = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_BANK_INFO_COL))
                contactDetails = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CONTACT_COL))
                AlertDialog.Builder(this)
                        .setTitle("Information")
                        .setMessage("Contact Details: $contactDetails \n \n" +
                                "Bank Details: $bankDetails")
                        .setPositiveButton("OK") {_,_->}
                        .show()
            }
        cursor.close()
        db.close()
    }









    private fun getArrearsShares(): ArrayList<Model>{
        val name = tvDetailsName.text
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name' AND" +
                " ${DBHandler.TRANSACTION_SHARE_PAYMENT_COL} < ${DBHandler.TRANSACTION_SHARE_AMOUNT_COL}"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)
        val transactionsModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(this, "All Paid", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val transactions = Model()
            transactions.transactionID = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_ID_COL))
            transactions.transactionMonth = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_MONTH_COL))
            transactions.transactionInterest = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_INTEREST_COL))
            transactions.transactionName = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_NAME_COL))
            transactions.transactionShares = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            transactions.transactionShareAmount = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            transactions.transactionSharePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            transactions.transactionShareDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_DATE_COL))
            transactions.transactionLoanApp = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            transactions.transactionLoanToRepay = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            transactions.transactionLoanPayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            transactions.transactionLoanPaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL))
            transactions.transactionLoanRepayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            transactions.transactionLoanRepaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL))
            transactions.transactionCharge = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            transactions.transactionChargePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
            transactions.transactionChargePaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_DATE_COL))
            transactionsModel.add(transactions)
        }
        }
        cursor.close()
        db.close()
        return transactionsModel
    }

    private fun viewArrearsShares(){
        val transactionList = getArrearsShares()
        val adapter = CustomAdapter2(this, transactionList)
        val rv2: RecyclerView = recyclerView2
        rv2.setHasFixedSize(true)
        rv2.adapter = adapter
    }




    private fun getArrearsLoanPayout(): ArrayList<Model>{
        val name = tvDetailsName.text
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name' AND" +
                " ${DBHandler.TRANSACTION_LOAN_PAYMENT_COL} < ${DBHandler.TRANSACTION_LOAN_APP_COL}"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)
        val transactionsModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(this, "All Paid", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val transactions = Model()
            transactions.transactionID = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_ID_COL))
            transactions.transactionMonth = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_MONTH_COL))
            transactions.transactionInterest = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_INTEREST_COL))
            transactions.transactionName = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_NAME_COL))
            transactions.transactionShares = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            transactions.transactionShareAmount = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            transactions.transactionSharePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            transactions.transactionShareDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_DATE_COL))
            transactions.transactionLoanApp = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            transactions.transactionLoanToRepay = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            transactions.transactionLoanPayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            transactions.transactionLoanPaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL))
            transactions.transactionLoanRepayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            transactions.transactionLoanRepaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL))
            transactions.transactionCharge = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            transactions.transactionChargePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
            transactions.transactionChargePaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_DATE_COL))
            transactionsModel.add(transactions)
        }
        }
        cursor.close()
        db.close()
        return transactionsModel
    }

    private fun viewArrearsLoanPayout(){
        val transactionList = getArrearsLoanPayout()
        val adapter = CustomAdapter2(this, transactionList)
        val rv2: RecyclerView = recyclerView2
        rv2.setHasFixedSize(true)
        rv2.adapter = adapter
    }



    private fun getArrearsLoanRepayment(): ArrayList<Model>{
        val name = tvDetailsName.text
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name' AND" +
                " ${DBHandler.TRANSACTION_LOAN_REPAYMENT_COL} < ${DBHandler.TRANSACTION_LOAN_TO_REPAY_COL}"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)
        val transactionsModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(this, "All Paid", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val transactions = Model()
            transactions.transactionID = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_ID_COL))
            transactions.transactionMonth = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_MONTH_COL))
            transactions.transactionInterest = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_INTEREST_COL))
            transactions.transactionName = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_NAME_COL))
            transactions.transactionShares = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            transactions.transactionShareAmount = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            transactions.transactionSharePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            transactions.transactionShareDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_DATE_COL))
            transactions.transactionLoanApp = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            transactions.transactionLoanToRepay = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            transactions.transactionLoanPayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            transactions.transactionLoanPaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL))
            transactions.transactionLoanRepayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            transactions.transactionLoanRepaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL))
            transactions.transactionCharge = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            transactions.transactionChargePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
            transactions.transactionChargePaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_DATE_COL))
            transactionsModel.add(transactions)
        }
        }
        cursor.close()
        db.close()
        return transactionsModel
    }

    private fun viewArrearsLoanRepayment(){
        val transactionList = getArrearsLoanRepayment()
        val adapter = CustomAdapter2(this, transactionList)
        val rv2: RecyclerView = recyclerView2
        rv2.setHasFixedSize(true)
        rv2.adapter = adapter
    }



    private fun getArrearsCharges(): ArrayList<Model>{
        val name = tvDetailsName.text
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name' AND" +
                " ${DBHandler.TRANSACTION_CHARGE_PAYMENT_COL} < ${DBHandler.TRANSACTION_CHARGE_COL}"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)
        val transactionsModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(this, "All Paid", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val transactions = Model()
            transactions.transactionID = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_ID_COL))
            transactions.transactionMonth = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_MONTH_COL))
            transactions.transactionInterest = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_INTEREST_COL))
            transactions.transactionName = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_NAME_COL))
            transactions.transactionShares = cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            transactions.transactionShareAmount = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            transactions.transactionSharePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            transactions.transactionShareDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_DATE_COL))
            transactions.transactionLoanApp = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            transactions.transactionLoanToRepay = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            transactions.transactionLoanPayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            transactions.transactionLoanPaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL))
            transactions.transactionLoanRepayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            transactions.transactionLoanRepaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL))
            transactions.transactionCharge = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            transactions.transactionChargePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
            transactions.transactionChargePaymentDate = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_DATE_COL))
            transactionsModel.add(transactions)
        }
        }
        cursor.close()
        db.close()
        return transactionsModel
    }

    private fun viewArrearsCharges(){
        val transactionList = getArrearsCharges()
        val adapter = CustomAdapter2(this, transactionList)
        val rv2: RecyclerView = recyclerView2
        rv2.setHasFixedSize(true)
        rv2.adapter = adapter
    }



    @SuppressLint("SimpleDateFormat")
    private fun sharePayment(){
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        val stf = java.text.SimpleDateFormat("kk:mm")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))
        val currentTime = (stf.format(c.time))

        val name = tvDetailsName.text
        var totalCash = 0.0
        var totalShares = 0

        var query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ?"
        var db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(name.toString()))
        while (cursor.moveToNext()){
            totalCash += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            totalShares += cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
        }
        AlertDialog.Builder(this)
                .setTitle("Settle all share payments for $name?")
                .setPositiveButton("Yes") {_,_->
                    query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_SHARE_PAYMENT_COL} = " +
                            "${DBHandler.TRANSACTION_SHARE_AMOUNT_COL} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '${name}'"
                    db = dbHandler.writableDatabase
                    db.execSQL(query)
                    viewTransactions()
                }
                .setNegativeButton("No") {_,_->}
                .show()

        val contentValues = ContentValues()
        contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
        contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
        contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
        contentValues.put(DBHandler.STATEMENT_NAME, name.toString())
        contentValues.put(DBHandler.STATEMENT_ACTION, "Settled All Share Arrears")
        contentValues.put(DBHandler.STATEMENT_SHARE, totalShares)
        contentValues.put(DBHandler.STATEMENT_SHARE_AMOUNT, BigDecimal(totalCash).setScale(2, RoundingMode.HALF_EVEN).toDouble())
        db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)
    }


    @SuppressLint("SimpleDateFormat")
    private fun loanPayment(){
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        val stf = java.text.SimpleDateFormat("kk:mm")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))
        val currentTime = (stf.format(c.time))

        val name = tvDetailsName.text
        var totalCash = 0.0

        var query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ?"
        var db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(name.toString()))
        while (cursor.moveToNext()){
            totalCash += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
        }


        AlertDialog.Builder(this)
                .setTitle("Clear all loan payouts for $name?")
                .setPositiveButton("Yes") {_,_->
                    query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_LOAN_PAYMENT_COL} = " +
                            "${DBHandler.TRANSACTION_LOAN_APP_COL} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '${name}'"
                    db = dbHandler.writableDatabase
                    db.execSQL(query)
                    viewTransactions()
                }
                .setNegativeButton("No") {_,_->}
                .show()

        val contentValues = ContentValues()
        contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
        contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
        contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
        contentValues.put(DBHandler.STATEMENT_NAME, name.toString())
        contentValues.put(DBHandler.STATEMENT_ACTION, "Paid All Loan Applications")
        contentValues.put(DBHandler.STATEMENT_LOAN, BigDecimal(totalCash).setScale(2, RoundingMode.HALF_EVEN).toDouble())
        db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

    }


    @SuppressLint("SimpleDateFormat")
    private fun loanRepayment(){
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        val stf = java.text.SimpleDateFormat("kk:mm")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))
        val currentTime = (stf.format(c.time))

        val name = tvDetailsName.text
        var totalCash = 0.0

        var query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ?"
        var db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(name.toString()))
        while (cursor.moveToNext()){
            totalCash += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
        }
        AlertDialog.Builder(this)
                .setTitle("Settle all loan repayments for $name?")
                .setPositiveButton("Yes") {_,_->
                    query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_LOAN_REPAYMENT_COL} = " +
                            "${DBHandler.TRANSACTION_LOAN_TO_REPAY_COL} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '${name}'"
                    db = dbHandler.writableDatabase
                    db.execSQL(query)
                    viewTransactions()
                }
                .setNegativeButton("No") {_,_->}
                .show()
        val contentValues = ContentValues()
        contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
        contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
        contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
        contentValues.put(DBHandler.STATEMENT_NAME, name.toString())
        contentValues.put(DBHandler.STATEMENT_ACTION, "Settled All Loan Repayments")
        contentValues.put(DBHandler.STATEMENT_LOAN, BigDecimal(totalCash).setScale(2, RoundingMode.HALF_EVEN).toDouble())
        db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)
    }


    @SuppressLint("SimpleDateFormat")
    private fun chargePayment(){
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        val stf = java.text.SimpleDateFormat("kk:mm")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))
        val currentTime = (stf.format(c.time))

        val name = tvDetailsName.text
        var totalCash = 0.0

        var query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ?"
        var db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(name.toString()))
        while (cursor.moveToNext()){
            totalCash += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
        }
        AlertDialog.Builder(this)
                .setTitle("Clear all charges for $name?")
                .setPositiveButton("Yes") {_,_->
                    query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_CHARGE_PAYMENT_COL} = " +
                            "${DBHandler.TRANSACTION_CHARGE_COL} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '${name}'"
                    db = dbHandler.writableDatabase
                    db.execSQL(query)
                    viewTransactions()
                }
                .setNegativeButton("No") {_,_->}
                .show()

        val contentValues = ContentValues()
        contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
        contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
        contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
        contentValues.put(DBHandler.STATEMENT_NAME, name.toString())
        contentValues.put(DBHandler.STATEMENT_ACTION, "Settled All Charges")
        contentValues.put(DBHandler.STATEMENT_CHARGE, BigDecimal(totalCash).setScale(2, RoundingMode.HALF_EVEN).toDouble())
        db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)
    }



}



