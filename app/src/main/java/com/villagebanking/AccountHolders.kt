package com.villagebanking

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_details.*
import kotlinx.android.synthetic.main.account_holders.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.delete_confirmation.view.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.view.*
import kotlinx.android.synthetic.main.dialog_insert_password.view.*
import kotlinx.android.synthetic.main.dialog_password.view.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.main_row_layout.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class AccountHolders: AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }

    //Format date to day Month Year
    //Get date to insert
    val date = Calendar.getInstance().time
    @RequiresApi(Build.VERSION_CODES.N)
    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance()
    @RequiresApi(Build.VERSION_CODES.N)
    val transactionDate: String = dateFormat.format(date)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_holders)

        dbHandler = DBHandler(this, null, null, 1)

        viewAccountHolders()
        securityWarning()

        val actionBar = supportActionBar
        actionBar!!.title = "Accounts"

        checkIfCurrentMonth()
        availableCash()
    }


    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_holders_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addAccountHolder -> {
                addAccountHolder(this)
            }
            R.id.searchName ->{

            }
            R.id.approveAll ->{
                approveAll()
            }
            R.id.ViewAll -> {
                viewAccountHolders()
            }
            R.id.ViewAdmins -> {
                viewAccountAdmins()
            }
            R.id.arrears ->{
                viewArrears()
            }
            R.id.delAllAccountHolders -> {
                deleteAllAccounts()
                viewAccountHolders()
            }





        }
        return true
    }



    override fun onResume() {
        super.onResume()
        availableCash()
        viewAccountHolders()
    }



    @SuppressLint("SimpleDateFormat")
    private fun checkIfCurrentMonth(){
        var query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)
        if (cursor.count != 0){


            val c: Calendar = GregorianCalendar()
            c.time = Date()
            val sdf = java.text.SimpleDateFormat("MMMM yyyy")
            //println(sdf.format(c.time)) // NOW
            val transactionMonth = (sdf.format(c.time))
            c.add(Calendar.MONTH, -1)
            //println(sdf.format(c.time)) // One month ago
            val transactionLastMonth = (sdf.format(c.time))


            query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_SHARE_COL} != ? OR " +
                    "${DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL} != ? OR ${DBHandler.ACCOUNT_HOLDERS_CHARGES_COL} != ?"
            db = dbHandler.readableDatabase
            cursor = db.rawQuery(query, arrayOf("0.0","0.0","0.0"))
            if (cursor.count != 0){

                query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = ?"
                db = Home.dbHandler.readableDatabase
                cursor = db.rawQuery(query, arrayOf(transactionMonth))
                if (cursor.count == 0){

                    //Create Tables when new month is detected
                    autoCreateMonth()

                val contentValues = ContentValues()
                db = dbHandler.writableDatabase
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
                db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, null, null)

                    AlertDialog.Builder(this)
                            .setTitle(transactionMonth)
                            .setMessage("You have entered a new month. Reset shares, loan applications and charges to zero?")
                            .setNegativeButton("No") {_,_->}
                            .setPositiveButton("Yes") {_,_->
                                resetPostsAndApps()
                                viewAccountHolders()
                            }
                            .show()
                }
            }
            cursor.close()
            db.close()
        }
    }



    private fun resetPostsAndApps(){
        val contentValues = ContentValues()
        val db = Home.dbHandler.writableDatabase
        contentValues.put(DBHandler.ACCOUNT_HOLDERS_SHARE_COL, 0.0)
        contentValues.put(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL, 0.0)
        contentValues.put(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL, 0.0)
        db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, null, null)
        Toast.makeText(this,"All postings reset to Zero", Toast.LENGTH_SHORT).show()
    }



    private fun viewAccountHolders(){
        val accountHoldersList = dbHandler.getAccountHolders(this)
        val adapter = CustomAdapter(this, accountHoldersList)
        val rv: RecyclerView = recyclerView1
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager
        rv.adapter = adapter
        availableCash()
    }



    private fun viewAccountAdmins(){
        val accountAdminList = dbHandler.getAccountAdmins(this)
        val adapter = CustomAdapter(this, accountAdminList)
        val rv: RecyclerView = recyclerView1
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager
        rv.adapter = adapter
    }



    private fun viewArrears(){
        val arrearsList = dbHandler.getArrears(this)
        val adapter = CustomAdapter(this, arrearsList)
        val rv: RecyclerView = recyclerView1
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager
        rv.adapter = adapter

    }



    private fun addAccountHolder(yContext: Context){
        //Create Spinner Array
        val admin = arrayOf(
                "SELECT ROLE...",
                "Chairperson",
                "Vice Chairperson",
                "Secretary",
                "Money Counter 1",
                "Money Counter 2",
                "Account Holder"
        )

        //Need to declare variables for the layout before adding array to it
        //Otherwise the layout will not know what variables to place in the spinner
        val addAccountHolderDialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_add_account_holder, null)

        //Add array to Spinner List in layout
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_selectable_list_item, admin)
        addAccountHolderDialogLayout.spAdministrators.adapter = arrayAdapter

        var selectedAdmin = ""

        addAccountHolderDialogLayout.spAdministrators.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAdmin = admin[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        AlertDialog.Builder(this)

                .setView(addAccountHolderDialogLayout)
                .setTitle("Add New Member")

                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel") { _, _ ->}
                .create().apply {
                    setOnShowListener{
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                            if (addAccountHolderDialogLayout.etFullNames.text.isEmpty()) {
                                Toast.makeText(yContext, "Please type Full Name", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener}

                            if (addAccountHolderDialogLayout.etContactNo.text.isEmpty()) {
                                Toast.makeText(yContext, "Please enter contact number", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener}

                            if (addAccountHolderDialogLayout.etAccountInfo.text.isEmpty()) {
                                Toast.makeText(yContext, "Please enter account or mobile banking info", Toast.LENGTH_LONG).show()
                                return@setOnClickListener}

                            if (selectedAdmin == "SELECT ROLE...") {
                                Toast.makeText(yContext, "Please select admin role", Toast.LENGTH_LONG).show()
                                return@setOnClickListener}

                            if (selectedAdmin == "Chairperson" ){
                                Toast.makeText(yContext, "Create Chairperson PIN", Toast.LENGTH_SHORT).show()
                                val insertPinDialogLayout = LayoutInflater.from(yContext).inflate(R.layout.dialog_insert_password, null)
                                AlertDialog.Builder(yContext)
                                        .setTitle("Create PIN")
                                        .setMessage("Chairperson access PIN required")
                                        .setView(insertPinDialogLayout)
                                        .setPositiveButton("Submit", null)
                                        .setNegativeButton("Cancel") { _, _->}
                                        .create().apply {
                                            setOnShowListener {
                                                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                                    if(insertPinDialogLayout.etInsertPIN.text.isEmpty()){
                                                        Toast.makeText(yContext, "Please type PIN", Toast.LENGTH_SHORT).show()
                                                        return@setOnClickListener}

                                                    if(insertPinDialogLayout.etPinRepeat.text.isEmpty()){
                                                        Toast.makeText(yContext, "Please type repeat PIN", Toast.LENGTH_SHORT).show()
                                                        return@setOnClickListener}

                                                    if(insertPinDialogLayout.etPinHint.text.isEmpty()){
                                                        Toast.makeText(yContext, "Please type hint", Toast.LENGTH_SHORT).show()
                                                        return@setOnClickListener}

                                                    if(insertPinDialogLayout.etInsertPIN.text.toString() != insertPinDialogLayout.etPinRepeat.text.toString()){
                                                        Toast.makeText(yContext, "PIN and repeat PIN do not match. Re-type", Toast.LENGTH_LONG).show()
                                                        return@setOnClickListener}

                                                    if(insertPinDialogLayout.etInsertPIN.text.length < 4){
                                                        Toast.makeText(yContext, "PIN should be 4 digits", Toast.LENGTH_LONG).show()
                                                        return@setOnClickListener}

                                                    val accountHolderModel = Model()
                                                    accountHolderModel.accountHoldersName = addAccountHolderDialogLayout.etFullNames.text.toString()
                                                    accountHolderModel.accountHoldersAdmin = selectedAdmin
                                                    accountHolderModel.accountHolderContact = addAccountHolderDialogLayout.etContactNo.text.toString()
                                                    accountHolderModel.accountHolderBankInfo = addAccountHolderDialogLayout.etAccountInfo.text.toString()
                                                    accountHolderModel.accountHolderPin = insertPinDialogLayout.etInsertPIN.text.toString()
                                                    accountHolderModel.accountHolderPinHint = insertPinDialogLayout.etPinHint.text.toString()
                                                    accountHolderModel.accountHoldersApproved = ""
                                                    dbHandler.addAccountHolder(yContext, accountHolderModel)
                                                    viewAccountHolders()
                                                    addAccountHolderDialogLayout.etFullNames.text.clear()
                                                    addAccountHolderDialogLayout.etContactNo.text.clear()
                                                    addAccountHolderDialogLayout.etAccountInfo.text.clear()
                                                    addAccountHolderDialogLayout.etFullNames.requestFocus()
                                                    dismiss()
                                                }
                                            }
                                        }

                                .show()
                                insertPinDialogLayout.etInsertPIN.requestFocus()
                                dismiss()
                            }else{
                                val accountHolderModel = Model()
                                accountHolderModel.accountHoldersName = addAccountHolderDialogLayout.etFullNames.text.toString()
                                accountHolderModel.accountHoldersAdmin = selectedAdmin
                                accountHolderModel.accountHolderContact = addAccountHolderDialogLayout.etContactNo.text.toString()
                                accountHolderModel.accountHolderBankInfo = addAccountHolderDialogLayout.etAccountInfo.text.toString()
                                dbHandler.addAccountHolder(yContext, accountHolderModel)
                                viewAccountHolders()
                                addAccountHolderDialogLayout.etFullNames.text.clear()
                                addAccountHolderDialogLayout.etContactNo.text.clear()
                                addAccountHolderDialogLayout.etAccountInfo.text.clear()
                                addAccountHolderDialogLayout.etFullNames.requestFocus()
                                dismiss()
                            }
                        }
                    }
                }
                .show()
        addAccountHolderDialogLayout.etFullNames.requestFocus()
    }



    private fun deleteAllAccounts(){
        val deleteDialogLayout = LayoutInflater.from(this).inflate(R.layout.delete_confirmation, null)
            AlertDialog.Builder(this)
                .setTitle("Warning!!!")
                .setMessage("Are you sure you want to delete all accounts? \n \n " +
                        "All transactions will be lost and cannot be recovered.")
                .setIcon(R.drawable.ic_warning)
                .setNegativeButton("No") {_,_->}
                .setPositiveButton("Yes") {_,_->

                    val delConfirm = AlertDialog.Builder(this)
                            .setTitle("Long press to delete")
                            .setIcon(R.drawable.ic_delete)
                            .setView(deleteDialogLayout)

                    val showDelConfirm = delConfirm.show()

                    deleteDialogLayout.btnConfirmDelete.setOnLongClickListener {
                        dbHandler.deleteAllTransactions(this)
                        dbHandler.deleteAllAccounts(this)
                        viewAccountHolders()
                        showDelConfirm.dismiss()
                        true
                    }

                    deleteDialogLayout.btnConfirmDelete.setOnClickListener {
                        Toast.makeText(this, "Long press to delete", Toast.LENGTH_SHORT).show()
                    }

                    deleteDialogLayout.btnCancelDelete.setOnClickListener {
                        showDelConfirm.dismiss()
                    }



                }
                    .show()


    }




    @SuppressLint("SimpleDateFormat")
    fun autoCreateMonth(){

        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))

        var interest = 0.0
        var name = ""

        //Get interest rate and share value
        var query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            interest = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_INTEREST_RATE_COL))
        }

        interest = (interest/100)+1

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_NAME_COL))

            val contentValues = ContentValues()
            contentValues.put(DBHandler.TRANSACTION_NAME_COL, name)
            contentValues.put(DBHandler.TRANSACTION_MONTH_COL, transactionMonth)
            contentValues.put(DBHandler.TRANSACTION_SHARE_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_SHARE_AMOUNT_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_SHARE_PAYMENT_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_SHARE_DATE_COL, transactionDate)
            contentValues.put(DBHandler.TRANSACTION_LOAN_APP_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_LOAN_PAYMENT_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL, transactionDate)
            contentValues.put(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL, transactionDate)
            contentValues.put(DBHandler.TRANSACTION_CHARGE_NAME_COL, "")
            contentValues.put(DBHandler.TRANSACTION_CHARGE_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_CHARGE_DATE_COL, transactionDate)
            contentValues.put(DBHandler.TRANSACTION_INTEREST_COL, interest)
            contentValues.put(DBHandler.TRANSACTION_SHARE_OUT_COL, 0.0)
            contentValues.put(DBHandler.TRANSACTION_ARREARS_COL, "")

            db = dbHandler.writableDatabase
            db.insert(DBHandler.TRANSACTION_TABLE, null, contentValues)

            //Update arrears
            val contentValues2 = ContentValues()
            contentValues2.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
            contentValues2.put(DBHandler.ACCOUNT_HOLDERS_LIABILITY_COL, 0.0)
            contentValues2.put(DBHandler.ACCOUNT_HOLDERS_ASSET_COL, 0.0)
            db = dbHandler.writableDatabase
            db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues2, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?", arrayOf(name))


            query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_SHARE_OUT_COL} = ${DBHandler.TRANSACTION_SHARE_PAYMENT_COL} * ${DBHandler.TRANSACTION_INTEREST_COL} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = '$transactionLastMonth' AND ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
            db.execSQL(query)


            Toast.makeText(this, interest.toString(), Toast.LENGTH_LONG).show()


            //Populate incremental interests for all past months
