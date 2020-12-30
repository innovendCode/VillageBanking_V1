package com.villagebanking

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_holders.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.view.*
import kotlinx.android.synthetic.main.dialog_insert_password.view.*
import kotlinx.android.synthetic.main.dialog_password.view.*
import kotlinx.android.synthetic.main.main_row_layout.*
import java.util.*

class AccountHolders: AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_holders)

        dbHandler = DBHandler(this, null, null, 1)

        viewAccountHolders()
        securityWarning()

        val actionBar = supportActionBar
        actionBar!!.title = "Accounts"

        checkIfCurrentMonth()
        //checkPayments()
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
            R.id.delAllAccountHolders -> {
                dbHandler.deleteAll(this)
                viewAccountHolders()
            }
            R.id.ViewAll -> {
                viewAccountHolders()
            }
            R.id.ViewAdmins -> {
                viewAccountAdmins()
            }
            R.id.searchName -> {




            }
        }
        return true
    }



    override fun onResume() {
        super.onResume()
        //checkPayments()
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


                val contentValues = ContentValues()
                db = dbHandler.writableDatabase
                contentValues.put(DBHandler.ACCOUNT_HOLDERS_ARREARS_COL, "")
                db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, null, null)

                    AlertDialog.Builder(this)
                            .setTitle("New Month $transactionMonth}")
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
    }



    private fun viewAccountAdmins(){
        val accountAdminList = dbHandler.getAccountAdmins(this)
        val adapter = CustomAdapter(this, accountAdminList)
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



    @SuppressLint("Recycle")
    fun securityWarning(){
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
    }








}