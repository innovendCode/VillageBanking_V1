package com.villagebanking

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import kotlinx.android.synthetic.main.home.*
import kotlin.math.roundToLong


class Home: AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val actionbar = supportActionBar
        actionbar!!.title = "Home"

       dbHandler = DBHandler(this, null, null, 1)

        getNoMembers()
        getTransactions()
        //getSettings()
        myScreenLightBroadcast
    }


    override fun onRestart() {
        getNoMembers()
        getTransactions()
        super.onRestart()
    }


    override fun onBackPressed() {
        logOff()
        return
    }

    private fun getNoMembers(){
        var query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)

        val noMembers = "Members: ${cursor.count}"

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_ARREARS_COL} = ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(""))

        val membersWithArrears = "Members: ${cursor.count}"

        tvNoAccountHolders.text = noMembers
        tvMembersArrears.text = membersWithArrears

        cursor.close()
        db.close()
    }




    private fun getTransactions(){
        var interestRate = 0.0
        var shareValue = 0.0

        var shares = 0.0
        var shareAmount = 0.0
        var sharePayment = 0.0
        var loanPayout = 0.0
        var loanToRepay = 0.0
        var loanRepayment = 0.0
        var charge = 0.0
        var chargePayment = 0.0
        var currentShareOut = 0.0
        var arrears = 0.0
        var cash = 0.0

        var query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()){
            interestRate = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_INTEREST_RATE_COL))
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }

        val showInterestRate = "Interest Rate: $interestRate%"
        val showShareValue = "Share Value: $shareValue"
        tvInterestRate.text = showInterestRate
        tvShareValue.text = showShareValue
        cursor.close()




        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            shares += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
            shareAmount += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
            sharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            loanPayout += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            loanToRepay += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))

            loanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            charge += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
            chargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
            currentShareOut += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_OUT_COL))
        }

        arrears = shareAmount + loanPayout + charge
        cash = sharePayment + loanRepayment + chargePayment


        val totalShares = "Total Shares: ${(sharePayment/shareValue).roundToLong()*10.0/10.0}"
        val totalShareAmount = "Total Share Value: ${sharePayment.roundToLong()*10.0/10.0}"
        val totalInvestment = "Total Investment: ${currentShareOut.roundToLong()*10.0/10.0}"

        val availableCash = "Available Cash: ${(cash - loanPayout).roundToLong()*10.0/10.0}"

        val unpaidLoans = "Loans: ${loanToRepay - loanRepayment.roundToLong()*10.0/10.0}"
        val unpaidCharges = "Charges: ${charge - chargePayment.roundToLong()*10.0/10.0}"

        tvTotalShares.text = totalShares
        tvTotalSharesAmount.text= totalShareAmount
        tvTotalInvestment.text = totalInvestment

        tvTotalCash.text = availableCash

        tvUnpaidLoans.text = unpaidLoans
        tvUnpaidCharges.text = unpaidCharges

        cursor.close()
        db.close()
    }













    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.accountHolders -> {
                val intent = Intent(this, AccountHolders::class.java)
                startActivity(intent)
            }
            R.id.logOff -> {
                logOff()
            }
            R.id.settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
        }



        return true
    }



    private fun logOff() {
        AlertDialog.Builder(this)
                .setTitle("Log Off")
                .setMessage("Are you sure you want to log off?")
                .setIcon(R.drawable.ic_key)
                .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    finish()
                }
                .setNegativeButton("No") { _: DialogInterface, _: Int ->
                }
        .show()
    }


    var myScreenLightBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        // This will fire whenever Screen Light will be ON or OFF
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                // Screen Light is On so you can close your all Activity Except your MainActivity
                // Or you can move user to MainActivity by starting a NewActivity or Restarting Application
                finish()
            } else if(intent.action == Intent.ACTION_SCREEN_OFF) {
                // this will call whenever your Screen will OFF
                finish()
            }
        }
    }














}




