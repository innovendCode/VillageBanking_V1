package com.villagebanking

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.dialog_add_account_holder.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.view.*
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
        actionBar!!.title = "Account Holders"
        actionBar.setDisplayHomeAsUpEnabled(true)

    }



    private fun viewAccountHolders(){
        val accountHoldersList = dbHandler.getAccountHolders(this)
        val adapter = CustomAdapter(this, accountHoldersList)
        val rv: RecyclerView = recyclerView
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager
        rv.adapter = adapter
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
        val addAccountHolderDialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_add_account_holder, null)

        when(item.itemId){
            R.id.addAccountHolder ->{
                val addAccountHolderDialog = AlertDialog.Builder(this)
                        .setView(addAccountHolderDialogLayout)
                        .setTitle("Add New Member")
                val showAddAccountHolderDialog = addAccountHolderDialog.show()
                addAccountHolderDialogLayout.etName.requestFocus()

                val admin = arrayOf(
                        "SELECT ROLE...",
                        "Chairperson",
                        "Vice Chairperson",
                        "Secretary",
                        "Money Counter 1",
                        "Money Counter 2",
                        "Account Holder"
                )
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, admin)
                addAccountHolderDialogLayout.spAdministrators.adapter = arrayAdapter

                var selectedAdmin = ""

                addAccountHolderDialogLayout.spAdministrators.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedAdmin = admin[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }


                addAccountHolderDialogLayout.btnCancel.setOnClickListener {
                    showAddAccountHolderDialog.dismiss()
                }

                addAccountHolderDialogLayout.btnAddAccountHolder.setOnClickListener {
                    if (addAccountHolderDialogLayout.etName.text.isEmpty()) {
                        Toast.makeText(this, "Please type Full Name", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener}

                    if (addAccountHolderDialogLayout.etContactNo.text.isEmpty()) {
                        Toast.makeText(this, "Please enter contact number", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener}

                    if (addAccountHolderDialogLayout.etAccountInfo.text.isEmpty()) {
                        Toast.makeText(this, "Please enter account or mobile banking info", Toast.LENGTH_LONG).show()
                        return@setOnClickListener}

                    if (selectedAdmin == "SELECT ROLE...") {
                        Toast.makeText(this, "Please select membership role", Toast.LENGTH_LONG).show()
                        return@setOnClickListener}


                    val accountHolderModel = AccountHolderModel()
                    accountHolderModel.accountHoldersName = addAccountHolderDialogLayout.etName.text.toString()
                    accountHolderModel.accountHoldersAdmin = selectedAdmin
                    accountHolderModel.accountHolderContact = addAccountHolderDialogLayout.etContactNo.text.toString()
                    accountHolderModel.accountHolderBankInfo = addAccountHolderDialogLayout.etAccountInfo.text.toString()
                    dbHandler.addAccountHolder(this, accountHolderModel)
                    viewAccountHolders()

                    addAccountHolderDialogLayout.etName.text.clear()
                    addAccountHolderDialogLayout.etContactNo.text.clear()
                    addAccountHolderDialogLayout.etAccountInfo.text.clear()

                    if(selectedAdmin == "Chairperson" ){
                        Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show()
                    }

                }
            }
            R.id.delAllAccountHolders ->{
                val deleteAllAccounts = AlertDialog.Builder(this)
                        .setTitle("Are you sure?")
                        .setMessage("Delete all members and accounts")
                        .setPositiveButton("Yes") { _: DialogInterface, _: Int ->




                            dbHandler.delAllAccountHolders(this)
                            Toast.makeText(this, "All Accounts Deleted", Toast.LENGTH_SHORT).show()
                            viewAccountHolders()



                        }
                        .setNegativeButton("No") {_: DialogInterface, _: Int ->}
                deleteAllAccounts.show()
            }
            R.id.ViewAll ->{
                viewAccountHolders()
            }

        }





        return true
    }

fun deleteAccountsPermission(){


}


}