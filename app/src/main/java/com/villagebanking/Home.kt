package com.villagebanking

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import kotlinx.android.synthetic.main.home.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt
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
        setSettingsTable()

        btnForceLoan.setOnClickListener {
            Toast.makeText(this, "Long press to apply force loan", Toast.LENGTH_SHORT).show()
        }

        btnForceLoan.setOnLongClickListener {
            forceLoan()
            true
        }

    }


    override fun onRestart() {
        getNoMembers()
        setSettingsTable()
        super.onRestart()
    }


    override fun onBackPressed() {
        logOff()
        return
    }

    private fun getNoMembers(){
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        val noMembers = "Members: ${cursor.count}"
        tvNoAccountHolders.text = noMembers
        cursor.close()
        db.close()
    }





    private fun setSettingsTable(){
        val query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.count == 0){
            val contentValues = ContentValues()
            val settingsModel = Model()
            db = dbHandler.writableDatabase
            contentValues.put(DBHandler.SETTINGS_SHARE_VALUE_COL, settingsModel.settingsShareValue)
            contentValues.put(DBHandler.SETTINGS_INTEREST_RATE_COL, settingsModel.settingsInterestRate)
            contentValues.put(DBHandler.SETTINGS_NOTES_COL, settingsModel.settingsInterestRate)
            db.insert(DBHandler.SETTINGS_TABLE, null, contentValues)
        }else{
            getTransactions()
        }
        cursor.close()
        db.close()
    }






    private fun getTransactions(){
        var interestRate = 0.0
        var shareValue = 0.0

        var loanSubmitted = 0.0

        var shares = 0
        var shareAmount = 0.0
        var sharePayment = 0.0
        var loanApplication = 0.0
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

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()){
            loanSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
        }


        val showInterestRate = "Interest Rate: $interestRate%"
        val showShareValue = "Share Value: $shareValue"
        tvInterestRate.text = showInterestRate
        tvShareValue.text = showShareValue
        cursor.close()

        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)

        if (cursor.count == 0){
            "Shares: 0".also { tvTotalShares.text = it }
            "Investment: 0.0".also { tvTotalSharesAmount.text = it }
            "Return on Investment: 0.0".also { tvTotalInvestment.text = it }

            "Shares: 0.0".also { tvUnpaidShares.text = it }
            "Loan Payout: 0.0".also { tvUnpaidLoans.text = it }
            "Loan Repayments: 0.0".also { tvUnpaidLoanRepayments.text = it }
            "Charges 0.0".also { tvUnpaidCharges.text = it }

            "Available Balance: 0.0".also { tvAvailableBalance.text = it }
            "Virtual Balance: 0.0".also { tvVirtualBalance.text = it }
        }else {

            while (cursor.moveToNext()) {
                shares += cursor.getInt(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_COL))
                shareAmount += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_AMOUNT_COL))
                sharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
                loanApplication += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
                loanPayout += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
                loanToRepay += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL))
                loanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
                charge += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_COL))
                chargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
                currentShareOut += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_OUT_COL))
            }

            arrears = shareAmount + loanPayout + charge
            cash = sharePayment + loanRepayment + chargePayment



            val totalShares = "Shares: ${(sharePayment / shareValue).roundToInt()}"
            val totalShareAmount = "Investment: ${BigDecimal(sharePayment).setScale(2, RoundingMode.HALF_EVEN)}"
            val totalInvestment = "Return on Investment: ${BigDecimal(currentShareOut).setScale(2, RoundingMode.HALF_EVEN)}"

            val unpaidShares = "Shares: ${(BigDecimal(shareAmount - sharePayment).setScale(2, RoundingMode.HALF_EVEN))}"
            val unpaidLoans = "Loan Applications: ${BigDecimal(loanApplication - loanPayout).setScale(2, RoundingMode.HALF_EVEN)}"
            val unpaidRepayments = "Loans Repayments: ${BigDecimal(loanToRepay - loanRepayment).setScale(2, RoundingMode.HALF_EVEN)}"
            val unpaidCharges = "Charges: ${BigDecimal(charge - chargePayment).setScale(2, RoundingMode.HALF_EVEN)}"

            val availableBalance = "Available Balance: ${BigDecimal(cash - loanPayout).setScale(2, RoundingMode.HALF_EVEN)}"
            val virtualBalance = "Virtual Balance: ${BigDecimal(sharePayment + loanRepayment + chargePayment  - loanSubmitted).setScale(2, RoundingMode.HALF_EVEN)}"

            tvTotalShares.text = totalShares
            tvTotalSharesAmount.text = totalShareAmount
            tvTotalInvestment.text = totalInvestment

            tvUnpaidShares.text = unpaidShares
            tvUnpaidLoans.text = unpaidLoans
            tvUnpaidLoanRepayments.text = unpaidRepayments
            tvUnpaidCharges.text = unpaidCharges

            tvAvailableBalance.text = availableBalance
            tvVirtualBalance.text = virtualBalance
        }
        cursor.close()
    }






    private fun forceLoan(){

        var sharePayment = 0.0
        var loanPayout = 0.0
        var loanRepayment = 0.0
        var chargePayment = 0.0
        var availableCash  = 0.0
        var virtualCash = 0.0

        var loanSubmitted = 0.0

        var query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)

        if (cursor.count == 0){
            Toast.makeText(this, "No Transactions found", Toast.LENGTH_SHORT).show()
            return
        }

            while (cursor.moveToNext()) {
                sharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
                loanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
                chargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
                loanPayout += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            }


        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()){
            loanSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
        }


        availableCash  = sharePayment + loanRepayment + chargePayment - loanPayout
        virtualCash = sharePayment + loanRepayment + chargePayment  - loanSubmitted

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        val noMembers = cursor.count

        cursor.close()

        if (virtualCash != availableCash){
            AlertDialog.Builder(this)
                    .setTitle("Pending Loans")
                    .setIcon(R.drawable.ic_info)
                    .setMessage("loans amounting to ${loanSubmitted - loanPayout} have either not been approved or paid out.\n\n" +
                            "Approve and payout all loans before applying force loan")
                    .setNegativeButton("Got It") {_,_->}
                    .show()
            return
        }

        val forceLoanAmount = availableCash / noMembers

        AlertDialog.Builder(this)
                .setTitle("Force Loan")
                .setMessage("Apply Force Loan of ${BigDecimal(forceLoanAmount).setScale(2, RoundingMode.HALF_EVEN)} each?")
                .setIcon(R.drawable.ic_info)
                .setPositiveButton("Yes") {_,_->
                    query = "UPDATE ${DBHandler.ACCOUNT_HOLDERS_TABLE} SET ${DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL} = ${DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL} +  $forceLoanAmount"
                    db.execSQL(query)
                    Toast.makeText(this, "Force Loan of ${BigDecimal(forceLoanAmount).setScale(2, RoundingMode.HALF_EVEN)} each approved", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") {_,_->}
                .show()


        cursor.close()
        getTransactions()
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
                gotoAccounts()
            }
            R.id.logOff -> {
                logOff()
            }
            R.id.settings -> {
                finish()
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
        }
        return true
    }


    private fun gotoAccounts(){
        var interestRate = 0.0
        var shareValue = 0.0

        val query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()){
            interestRate = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_INTEREST_RATE_COL))
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }


        when {
            interestRate == 0.0 -> {
                Toast.makeText(this, "Set Share Value and Interest Rate", Toast.LENGTH_SHORT).show()
                return
            }
            shareValue == 0.0 -> {
                Toast.makeText(this, "Set Share Value and Interest Rate", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                val intent = Intent(this, AccountHolders::class.java)
                startActivity(intent)
            }
        }
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
















}




