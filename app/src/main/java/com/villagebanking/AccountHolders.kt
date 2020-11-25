package com.villagebanking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_holders.*
import kotlinx.android.synthetic.main.dialog_add_account_holder.view.*

class AccountHolders: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_holders)

        Home.dbHandler = DBHandler(this, null, null, 1)

        viewAccountHolders()

        val actionBar = supportActionBar
        actionBar!!.title = "Account Holders"
        actionBar.setDisplayHomeAsUpEnabled(true)


    }



    @SuppressLint("WrongConstant")
    private fun viewAccountHolders(){
        val accountHoldersList = Home.dbHandler.getAccountHolders(this)
        val adapter = CustomAdapter(this, accountHoldersList)
        val rv: RecyclerView = recyclerView
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager
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

                val role = arrayOf(
                        "SELECT ROLE...",
                        "Chairperson",
                        "Vice Chairperson",
                        "Secretary",
                        "Money Counter 1",
                        "Money Counter 2",
                        "Account Holder"
                )
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, role)
                addAccountHolderDialogLayout.spAdministrators.adapter = arrayAdapter


                addAccountHolderDialogLayout.btnCancel.setOnClickListener {
                    showAddAccountHolderDialog.dismiss()
                }


            }
        }





        return true
    }



}