/*            query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_SHARE_OUT_COL} = ${DBHandler.TRANSACTION_SHARE_OUT_COL} * " +
                    "${DBHandler.TRANSACTION_INTEREST_COL} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
            db.execSQL(query)*/



        }







        var currentShareOut = 0.0
        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} = ?"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(transactionLastMonth))
        while (cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(DBHandler.TRANSACTION_NAME_COL))
            currentShareOut = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_OUT_COL))


            val contentValues3 = ContentValues()
            contentValues3.put(DBHandler.ACCOUNT_HOLDERS_ASSET_COL, currentShareOut)
            db = dbHandler.writableDatabase
            db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues3, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?", arrayOf(name))

        }



        viewAccountHolders()

    }







    @SuppressLint("SimpleDateFormat")
    private fun populateInvestment(){
        var lastMonthSharePayment = 0.0
        var name = ""


        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))


        var query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_NAME_COL))

            //Get previous month sharePayment to be populated as current share out
            query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_NAME_COL} = ? AND ${DBHandler.TRANSACTION_MONTH_COL} = ?"
            db = dbHandler.readableDatabase
            cursor = db.rawQuery(query, arrayOf(name, transactionLastMonth))
            if (cursor.count != 0) {
                if (cursor.moveToFirst()) {
                    lastMonthSharePayment = cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
                }
            } else {
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
                    "${DBHandler.TRANSACTION_NAME_COL} = ?", arrayOf(transactionLastMonth, name))


            //Populate incremental interests for all past months
            query = "UPDATE ${DBHandler.TRANSACTION_TABLE} SET ${DBHandler.TRANSACTION_SHARE_OUT_COL} = ${DBHandler.TRANSACTION_SHARE_OUT_COL} * " +
                    "${DBHandler.TRANSACTION_INTEREST_COL} WHERE ${DBHandler.TRANSACTION_NAME_COL} = '$name'"
            db = AccountDetails.dbHandler.writableDatabase
            db.execSQL(query)
            db.close()
        }
    }










    @SuppressLint("SimpleDateFormat")
    private fun approveAll(){
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))

        val contentValues = ContentValues()
        val contentValues2 = ContentValues()


        var interest = 0.0
        var shareValue = 0.0

        var name = ""
        var shares = 0.0
        var loanApplication = 0.0
        var charge = 0.0

        //Get interest rate and share value
        var query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            interest = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_INTEREST_RATE_COL))
            shareValue = cursor.getDouble(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL))
        }

        interest = (interest/100)+1

        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_NAME_COL))
            shares = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            loanApplication = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            charge = cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))


                    contentValues.put(DBHandler.TRANSACTION_NAME_COL, name)
                    contentValues.put(DBHandler.TRANSACTION_MONTH_COL, transactionMonth)
                    contentValues.put(DBHandler.TRANSACTION_SHARE_COL, shares)
                    contentValues.put(DBHandler.TRANSACTION_SHARE_AMOUNT_COL, shares * shareValue)
                    contentValues.put(DBHandler.TRANSACTION_SHARE_PAYMENT_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_SHARE_DATE_COL, transactionDate)
                    contentValues.put(DBHandler.TRANSACTION_LOAN_APP_COL, loanApplication)
                    contentValues.put(DBHandler.TRANSACTION_LOAN_PAYMENT_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_LOAN_PAYMENT_DATE_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_LOAN_TO_REPAY_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_LOAN_REPAYMENT_DATE_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_CHARGE_NAME_COL, "")
                    contentValues.put(DBHandler.TRANSACTION_CHARGE_COL, charge)
                    contentValues.put(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_CHARGE_DATE_COL, transactionDate)
                    contentValues.put(DBHandler.TRANSACTION_INTEREST_COL, interest)
                    contentValues.put(DBHandler.TRANSACTION_SHARE_OUT_COL, 0.0)
                    contentValues.put(DBHandler.TRANSACTION_ARREARS_COL, "")
                    db.insert(DBHandler.TRANSACTION_TABLE, null, contentValues)

                    //Update arrears
                    contentValues2.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
                    contentValues2.put(DBHandler.ACCOUNT_HOLDERS_LIABILITY_COL, (-shares * shareValue) - charge)
                    contentValues2.put(DBHandler.ACCOUNT_HOLDERS_ASSET_COL, 0.0)
                    db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues2, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?", arrayOf(name))


        }

            viewAccountHolders()


    }







    private fun securityWarning(){
    //No Chairperson Account
        var query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)

        if (cursor.count != 0) {
            query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}  WHERE ${DBHandler.ACCOUNT_HOLDERS_ADMIN_COL} = 'Chairperson'"
            db = dbHandler.readableDatabase
            cursor = db.rawQuery(query, null)
                if (cursor.count == 0) {
                    val duplicateAdmin = android.app.AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_warning)
                            .setTitle("Security Warning")
                            .setMessage("Without Chairperson account, " +
                                    "anyone can login. For security PIN, create a Chairperson account")
                            .setNegativeButton("Ok") { _: DialogInterface, _: Int ->
                            }
                    duplicateAdmin.show()
                }
        }
        cursor.close()
    }



    private fun availableCash(){
        var sharePayment = 0.0
        var loanPayout = 0.0
        var loanRepayment = 0.0
        var chargePayment = 0.0

        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        val db = Home.dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            sharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            loanPayout += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
            loanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            chargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
        }

        val availableCash ="Avail Balance: ${(sharePayment + loanRepayment + chargePayment - loanPayout).roundToLong()*10.0/10.0}"
        tvCashAvailable.text = availableCash
    }



}