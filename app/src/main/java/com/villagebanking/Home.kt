package com.villagebanking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_holders.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home.*

class Home: AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val actionBar = supportActionBar
        actionBar!!.title = "Home"

       dbHandler = DBHandler(this, null, null, 1)

        getNoAccounts()

    }


    override fun onRestart() {
        getNoAccounts()
        super.onRestart()
    }



    private fun getNoAccounts(){
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        val tvNoAccountHolders: TextView = tvNoAccountHolders
        tvNoAccountHolders.text = "- Account Holders: ${cursor.count.toString()}"
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
            R.id.accountHolders ->{
               val intent = Intent(this,AccountHolders::class.java)
               startActivity(intent)
            }
        }



        return true
    }
}