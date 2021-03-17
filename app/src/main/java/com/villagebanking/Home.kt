package com.villagebanking

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.opencsv.CSVReaderHeaderAware
import kotlinx.android.synthetic.main.dialog_payments.view.*
import kotlinx.android.synthetic.main.home.*
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList


class Home: AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }

    private var STORAGE_PERMISSION_CODE = 1

    var myDownloadId : Long = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val actionbar = supportActionBar
        actionbar!!.title = "Home"

       dbHandler = DBHandler(this, null, null, 1)

        getNoMembers()
        setSettingsTable()
        getTransactions()

        btnForceLoan.setOnClickListener {
            Toast.makeText(this, "Long press to apply force loan", Toast.LENGTH_SHORT).show()
        }

        btnForceLoan.setOnLongClickListener {
            forceLoan()
            true
        }

        homeProgressBar.isVisible = false
        test()

        test2()



        btTest3.setOnClickListener {


        startOpenGoogleDriveApp()


        }





    }


    fun startOpenGoogleDriveApp() {
        val intent = Intent("com.google.android.apps.docs.DRIVE_OPEN")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }




    private fun test2() {

        btTest2.setOnClickListener {


            val dl = "/sdcard/Download"
            val fl = "/data/data/com.villagebanking/files"

            if(!File(dl, "/Database").exists()){
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com"))
                startActivity(browserIntent)
            }else{

                File(dl, "/Database").let { sourceFile ->
                    sourceFile.copyTo(File(fl, "/Database.csv"))
                    sourceFile.delete()

                    restore()

                }
            }


        }
    }







    @SuppressLint("SimpleDateFormat", "SdCardPath")
    @RequiresApi(Build.VERSION_CODES.N)
    fun test(){

        btTest.setOnClickListener {








        }


    }







    private fun importBackupPermission(){
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();

            askRestore()

        } else {
            requestStoragePermission();
        }
    }






    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setPositiveButton("ok") { dialog, which -> ActivityCompat.requestPermissions(this@Home, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE) }
                    .setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
                    .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }


    @SuppressLint("SdCardPath")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()

                    askRestore()

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }







    private fun restore(){



        homeProgressBar.isVisible = true
        val mCountDownTimer: CountDownTimer
        var i = 0
        val mProgressBar: ProgressBar = homeProgressBar
        mProgressBar.progress = i
        mCountDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.v("Log_tag", "Tick of Progress$i$millisUntilFinished")
                i++
                mProgressBar.progress = i as Int * 100 / (5000 / 1000)
            }
            override fun onFinish() {

                importDB()

                homeProgressBar.isVisible = false
                i++
                mProgressBar.progress = 100
                finish()

                Toast.makeText(this@Home, "Restore Successful", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@Home, "chairperson PIN reset", Toast.LENGTH_SHORT).show()
                File("/data/data/com.villagebanking/files", "/Database.csv").delete()

                File("/sdcard/Download", "/Database.csv").let { sourceFile ->
                    sourceFile.deleteOnExit()}

            }
        }
        mCountDownTimer.start()
    }






    private fun askRestore(){

        val dl = "/sdcard/Download"
        val fl = "/data/data/com.villagebanking/files"

        if(!File(dl, "/Database.csv").exists()){
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com"))
            startActivity(browserIntent)
            return
        }

                AlertDialog.Builder(this)
                    .setTitle("System Restore?")
                    .setNegativeButton("No") { _, _->}
                    .setPositiveButton("Yes") { _, _->

                        File("/data/data/com.villagebanking/files", "/Database.csv").delete()

                        File(dl, "/Database.csv").let { sourceFile ->
                            sourceFile.copyTo(File(fl, "/Database.csv"))
                        sourceFile.delete()}

                        restore()

                    }
                    .show()
    }








    @SuppressLint("SdCardPath")
    private fun export() {
        //generate data

        var model = ArrayList<Model>()
        model.clear()
        model = dbHandler.getAccountHolders(this)

        val backup = StringBuffer()
        backup.append("\n")

             for (i in model.indices) {
                backup.append(
                        "${model[i].accountHoldersID}," +
                                "Account Holders," +
                                "${model[i].accountHoldersName}," +
                                "${model[i].accountHoldersAdmin}," +
                                "${model[i].accountHolderContact}," +
                                "${model[i].accountHolderBankInfo}," +
                                "${model[i].accountHolderPin}," +
                                "${model[i].accountHolderPinHint}," +
                                "${model[i].accountHoldersShare}," +
                                "${model[i].accountHoldersLoanApp}," +
                                "${model[i].accountHoldersCharges}," +
                                "${model[i].accountHoldersArrears}," +
                                "${model[i].accountHoldersAsset}," +
                                "${model[i].accountHoldersLiability}")
                backup.append("\n")
            }

        model = dbHandler.getTransactions(this)

        for (i in model.indices) {
            backup.append(
                    "${model[i].transactionID}," +
                            "Transactions," +
                            "${model[i].transactionName}," +
                            "${model[i].transactionMonth}," +
                            "${model[i].transactionShares}," +
                            "${model[i].transactionShareAmount}," +
                            "${model[i].transactionSharePayment}," +
                            "${model[i].transactionShareDate}," +
                            "${model[i].transactionLoanApp}," +
                            "${model[i].transactionLoanPayment}," +
                            "${model[i].transactionLoanPaymentDate}," +
                            "${model[i].transactionLoanToRepay}," +
                            "${model[i].transactionLoanRepayment}," +
                            "${model[i].transactionLoanRepaymentDate}," +
                            "${model[i].transactionChargeName}," +
                            "${model[i].transactionCharge}," +
                            "${model[i].transactionChargePayment}," +
                            "${model[i].transactionChargePaymentDate}," +
                            "${model[i].transactionInterest}," +
                            "${model[i].transactionShareOut}," +
                            "${model[i].transactionArrears},")
            backup.append("\n")
        }

        model = dbHandler.getSettings(this)

        for (i in model.indices) {
            backup.append(
                    "1," +
                            "Settings," +
                            "${model[i].settingsShareValue}," +
                            "${model[i].settingsInterestRate}," +
                            "${model[i].settingsNotes},")
            backup.append("\n")
        }

        model = dbHandler.getStatements(this)

        for (i in model.indices) {
            backup.append(
                    "${model[i].statementsID}," +
                            "Statements," +
                            "${model[i].statementsMonth}," +
                            "${model[i].statementsDate}," +
                            "${model[i].statementsTime}," +
                            "${model[i].statementsName}," +
                            "${model[i].statementsAction}," +
                            "${model[i].statementsShare}," +
                            "${model[i].statementsShareAmount}," +
                            "${model[i].statementsLoan}," +
                            "${model[i].statementChargeName}," +
                            "${model[i].statementsCharge},")
            backup.append("\n")
        }



        try {
            //saving the file into device
            val accountHoldersOut: FileOutputStream = openFileOutput("Database.csv", MODE_PRIVATE)
            accountHoldersOut.write(backup.toString().toByteArray())
            accountHoldersOut.close()

            //exporting
/*            val context = applicationContext
            val fileLocation = File(filesDir, "Database.csv")
            val path: Uri = FileProvider.getUriForFile(context, "com.villagebanking.fileprovider", fileLocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "text/csv"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Database.csv")
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(fileIntent, "Google Drive Backup"))*/

            val context = applicationContext
            val fileLocation = File(filesDir, "Database.csv")
            val path: Uri = FileProvider.getUriForFile(context, "com.villagebanking.fileprovider", fileLocation)

            val shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setText("Share PDF doc")
                    .setType("text/csv")
                    .setStream(path)
                    .intent
                    .setPackage("com.google.android.apps.docs")
            startActivity(shareIntent)





        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    @SuppressLint("SdCardPath")
    private fun importDB() {

        val contentValues = ContentValues()
        val db = dbHandler.writableDatabase

        val reader = CSVReaderHeaderAware(FileReader("/data/data/com.villagebanking/files/Database.csv"))

        db.delete(DBHandler.ACCOUNT_HOLDERS_TABLE, null, null)
        db.delete(DBHandler.TRANSACTION_TABLE, null, null)
        db.delete(DBHandler.SETTINGS_TABLE, null, null)
        db.delete(DBHandler.STATEMENT_TABLE, null, null)

        for (i in reader){

            val table = i[1]

            if (table == "Account Holders") {
                val accountHoldersName = i[2]
                val accountHoldersAdmin = i[3]
                val accountHoldersContact = i[4]
                val accountHoldersBankInfo = i[5]
                val accountHoldersPin = i[6]
                val accountHoldersPinHint = i[7]
                val accountHoldersShares = i[8]
                val accountHoldersLoanApp = i[9]
                val accountHoldersCharges = i[10]
                val accountHoldersArrears = i[11]
                val accountHoldersAsset = i[12]
                val accountHoldersLiabilities = i[13]

                contentValues.put(DBHandler.ACCOUNT_HOLDERS_NAME_COL, accountHoldersName)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ADMIN_COL, accountHoldersAdmin)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_CONTACT_COL, accountHoldersContact)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_BANK_INFO_COL, accountHoldersBankInfo)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_PIN_COL, accountHoldersPin)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_PIN_HINT_COL, accountHoldersPinHint)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_SHARE_COL, accountHoldersShares)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL, accountHoldersLoanApp)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL, accountHoldersCharges)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, accountHoldersArrears)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ASSET_COL, accountHoldersAsset)
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_LIABILITY_COL, accountHoldersLiabilities)

                db.insert(DBHandler.ACCOUNT_HOLDERS_TABLE, null, contentValues)
            }


            if (table == "Transactions") {
                val transactionName = i[2]
                val transactionMonth = i[3]
                val transactionShare = i[4]
                val transactionShareAmount = i[5]
                val transactionSharePayment = i[6]
                val transactionShareDate = i[7]
                val transactionLoanApp = i[8]
                val transactionLoanPayment = i[9]
                val transactionLoanPaymentDate = i[10]
                val transactionLoanToRepay = i[11]
                val transactionLoanRepayment = i[12]
                val transactionLoanRepaymentDate = i[13]
                val transactionChargeName = i[14]
                val transactionCharge = i[15]
                val transactionChargePayment = i[16]
                val transactionChargePaymentDate = i[17]
                val transactionInterest = i[18]
                val transactionShareOut = i[19]
                val transactionArrears = i[20]

                val contentValues2 = ContentValues()
                contentValues2.put(DBHandler.TRANSACTION_NAME_COL, transactionName)
                contentValues2.put(DBHandler.TRANSACTION_MONTH_COL, transactionMonth)
                contentValues2.put(DBHandler.TRANSACTION_SHARE_COL, transactionShare)
                contentValues2.put(DBHandler.TRANSACTION_SHARE_AMOUNT_COL, transactionShareAmount)
                contentValues2.put(DBHandler.TRANSACTION_SHARE_PAYMENT_COL, transactionSharePayment)
                contentValues2.put(DBHandler.TRANSACTION_SHARE_DATE_COL, transactionShareDate)
                contentValues2.put(DBHandler.TRANSACTION_LOAN_APP_COL, transactionLoanApp)
                contentValues2.put(DBHandler.TRANSACTION_LOAN_PAYMENT_COL, transactionLoanPayment)
                contentValues2.put(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL, transactionLoanPaymentDate)
                contentValues2.put(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL, transactionLoanToRepay)
                contentValues2.put(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL, transactionLoanRepayment)
                contentValues2.put(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL, transactionLoanRepaymentDate)
                contentValues2.put(DBHandler.TRANSACTION_CHARGE_NAME_COL, transactionChargeName)
                contentValues2.put(DBHandler.TRANSACTION_CHARGE_COL, transactionCharge)
                contentValues2.put(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL, transactionChargePayment)
                contentValues2.put(DBHandler.TRANSACTION_CHARGE_DATE_COL, transactionChargePaymentDate)
                contentValues2.put(DBHandler.TRANSACTION_INTEREST_COL, transactionInterest)
                contentValues2.put(DBHandler.TRANSACTION_SHARE_OUT_COL, transactionShareOut)
                contentValues2.put(DBHandler.TRANSACTION_ARREARS_COL, transactionArrears)

                db.insert(DBHandler.TRANSACTION_TABLE, null, contentValues2)
            }

            if (table == "Settings") {
                val settingsShareValue = i[2]
                val settingsInterestRate = i[3]
                val settingsNotes = i[4]

                val contentValues3 = ContentValues()
                contentValues3.put(DBHandler.SETTINGS_ID_COL, 1)
                contentValues3.put(DBHandler.SETTINGS_SHARE_VALUE_COL, settingsShareValue)
                contentValues3.put(DBHandler.SETTINGS_INTEREST_RATE_COL, settingsInterestRate)
                contentValues3.put(DBHandler.SETTINGS_NOTES_COL, settingsNotes)

                db.insert(DBHandler.SETTINGS_TABLE, null, contentValues3)
            }


            if (table == "Statements") {
                val statementsMonth = i[2]
                val statementsDate = i[3]
                val statementsTime = i[4]
                val statementsName = i[5]
                val statementsAction = i[6]
                val statementsShare = i[7]
                val statementsShareAmount = i[8]
                val statementsLoan = i[9]
                val statementChargeName = i[10]
                val statementsCharge = i[11]

                val contentValues4 = ContentValues()
                contentValues4.put(DBHandler.STATEMENT_MONTH, statementsMonth)
                contentValues4.put(DBHandler.STATEMENT_DATE, statementsDate)
                contentValues4.put(DBHandler.STATEMENT_TIME, statementsTime)
                contentValues4.put(DBHandler.STATEMENT_NAME, statementsName)
                contentValues4.put(DBHandler.STATEMENT_ACTION, statementsAction)
                contentValues4.put(DBHandler.STATEMENT_SHARE, statementsShare)
                contentValues4.put(DBHandler.STATEMENT_SHARE_AMOUNT, statementsShareAmount)
                contentValues4.put(DBHandler.STATEMENT_LOAN, statementsLoan)
                contentValues4.put(DBHandler.STATEMENT_CHARGE_NAME, statementChargeName)
                contentValues4.put(DBHandler.STATEMENT_CHARGE, statementsCharge)

                db.insert(DBHandler.STATEMENT_TABLE, null, contentValues4)
            }
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



            val totalShares = "Shares: ${(sharePayment / shareValue)}"
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
                importBackupPermission()
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




