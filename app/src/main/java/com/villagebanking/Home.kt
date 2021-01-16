package com.villagebanking

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.dialog_payments.view.*
import kotlinx.android.synthetic.main.home.*
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.roundToInt


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




    private fun importDB() {

    }


    private fun export() {
        //generate data

        var recordList = ArrayList<Model>()
        recordList.clear()
        recordList = dbHandler.getAccountHolders(this)

            val accountHolders = StringBuffer()
            for (i in recordList.indices) {
                accountHolders.append(""+ recordList[i].accountHoldersID)
                accountHolders.append(""+ recordList[i].accountHoldersName)
                accountHolders.append(""+ recordList[i].accountHoldersAdmin)
                accountHolders.append(""+ recordList[i].accountHolderContact)
                accountHolders.append(""+ recordList[i].accountHolderBankInfo)
                accountHolders.append(""+ recordList[i].accountHolderPin)
                accountHolders.append(""+ recordList[i].accountHolderPinHint)
                accountHolders.append(""+ recordList[i].accountHoldersShare)
                accountHolders.append(""+ recordList[i].accountHoldersLoanApp)
                accountHolders.append(""+ recordList[i].accountHoldersCharges)
                accountHolders.append(""+ recordList[i].accountHoldersApproved)
                accountHolders.append(""+ recordList[i].accountHoldersAsset)
                accountHolders.append(""+ recordList[i].accountHoldersLiability)
                accountHolders.append("\n")
            }


        try {
            //saving the file into device
            val out: FileOutputStream = openFileOutput("Account Holders.csv", MODE_PRIVATE)
            out.write(accountHolders.toString().toByteArray())
            out.close()

            //exporting
            val context = applicationContext
            val fileLocation = File(filesDir, "Account Holders.csv")
            val path: Uri = FileProvider.getUriForFile(context, "com.villagebanking.fileprovider", fileLocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "text/csv"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(fileIntent, "Send mail"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }






    private fun exportDB() {

/*        var recordList = ArrayList<Model>()
        recordList.clear()
        recordList = dbHandler.getAccountHolders(this)

        try {

            val buffer = StringBuffer()

            for (i in recordList.indices) {
                buffer.append(""+ recordList[i].accountHoldersID)
                buffer.append(""+ recordList[i].accountHoldersName)
                buffer.append(""+ recordList[i].accountHoldersAdmin)
                buffer.append(""+ recordList[i].accountHolderContact)
                buffer.append(""+ recordList[i].accountHolderBankInfo)
                buffer.append(""+ recordList[i].accountHolderPin)
                buffer.append(""+ recordList[i].accountHolderPinHint)
                buffer.append(""+ recordList[i].accountHoldersShare)
                buffer.append(""+ recordList[i].accountHoldersLoanApp)
                buffer.append(""+ recordList[i].accountHoldersCharges)
                buffer.append(""+ recordList[i].accountHoldersApproved)
                buffer.append(""+ recordList[i].accountHoldersAsset)
                buffer.append(""+ recordList[i].accountHoldersLiability)
                buffer.append("\n")
            }

            AlertDialog.Builder(this)
                    .setMessage(buffer.toString())
                    .show()


            Toast.makeText(this, "Backup exported", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }*/

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


            var previousLoanPayouts = 0.0

            query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} != ?"
            db = dbHandler.readableDatabase
            cursor = db.rawQuery(query, arrayOf(transactionMonth))
            while (cursor.moveToNext()) {
                previousLoanPayouts += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
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
            val virtualBalance = "Virtual Balance: ${BigDecimal(cash - loanSubmitted - previousLoanPayouts).setScale(2, RoundingMode.HALF_EVEN)}"

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






    @SuppressLint("SimpleDateFormat")
    private fun forceLoan(){

        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))

        var sharePayment = 0.0
        var loanApplication = 0.0
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
                loanApplication += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_APP_COL))
            }


        var previousLoanPayouts = 0.0

        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} != ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(transactionMonth))
        while (cursor.moveToNext()) {
            previousLoanPayouts += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
        }


        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()){
            loanSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
        }


        availableCash  = sharePayment + loanRepayment + chargePayment - loanPayout
        virtualCash = sharePayment + loanRepayment + chargePayment  - loanSubmitted - previousLoanPayouts

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        val noMembers = cursor.count

        cursor.close()

        if (virtualCash != availableCash){
            AlertDialog.Builder(this)
                    .setTitle("Pending Loans")
                    .setIcon(R.drawable.ic_info)
                    .setMessage("A sum of $loanSubmitted loan(s) are pending approval and/or payout.\n\n" +
                            "Approve and payout all loans before applying force loan.")
                    .setNegativeButton("Got It") { _, _->}
                    .show()
            return
        }

        val forceLoanAmount = availableCash / noMembers

        AlertDialog.Builder(this)
                .setTitle("Force Loan")
                .setMessage("Apply Force Loan of ${BigDecimal(forceLoanAmount).setScale(2, RoundingMode.HALF_EVEN)} each?")
                .setIcon(R.drawable.ic_info)
                .setPositiveButton("Yes") { _, _->
                    query = "UPDATE ${DBHandler.ACCOUNT_HOLDERS_TABLE} SET ${DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL} = ${DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL} +  $forceLoanAmount"
                    db.execSQL(query)
                    Toast.makeText(this, "Force Loan of ${BigDecimal(forceLoanAmount).setScale(2, RoundingMode.HALF_EVEN)} each approved", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { _, _->}
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
            R.id.importDB -> {
                importDB()
            }
            R.id.exportDB -> {
                //exportDB()
                export()
            }
            R.id.reset -> {
                factoryReset()
            }
        }
        return true
    }



    private fun factoryReset(){
        var pin = ""
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_ADMIN_COL} = ?"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, arrayOf("Chairperson"))


        if (cursor.count == 0){
            Toast.makeText(this@Home, "Create Chairperson Account to Reset", Toast.LENGTH_LONG).show()
            return
        }

        if (cursor.moveToFirst()){
            pin = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_PIN_COL))
        }

        val confirmDialog = LayoutInflater.from(this).inflate(R.layout.dialog_payments, null)

        AlertDialog.Builder(this)
                .setTitle("Factory Reset System")
                .setMessage("Enter Chairperson PIN to Reset")
                .setView(confirmDialog)
                .setNegativeButton("Cancel") { _, _->}
                .setPositiveButton("Confirm", null)
                .create().apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            Toast.makeText(this@Home, "Long press to reset", Toast.LENGTH_LONG).show()
                        }

                        getButton(AlertDialog.BUTTON_POSITIVE).setOnLongClickListener {
                            if (confirmDialog.etPayments.text.toString() == pin){
                                dbHandler.deleteAllAccounts(this@Home)
                                dbHandler.deleteAllTransactions(this@Home)
                                dbHandler.deleteAllSettings(this@Home)
                                dbHandler.deleteAllStatements(this@Home)
                                Toast.makeText(this@Home, "System Reset Successful", Toast.LENGTH_LONG).show()
                                dismiss()
                                finish()
                            }else{
                                Toast.makeText(this@Home, "Invalid Pin $pin", Toast.LENGTH_LONG).show()
                            }
                            true
                        }
                    }
                }
                .show()

        cursor.close()
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




