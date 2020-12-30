package com.villagebanking

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_details.*
import kotlinx.android.synthetic.main.bank_info.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.math.roundToLong


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
    val date = Calendar.getInstance().time
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

        getAccountDetails()
        checkMonth()
    }


    override fun onBackPressed() {
        updateShareOut()
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
            R.id.sharesPayment -> {
            }
            R.id.loanPayOut -> {
            }
            R.id.loanRepayment -> {
            }
            R.id.bankAccount -> {
                getBankingDetails()
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


    @RequiresApi(Build.VERSION_CODES.N)
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
            transactions.transactionShares = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
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



    @RequiresApi(Build.VERSION_CODES.N)
    private fun viewTransactions(){
        val transactionList = getTransactions()
        val adapter = CustomAdapter2(this, transactionList)
        val rv2: RecyclerView = recyclerView2
        rv2.setHasFixedSize(true)
        rv2.adapter = adapter
    }



    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createMonth(){
        val name = tvDetailsName.text
        var share = 0.0
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
            share = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loan = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            charge = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }
        cursor.close()
        db.close()

        interest = (interest/100) + 1


        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))



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
            transactionModel.transactionShares = share.roundToLong() *10.0/10.0
            transactionModel.transactionShareAmount = (shareValue * share).roundToLong() *10.0/10.0
            transactionModel.transactionShareDate = transactionDate
            transactionModel.transactionLoanApp = loan.roundToLong() *10.0/10.0
            transactionModel.transactionLoanPaymentDate = transactionDate
            transactionModel.transactionLoanToRepay = (loanToRepay * interest).roundToLong() *10.0/10.0
            transactionModel.transactionLoanRepaymentDate = transactionDate
            transactionModel.transactionCharge = charge.roundToLong() *10.0/10.0
            transactionModel.transactionChargePaymentDate = transactionDate
            transactionModel.transactionInterest = interest
            dbHandler.createMonth(this, transactionModel)
            populateInvestment()

    }



     @SuppressLint("SimpleDateFormat")
     private fun updateMonth(){
        val name = tvDetailsName.text
        var share = 0.0
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
        db = dbHandler.readableDatabase
        cursor =  db.rawQuery(query, arrayOf(name.toString()))
        if(cursor.moveToFirst()){
            share = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loan = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            charge = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }
        cursor.close()
        db.close()


         val c: Calendar = GregorianCalendar()
         c.time = Date()
         val sdf = java.text.SimpleDateFormat("MMMM yyyy")
         //println(sdf.format(c.time)) // NOW
         val transactionMonth = (sdf.format(c.time))


        val transactionModel = Model()
        transactionModel.transactionName = name.toString()
        transactionModel.transactionMonth = transactionMonth
        transactionModel.transactionShares = share.roundToLong()*10.0/10.0
        transactionModel.transactionShareAmount = (shareValue * share).roundToLong() *10.0/10.0
        //transactionModel.transactionSharePayment
        transactionModel.transactionLoanApp = loan.roundToLong()*10.0/10.0
        //transactionModel.transactionLoanPayment
        //transactionModel.transactionLoanRepayment
        transactionModel.transactionCharge = charge.roundToLong()*10.0/10.0
        //transactionModel.transactionChargePayment
        dbHandler.updateMonth(this, transactionModel, name.toString(), transactionMonth)
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
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = ? AND ${DBHandler.TRANSACTION_NAME_COL} = ?"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(transactionMonth, name.toString()))
        if (cursor.count == 0){
            createMonth()
            Toast.makeText(this, "New Month", Toast.LENGTH_SHORT).show()
        }else{
            updateMonth()
            Toast.makeText(this, transactionMonth, Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        db.close()
        viewTransactions()
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
                        .setPositiveButton("OK") { _: DialogInterface, _: Int ->}
                val bankDetails = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_BANK_INFO_COL))
                bankInfoDialogLayout.tvBankingInfo.setText(bankDetails)
                bankInfoDialog.show()
            }else{
                Toast.makeText(this, "Error finding banking info", Toast.LENGTH_SHORT).show()
            }
        cursor.close()
        db.close()
    }







    private fun updateShareOut(){
        val name = tvDetailsName.text
        var currentShareOut = 0.0
        var shareAmount = 0.0
        var sharePayment = 0.0
        var loanApp = 0.0
        var loanPayout = 0.0
        var loanToRepay = 0.0
        var loanRepayment = 0.0
        var charge = 0.0
        var chargePayment = 0.0
        var arrears = 0.0
        var payments = 0.0

        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ?"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(name.toString()))

        while (cursor.moveToNext()){
            currentShareOut += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_OUT_COL))
            shareAmount += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            sharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            loanApp += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            loanPayout += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            loanToRepay += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            loanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            charge += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            chargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
        }

        arrears = shareAmount + loanToRepay + charge - loanApp
        payments = sharePayment + loanRepayment + chargePayment - loanPayout

        val contentValues = ContentValues()
        if (arrears == payments){
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "No Arrears")
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_ASSET_COL, currentShareOut.roundToInt()*10.0/10.0)
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_LIABILITY_COL, (payments - arrears).roundToLong()*10.0/10.0)
        }else{
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_ASSET_COL, currentShareOut.roundToInt()*10.0/10.0)
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_LIABILITY_COL, (payments - arrears).roundToLong()*10.0/10.0)
        }
        db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?", arrayOf(name.toString()))
        cursor.close()
    }





    private fun checkMonthlyPayments(){
        var month : String = ""
        var shareAmount = 0.0
        var sharePayment = 0.0
        var loanToPay = 0.0
        var loanRepayment = 0.0
        var charge = 0.0
        var chargePayment = 0.0
        var arrears = 0.0
        var payments = 0.0

        val contentValues = ContentValues()


        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        var db = AccountHolders.dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)



        while (cursor.moveToNext()){
            month = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_MONTH_COL))
            shareAmount += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            sharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            loanToPay += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
            loanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            charge += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            chargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))

            arrears = shareAmount + loanToPay + charge
            payments = sharePayment + loanRepayment + chargePayment

            if (arrears == payments){
                contentValues.put(DBHandler.TRANSACTION_ARREARS_COL, "No Arrears")
                db.update(DBHandler.TRANSACTION_TABLE, contentValues, "${DBHandler.TRANSACTION_MONTH_COL} = ?", arrayOf(month))
            }else{
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
                db.update(DBHandler.TRANSACTION_TABLE, contentValues, "${DBHandler.TRANSACTION_MONTH_COL} = ?", arrayOf(month))
            }
        }



    }









}



