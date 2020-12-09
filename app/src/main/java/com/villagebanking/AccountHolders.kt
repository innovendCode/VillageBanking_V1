package com.villagebanking

import android.annotation.SuppressLint
import android.content.Context
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

class AccountHolders: AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_holders)

        dbHandler = DBHandler(this, null, null, 1)

        viewAccountHolders()

        val actionBar = supportActionBar
        actionBar!!.title = "Accounts"
        actionBar.setDisplayHomeAsUpEnabled(true)
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
                dbHandler.delAllAccountHolders(this)
                viewAccountHolders()
            }
            R.id.ViewAll -> {
                viewAccountHolders()
            }
            R.id.ViewAdmins -> {
                viewAccountAdmins()
            }
        }
        return true
    }



    override fun onResume() {
        super.onResume()
        viewAccountHolders()
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
        val admin = arrayOf(
                "SELECT ROLE...",
                "Chairperson",
                "Vice Chairperson",
                "Secretary",
                "Money Counter 1",
                "Money Counter 2",
                "Account Holder"
        )

        val addAccountHolderDialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_add_account_holder, null)

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

                                                    if(insertPinDialogLayout.etPasswordRepeat.text.isEmpty()){
                                                        Toast.makeText(yContext, "Please type repeat PIN", Toast.LENGTH_SHORT).show()
                                                        return@setOnClickListener}

                                                    if(insertPinDialogLayout.etPinHint.text.isEmpty()){
                                                        Toast.makeText(yContext, "Please type hint", Toast.LENGTH_SHORT).show()
                                                        return@setOnClickListener}

                                                    if(insertPinDialogLayout.etInsertPIN.text.toString() != insertPinDialogLayout.etPasswordRepeat.text.toString()){
                                                        Toast.makeText(yContext, "PIN and repeat PIN do not match. Re-type", Toast.LENGTH_LONG).show()
                                                        return@setOnClickListener}

                                                    if(insertPinDialogLayout.etInsertPIN.text.length < 4){
                                                        Toast.makeText(yContext, "PIN should be 4 digits", Toast.LENGTH_LONG).show()
                                                        return@setOnClickListener}

                                                    val accountHolderModel = AccountHolderModel()
                                                    accountHolderModel.accountHoldersName = addAccountHolderDialogLayout.etFullNames.text.toString()
                                                    accountHolderModel.accountHoldersAdmin = selectedAdmin
                                                    accountHolderModel.accountHolderContact = addAccountHolderDialogLayout.etContactNo.text.toString()
                                                    accountHolderModel.accountHolderBankInfo = addAccountHolderDialogLayout.etAccountInfo.text.toString()
                                                    accountHolderModel.accountHolderPin = insertPinDialogLayout.etInsertPIN.text.toString()
                                                    accountHolderModel.accountHolderPinHint = insertPinDialogLayout.etPinHint.text.toString()
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
                                val accountHolderModel = AccountHolderModel()
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



}